package application.controller;

import application.action.Action;
import application.action.error;
import application.action.move;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientHandler {
    public Socket socket = null;
    OutputStream outToServer = null;
    InputStream is = null;
    Action actions;
    String username;
    public final Boolean myTurn;

    public Boolean getMyTurn() {
        return myTurn;
    }

    public ClientHandler(Boolean myTurn, String username) {
        this.myTurn = myTurn;
        this.username = username;
        try {
            //你的ip，你的端口
            socket = new Socket("10.26.142.228", 5612);
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
                        String info="";
                        try{
                            info = in.readUTF();
                        }catch (Exception e){
//                            send(new error("error","server error"));
                            alertMsg("server error");
                            break;
//                            System.exit(0);
                        }

                        if (!info.equals("")){
                            String type = info.split(",")[0];
                            if (type.equals("move")){
                                System.out.println("info : "+info);
                                Action action = new move(info);
                                actions = action;
                                if (action.getName().equals("move")&&!action.getStatus().equals("going")){
                                    break;
                                }
                            }else if(type.equals("error")){
                                System.out.println("info : "+info);
                                Action action = new error(info);
                                actions = action;
                                if (action.getName().equals("move")&&!action.getStatus().equals("going")){
                                    break;
                                }
                            }

                        }
                        Thread.sleep(1);
                    }
                } catch (Exception e) {
//                    e.printStackTrace();

                }

            });
            td.start();
        } catch (Exception e) {
//            e.printStackTrace();
            alertMsg("Not Find Server!");

        }
    }

    public void setUsername(String name){
        this.username = name;
    }
    public String getUsername(){
        return username;
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
            if (actions.getName().equals("move")){
                Action t = new move(actions.toString());
//            System.out.println(t);
                actions = null;
                return t;
            }else if (actions.getName().equals("error")){
                Action t = new error(actions.toString());
                actions = null;
                return t;
            }
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
    private void alertMsg(String s) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("message");
            alert.setHeaderText("detail:");
            alert.setContentText(s);
            alert.showAndWait();
        });
    }
}
