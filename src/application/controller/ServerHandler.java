package application.controller;

import application.action.Action;
import application.action.move;

import java.io.*;
import java.net.Socket;
import java.util.Vector;

public class ServerHandler extends Thread {
    public static Vector<ServerHandler> allSocket = new Vector<>();

    private Socket socket = null;
    private Socket socket2 = null;

    public ServerHandler(Socket socket, Socket socket2) {
        try {
            this.socket = socket;
            this.socket2 = socket2;
            allSocket.add(this);
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

//            Action Play2Initial = new move("move,0,0,0,0,0,0,0,0,0,true,false,false,false,false,false,false,false,false,false");
//            DataOutputStream setPlay2 = new DataOutputStream(outputStream2);
//            setPlay2.writeUTF(Play2Initial.toString());
            Boolean initialTurn = false;

            while(true){
                if (socket.isClosed() || socket2.isClosed())
                    break;
                String s;
                if (!initialTurn){
                    DataInputStream in = new DataInputStream(inputStream);
                    s=in.readUTF();
                }else {
                    DataInputStream in2 = new DataInputStream(inputStream2);
                    s=in2.readUTF();
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
                if (!action.getStatus().equals("going")){
                    closeServerHandler();
                    break;
                }

            }
        } catch(Exception e){
            e.printStackTrace();
        }

    }

    public void closeServerHandler(){
        try{
            socket.close();
            socket2.close();
            System.out.println(""+socket.getInetAddress()+" "+socket.getPort()+" and "+socket2.getInetAddress()+" "+socket.getPort()+" game over!");
        } catch (IOException e){

        }
    }
}
