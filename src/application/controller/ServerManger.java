package application.controller;

import application.Server;
import application.action.Action;
import application.action.log;
import application.action.move;
import application.body.account;
import com.mchange.v2.c3p0.ComboPooledDataSource;
//import application.view.GUI;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

public class ServerManger extends Thread {
    database db = new database();
    public static Vector<Player> LinePlayer = new Vector<>();
    public static Vector<Player> CirclePlayer = new Vector<>();
//    public Connection con ;

    private Socket socket = null;

    public ServerManger(Socket socket) {
        try {
            this.socket = socket;
//            this.con = db.getCon();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ServerManger() throws SQLException {
//        this.con = db.getCon();
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
    public Boolean checkUser(String username, String password) throws SQLException {
        Connection con = db.getCon();

        try{
        PreparedStatement checkPass = con.prepareStatement("select username, password, circle_match, circle_win, line_match, line_win from account ");
        ResultSet resultSet = checkPass.executeQuery();
        ArrayList<account> allAccount = new ArrayList<>();
        while (resultSet.next()){
            account a =new account(resultSet.getString("username"),resultSet.getString("password"),
                    resultSet.getInt("circle_match"),
                    resultSet.getInt("circle_win"),
                    resultSet.getInt("line_match"),
                    resultSet.getInt("line_win"));
            allAccount.add(a);
        }
        for (int i = 0; i < allAccount.size(); i++) {
            if (allAccount.get(i).getUsername().equals(username)&&allAccount.get(i).getPassword().equals(password))
                return true;
        }
        return false;
        } catch (SQLException e){
            return false;
        }finally {
            con.close();
        }
    }


    public String insertUser(String username, String password) throws SQLException {
        Connection con = db.getCon();
        try{
            PreparedStatement insertU = con.prepareStatement("insert into account (username, password, circle_match, circle_win, line_match, line_win) values (?, ?, 0,0,0,0)");
            insertU.setString(1,username);
            insertU.setString(2,password);
            insertU.execute();
            return "success";
        } catch (SQLException e){
            e.printStackTrace();
            return "fail";
        }finally {
            con.close();
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
    class database{

        private ComboPooledDataSource dbPool = new ComboPooledDataSource();
        private String host = "localhost";
        private String dbname = "cs209";
        private String user = "checker";
        private String pwd = "123456";
        private String port = "5432";

        public database(){
            openDB();
        }

        public void openDB() {

            try {
                dbPool.setDriverClass("org.postgresql.Driver");
            } catch (Exception e) {
                System.err.println("Cannot find the PostgreSQL driver. Check CLASSPATH.");
                System.exit(1);
            }
            String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbname;
            dbPool.setUser(user);
            dbPool.setPassword(pwd);
            dbPool.setJdbcUrl(url);

            dbPool.setInitialPoolSize(8);
            dbPool.setMaxPoolSize(12);
//            System.out.println("done");
        }


        public Connection getCon() throws SQLException {
            return this.dbPool.getConnection();
        }
    }

}
