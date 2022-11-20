package application.controller;

import application.action.Action;
import application.action.error;
import application.action.move;
import application.body.account;
import com.csvreader.CsvReader;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Vector;

public class ServerHandler extends Thread {
    public static Vector<ServerHandler> allSocket = new Vector<>();

    private Socket socket = null;
    private Socket socket2 = null;
    public static ArrayList<account> accounts = new ArrayList<>();


    public ServerHandler(Socket socket, Socket socket2) {
        try {
            this.socket = socket;
            this.socket2 = socket2;
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public ServerHandler() {
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = null;
            OutputStream outputStream = null;
            InputStream inputStream2 = null;
            OutputStream outputStream2 = null;
            //获取客户端信息
            inputStream = socket.getInputStream();
            //回复客户端
            outputStream = socket.getOutputStream();
            inputStream2 = socket2.getInputStream();
            //回复客户端
            outputStream2 = socket2.getOutputStream();

            Boolean initialTurn = false;
            OutputStream finalOutputStream2 = outputStream2;
            OutputStream finalOutputStream = outputStream;
            new Thread(() -> {
                try {
                    while (true){
                        if (socket.isClosed() || socket2.isClosed()) {
//                    if (socket.isClosed()){
//                        DataOutputStream out2 = new DataOutputStream(outputStream2);
//                        Action err = new error("error","another player close");
//                        out2.writeUTF(err.toString());
//                    }else {
//                        DataOutputStream out = new DataOutputStream(outputStream);
//                        Action err = new error("error","another player close");
//                        out.writeUTF(err.toString());
//                    }
                            break;
                        }
                        try{
                            DataOutputStream out2 = new DataOutputStream(finalOutputStream2);
//                            Action err = new error("","");
                            out2.writeUTF("");
                        }catch (Exception e){
                            DataOutputStream out = new DataOutputStream(finalOutputStream);
                            Action err = new error("error", "another player close");
                            out.writeUTF(err.toString());
                            break;
                        }
                        try{
                            DataOutputStream out = new DataOutputStream(finalOutputStream);
//                            Action err = new error("","");
                            out.writeUTF("");
                        }catch (Exception e){
                            DataOutputStream out2 = new DataOutputStream(finalOutputStream2);
                            Action err = new error("error", "another player close");
                            out2.writeUTF(err.toString());
                            break;
                        }
                        Thread.sleep(100);
                    }
                }catch (Exception e){
                    System.out.println("new thread error");
                }
            }).start();
//            while (true) {
//                if (socket.isClosed())
//                    break;
//                String s;
//                DataInputStream in = new DataInputStream(inputStream);
//                s = in.readUTF();
//                DataOutputStream out = new DataOutputStream(outputStream);
//                if (s.split(",")[0].equals("log")){
//                    log log = new log(s);
//                    if (log.getType().equals("signin")){
//                        if (checkUser(log.getName(),log.getUserName())){
//                            log.status = "success";
//                            out.writeUTF(log.toString());
//                        }else {
//                            log.status = "fail";
//                            out.writeUTF(log.toString());
//                        }
//                    }else if (log.getType().equals("signup")){
//                        System.out.println(insertUser(log.getUserName(), log.getPassword()));
//                    }
//                    break;
//                }
//                Thread.sleep(10);
//            }
            while (true) {
                if (socket.isClosed() || socket2.isClosed()) {
//                    if (socket.isClosed()){
//                        DataOutputStream out2 = new DataOutputStream(outputStream2);
//                        Action err = new error("error","another player close");
//                        out2.writeUTF(err.toString());
//                    }else {
//                        DataOutputStream out = new DataOutputStream(outputStream);
//                        Action err = new error("error","another player close");
//                        out.writeUTF(err.toString());
//                    }
                    break;
                }
                String s;
                if (!initialTurn) {
                    try {
                        DataInputStream in = new DataInputStream(inputStream);
                        s = in.readUTF();
                    } catch (Exception e) {
//                        DataOutputStream out2 = new DataOutputStream(outputStream2);
//                        Action err = new error("error","another player close");
//                        out2.writeUTF(err.toString());
                        System.out.println("error 1");
                        break;
                    }

                } else {
                    try{
                        DataInputStream in2 = new DataInputStream(inputStream2);
                        s = in2.readUTF();
                    }catch (Exception e){
//                        DataOutputStream out = new DataOutputStream(outputStream);
//                        Action err = new error("error", "another player close");
//                        out.writeUTF(err.toString());
                        System.out.println("error 2");
                        break;
                    }
                }
                Action action = new move(s);
                action.setTurn();
                initialTurn = action.getTurn();
//                System.out.println(action);
                DataOutputStream out = new DataOutputStream(outputStream);
                out.writeUTF(action.toString());
                DataOutputStream out2 = new DataOutputStream(outputStream2);
                out2.writeUTF(action.toString());
                Thread.sleep(1);
                System.out.println(initialTurn);
                System.out.println(action.getStatus());
                if (!action.getStatus().equals("going")) {
                    closeServerHandler();
                    break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void closeServerHandler() {
        try {
            socket.close();
            socket2.close();
            System.out.println("" + socket.getInetAddress() + " " + socket.getPort() + " and " + socket2.getInetAddress() + " " + socket.getPort() + " game over!");
        } catch (IOException e) {

        }
    }

    public Boolean checkUser(String username, String password) {
        getAccount();
        try {
            for (int i = 0; i < accounts.size(); i++) {
                if (accounts.get(i).getUsername().equals(username) && accounts.get(i).getPassword().equals(password)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }


    public String insertUser(String username, String password) {
        try {
            accounts.add(new account(username, password, 0, 0, 0, 0));
            writeBackAccount();
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        } finally {
        }
    }

    class Player {
        public String username;
        public Socket socket;

        public Player(String username, Socket socket) {
            this.username = username;
            this.socket = socket;
        }
    }

    public synchronized void getAccount() {
        try {
            String path = String.valueOf(getClass().getClassLoader().getResource("application/controller/account.csv").toURI());
            CsvReader infile = new CsvReader("application/controller/account.csv", ',', Charset.forName("UTF-8"));
            String[] values;
            infile.readHeaders();
            while (infile.readRecord()) {
                values = infile.getValues();
                accounts.add(new account(values[0], values[1], Integer.parseInt(values[2]),
                        Integer.parseInt(values[3]),
                        Integer.parseInt(values[4]),
                        Integer.parseInt(values[5])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void writeBackAccount() {
        try {
            String path = String.valueOf(getClass().getClassLoader().getResource("application/controller/account.csv").toURI());
            BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("application/controller/account.csv"), "UTF-8"));
            w.write("username,password,circle_match,circle_win,line_match,line_win");
            w.newLine();
            for (account a : accounts) {
                w.write(a.getAccount());
                w.newLine();
            }
            w.close();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
