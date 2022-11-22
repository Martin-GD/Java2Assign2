package application;

import application.controller.ServerHandler;
import application.controller.ServerHandler_can_choose;
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
import java.util.ArrayList;


public class Server_can_choose {
  private static ArrayList<Socket> sockets = new ArrayList<>();

  public static void main(String[] args) throws IOException {
    try {

      // 创建服务端socket
      ServerSocket serverSocket = new ServerSocket(5612);
      System.out.println("服务器端启动!!");
      // 创建客户端socket
      Socket socket, socket2;

      //循环监听等待客户端的连接
      while (true) {
        // 监听客户端
        socket = serverSocket.accept();
        sockets.add(socket);
        Socket t = socket;
        Thread td = new Thread(() -> {
          try {
            InputStream inputStream = null;
            OutputStream outputStream = null;
            inputStream = t.getInputStream();
            outputStream = t.getOutputStream();
            OutputStream finalOutputStream = outputStream;
            new Thread(() -> {
              try {
                while (true) {
//                                    System.out.println("6");
                  DataOutputStream out = new DataOutputStream(finalOutputStream);
                  out.writeUTF("?");
                  Thread.sleep(1000);
                }
              } catch (Exception e) {
                sockets.remove(t);
              }
            }).start();

            DataOutputStream out = new DataOutputStream(finalOutputStream);
            out.writeUTF("??, " + (sockets.size() - 1));
            DataInputStream in = new DataInputStream(inputStream);
            String s = in.readUTF();
            if (Integer.parseInt(s) >= 0) {
              Socket another = sockets.get(Integer.parseInt(s));
              ServerHandler_can_choose thread = new ServerHandler_can_choose(t, another);
              System.out.println("start one game.");
              thread.start();
              sockets.remove(t);
              sockets.remove(another);
            }
            System.out.println("end thread");

          } catch (Exception e) {
            e.printStackTrace();
          }
        });
        td.start();


        InetAddress address = socket.getInetAddress();
        System.out.println("当前客户端的IP：" + sockets.size() + "  " + address.getHostAddress());
      }
    } catch (Exception e) {
      // TODO: handle exception
      e.printStackTrace();
    }
  }

  public void sendAll() {
    for (int i = 0; i < sockets.size(); i++) {
      Socket t = sockets.get(i);
      new Thread(() -> {
        try {
          InputStream inputStream = null;
          OutputStream outputStream = null;
          inputStream = t.getInputStream();
          outputStream = t.getOutputStream();
          OutputStream finalOutputStream = outputStream;

          DataOutputStream out = new DataOutputStream(finalOutputStream);
          out.writeUTF("??, " + (sockets.size() - 1));
          DataInputStream in = new DataInputStream(inputStream);
          String s = in.readUTF();
          if (Integer.parseInt(s) >= 0) {
            Socket another = sockets.get(Integer.parseInt(s));
            ServerHandler_can_choose thread = new ServerHandler_can_choose(t, another);
            thread.start();
            sockets.remove(t);
            sockets.remove(another);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }).start();

    }
  }

}
