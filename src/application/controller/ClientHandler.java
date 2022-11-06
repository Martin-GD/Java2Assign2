package application.controller;

import application.action.Action;
import application.action.move;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientHandler {
    public Socket socket = null;
    OutputStream outToServer = null;
    InputStream is = null;
    Action actions;
    private final Boolean myTurn;

    public Boolean getMyTurn() {
        return myTurn;
    }

    public ClientHandler(Boolean myTurn) {
        this.myTurn = myTurn;
        try {
            //你的ip，你的端口
            socket = new Socket("localhost", 5612);
            outToServer = socket.getOutputStream();


            Thread td = new Thread(() -> {
                try {
                    System.out.println("接受线程开启");
                    //获取输入流
                    is = socket.getInputStream();
                    byte[] inputBytes = new byte[1024];

                    int len;
                    //监听输入流,持续接收
                    while (true) {
                        DataInputStream in = new DataInputStream(is);
//                        System.out.println("Server says " + in.readUTF());
                        String info = in.readUTF();

                        if (!info.equals("")){
                            System.out.println("info : "+info);
                            Action action = new move(info);
                            actions = action;
//                            System.out.println(actions);
//                            System.out.println("receive");
                        }
                        Thread.sleep(1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
            td.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void send(Action action) {
        try {
            DataOutputStream out = new DataOutputStream(outToServer);

            out.writeUTF(action.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public Action getActions(){
        if (actions!=null){
            Action t = new move(actions.toString());
//            System.out.println(t);
            actions = null;
            return t;
        }
        return null;
    }

    public void closeClient(){
        try {
            if (outToServer!=null)
                outToServer.close();
            if (is!=null)
                is.close();
            socket.close();
        } catch (IOException e){
            e.printStackTrace();
        }


    }
}
