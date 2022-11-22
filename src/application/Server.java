package application;

import application.controller.ServerHandler;
import application.controller.ServerManger;
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
    public static void main(String[] args) throws IOException {
        try {

            // 创建服务端socket
            ServerSocket serverSocket = new ServerSocket(5612);
            System.out.println("服务器端启动!!");
            // 创建客户端socket
            Socket socket, socket2;

            //循环监听等待客户端的连接
            while (true) {
                InputStream inputStream = null;
                OutputStream outputStream = null;
                InputStream inputStream2 = null;
                OutputStream outputStream2 = null;
                // 监听客户端
                socket = serverSocket.accept();
                System.out.println("one client waiting");
                inputStream = socket.getInputStream();
                //回复客户端
                outputStream = socket.getOutputStream();
                try {
                    DataOutputStream out = new DataOutputStream(outputStream);
                    out.writeUTF("wait");
                } catch (Exception e) {

                }
                socket2 = serverSocket.accept();
                //获取客户端信息
                inputStream2 = socket2.getInputStream();
                //回复客户端
                outputStream2 = socket2.getOutputStream();
                OutputStream finalOutputStream2 = outputStream2;
                OutputStream finalOutputStream = outputStream;
                try {
                    DataOutputStream out = new DataOutputStream(finalOutputStream);
                    out.writeUTF("false");
                    Thread.sleep(500);
                } catch (Exception e) {
                    socket = socket2;
                    socket2 = serverSocket.accept();
                    inputStream = socket.getInputStream();
                    //回复客户端
                    outputStream = socket.getOutputStream();
                    inputStream2 = socket2.getInputStream();
                    //回复客户端
                    outputStream2 = socket2.getOutputStream();
                    DataOutputStream out = new DataOutputStream(outputStream);
                    out.writeUTF("false");
                    Thread.sleep(500);
                    DataOutputStream out2 = new DataOutputStream(outputStream2);
                    out2.writeUTF("true");
                }
                try {
                    DataOutputStream out2 = new DataOutputStream(finalOutputStream2);
                    out2.writeUTF("true");
                } catch (Exception e) {

                }


                ServerHandler thread = new ServerHandler(socket, socket2);
                thread.start();

                InetAddress address = socket.getInetAddress();
                System.out.println("当前客户端的IP：" + address.getHostAddress());
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }

}
