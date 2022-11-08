package application;

import application.controller.ServerHandler;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class Server {
    database db = new database();
    public static void main(String[] args) throws IOException {
        try {

            // 创建服务端socket
            ServerSocket serverSocket = new ServerSocket(5612);
            System.out.println("服务器端启动!!");
            // 创建客户端socket
            Socket socket, socket2;

            //循环监听等待客户端的连接
            while(true){
                // 监听客户端
                socket = serverSocket.accept();
                System.out.println("one client waiting");
                socket2 = serverSocket.accept();


                ServerHandler thread = new ServerHandler(socket, socket2);
                thread.start();

                InetAddress address=socket.getInetAddress();
                System.out.println("当前客户端的IP："+address.getHostAddress());
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }
    class database{

        private ComboPooledDataSource dbPool = new ComboPooledDataSource();
        private String host = "localhost";
        private String dbname = "cs309";
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
        }

        public void changeConnectionToManager(String password) {
            //pwd=password_m
            this.user = "manager";
            this.pwd = password;
            dbPool.close();
            dbPool = new ComboPooledDataSource();
            openDB();
        }

        public void changeConnectionBack(String password) {
            //pwd=123456
            user = "test";
            pwd = password;
            dbPool.close();
            dbPool = new ComboPooledDataSource();
            openDB();
        }

        public void createRole(String role_name) throws SQLException {
            //do block for creating roles do not exist
            Connection con = dbPool.getConnection();

            String sql="DO " +
                    "                $do$ " +
                    "                BEGIN " +
                    "                   IF EXISTS ( " +
                    "                      SELECT FROM pg_catalog.pg_roles " +
                    "                      WHERE  rolname = ";
            sql+="'"+role_name;
            sql+="')then ELSE create role \""+role_name+"\"; end if; end $do$;";
            try {

                PreparedStatement stmt = con.prepareStatement(sql);
                stmt.execute();
                grantUsages(role_name);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        //一定要在test user下切换成staff不然会查不到
        public void grantUsages(String userName) throws SQLException {
            Connection con = dbPool.getConnection();

            String sqlGrant = "grant normal_staffs to ";
            String sql = "set role = ";
            try {
                sql+="\""+userName+"\"";
                PreparedStatement stmt = con.prepareStatement(sql);
                sqlGrant+="\""+userName+"\"";
                PreparedStatement stmtGrant = con.prepareStatement(sqlGrant);
                stmtGrant.execute();
                stmt.execute();

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

        }



        public Connection getCon() throws SQLException {
            return this.dbPool.getConnection();
        }
    }
}
