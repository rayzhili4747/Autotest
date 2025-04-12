package com.forex.autotest.quotesimulator.openexclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class QuoteMaker extends Thread {
    private Socket socket;
    private PrintWriter pw;
    private BufferedReader br;
    private String returnMsg;


    public String getMsg(){return returnMsg;}

    public QuoteMaker(String host, int port) throws IOException {
        super();
        this.socket=new Socket(host,port);
        //start connection
        this.start();
    }

    //send message function
    public void sendMsg(String msg){
        //send msg via socket
        try {
            if(pw==null){
                pw=new PrintWriter(socket.getOutputStream());
            }
            //output message
            System.out.println("sent: "+msg);
            pw.println(msg);
            pw.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //close socket
    public void disConnect(){
        if(socket!=null){
            try {
              //  this.stop();
                this.socket.close();

            } catch (Exception e) {
            }
        }
    }


    //receive return msg
    public void run() {

        try {


                if (br == null) {
                    br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                }

                String msg = null;

                while ((msg = br.readLine()) != null) {
                    this.returnMsg = msg;
                    System.out.println("receive: "+msg);

                }


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public String getIp(){
        return this.socket.getInetAddress().getHostAddress();
    }

}