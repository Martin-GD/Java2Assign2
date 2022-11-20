package application.controller;

import application.action.log;
import application.body.account;
import com.csvreader.CsvReader;
//import application.view.GUI;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

public class ServerAccount extends Thread {
    public static Vector<Player> LinePlayer = new Vector<>();
    public static Vector<Player> CirclePlayer = new Vector<>();
    public static ArrayList<account> accounts = new ArrayList<>();

    private Socket socket = null;

    public ServerAccount(Socket socket) {
        try {
            this.socket = socket;
            getAccount();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ServerAccount() throws SQLException {
        getAccount();
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = null;
            OutputStream outputStream = null;

            //获取客户端信息
            inputStream = socket.getInputStream();
            //回复客户端
            outputStream = socket.getOutputStream();

            while (true) {
                if (socket.isClosed())
                    break;
                String s;
                DataInputStream in = new DataInputStream(inputStream);
                s = in.readUTF();
                DataOutputStream out = new DataOutputStream(outputStream);
                if (s.split(",")[0].equals("log")){
                    log log = new log(s);
                    if (log.getType().equals("signin")){
                        if (checkUser(log.getName(),log.getUserName())){
                            log.status = "success";
                            out.writeUTF(log.toString());
                            if (log.identity.equals("line"))
                                LinePlayer.add(new Player(log.getUserName(), socket));
                            else
                                CirclePlayer.add(new Player(log.getUserName(), socket));
                        }else {
                            log.status = "fail";
                            out.writeUTF(log.toString());
                        }
                    }else if (log.getType().equals("signup")){
                        System.out.println(insertUser(log.getUserName(), log.getPassword()));
                        if (log.identity.equals("line"))
                            LinePlayer.add(new Player(log.getUserName(), socket));
                        else
                            CirclePlayer.add(new Player(log.getUserName(), socket));
                    }
                    break;
                }
                Thread.sleep(10);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void closeServerHandler() {
        try {
            socket.close();
            System.out.println("" + socket.getInetAddress() + " " + socket.getPort() + " and " + " " + socket.getPort() + " game over!");
        } catch (IOException e) {

        }
    }
    public Boolean checkUser(String username, String password) {
        getAccount();
        try{
            for (int i = 0; i < accounts.size(); i++) {
                if (accounts.get(i).getUsername().equals(username)&&accounts.get(i).getPassword().equals(password)){
                    return true;
                }
            }
            return false;
        } catch (Exception e){
            return false;
        }
    }
    public String getOne(String username){
        getAccount();
        String ans = "";
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getUsername().equals(username)){
                ans = ans+"username: "+accounts.get(i).getUsername()+"\n";
                ans = ans+"circle_match: "+accounts.get(i).circle_match+"\n";
                ans = ans+"circle_win: "+accounts.get(i).circle_win+"\n";
                ans = ans+"line_match: "+accounts.get(i).line_match+"\n";
                ans = ans+"line_win: "+accounts.get(i).line_win+"\n";
                break;
            }
        }
        return ans;
    }
    public synchronized void changeData(String username, Boolean turn, Boolean status){
        System.out.println("changeData"+username+" "+turn+" "+status);

        getAccount();
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getUsername().equals(username)){
                if (turn){ // Circle
                    if (status){
                        accounts.get(i).circle_win += 1;
                    }
//                    accounts.get(i).circle_match += 1;
                }else {
                    if (status){
                        accounts.get(i).line_win += 1;
                    }
//                    accounts.get(i).line_match += 1;
                }
                break;
            }
        }
        writeBackAccount();
    }
    public synchronized void addMatch(String username, Boolean turn){
        System.out.println("addMatch");

        getAccount();
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getUsername().equals(username)){
                if (turn){ // Circle
                    accounts.get(i).circle_match += 1;
                }else {
                    accounts.get(i).line_match += 1;
                }
                break;
            }
        }
        writeBackAccount();
    }

    public String insertUser(String username, String password) {
        try{
            System.out.println("Insert");

            getAccount();
            accounts.add(new account(username,password,0,0,0,0));
            writeBackAccount();
            return "success";
        } catch (Exception e){
            e.printStackTrace();
            return "fail";
        }finally {
        }
    }
    class Player{
        public String username;
        public Socket socket;

        public Player(String username, Socket socket) {
            this.username = username;
            this.socket = socket;
        }
    }

    public synchronized void getAccount(){
        try{
//            System.out.println("getAccount");
            accounts.clear();
            CsvReader infile = new CsvReader("account.csv",',', Charset.forName("UTF-8"));
            String[] values;
            infile.readHeaders();
            while (infile.readRecord()){
                values=infile.getValues();
                accounts.add(new account(values[0],values[1],Integer.parseInt(values[2]),
                        Integer.parseInt(values[3]),
                        Integer.parseInt(values[4]),
                        Integer.parseInt(values[5])));
//                System.out.println(Arrays.toString(values));
            }
//            for (int i = 0; i < accounts.size(); i++) {
//                System.out.println(accounts.get(i).getAccount());
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public synchronized void writeBackAccount(){
        try {
//            String path = String.valueOf(getClass().getClassLoader().getResource("application/controller/account.csv").toURI());
            BufferedWriter w = new BufferedWriter (new OutputStreamWriter(new FileOutputStream("account.csv"),"UTF-8"));
            w.write("username,password,circle_match,circle_win,line_match,line_win");
            w.newLine();
            for (account a:accounts){
                w.write(a.getAccount());
                w.newLine();
            }
            w.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
