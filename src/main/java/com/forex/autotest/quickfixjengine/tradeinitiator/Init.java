package com.forex.autotest.quickfixjengine.tradeinitiator;

import quickfix.*;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;


public class Init {
    private SocketInitiator socketInitiator=null;
    private TradeClient APIs=null;
    private SessionID sessionId=null;
    private int loginCounter=0;

    public SocketInitiator getSocketInitiator(){
        return this.socketInitiator;
    }
    public TradeClient getAPIs(){
        return this.APIs;
    }
    public SessionID getSessionId(){
        return this.sessionId;
    }


    public Init(String configFile, String targetCompID, String senderCompID, String userName, String password){


        try {
            SessionSettings sessionSettings = new SessionSettings(this.configStream(configFile,targetCompID,senderCompID,userName,password));

            APIs=new TradeClient( );
            APIs.setter(sessionSettings);

            FileStoreFactory fileStoreFactory = new FileStoreFactory (sessionSettings);
            FileLogFactory fileLogFactory = new FileLogFactory(sessionSettings);
            MessageFactory messageFactory = new DefaultMessageFactory();

            this.socketInitiator = new SocketInitiator(APIs, fileStoreFactory, sessionSettings, fileLogFactory, messageFactory);
            this.socketInitiator.start();
            List<Session> sessions=this.socketInitiator.getManagedSessions();
            this.sessionId = sessions.get(0).getSessionID();

        } catch (ConfigError e) {
            e.printStackTrace();
        }

    }


    public void loginSession(SessionID sessionID){
        try{
            System.out.println("SID: "+sessionID);
            Session.lookupSession(sessionID).logon();
            while(!Session.lookupSession(sessionID).isLoggedOn()){
                if(loginCounter==0) {
                    System.out.println("Waiting for login");
                }else {
                    System.out.print(".");
                }

                if(loginCounter==50)
                {
                    System.out.println("\n"+"Login failed");
                    break;
                }
                Thread.sleep(100);
                loginCounter++;
            }
            if(Session.lookupSession(sessionID).isLoggedOn()){
                System.out.println(" ");
            }
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void logoutSession(SessionID sessionID){
        try {
            if (Session.lookupSession(sessionID).isLoggedOn()) {
                Session.lookupSession(sessionID).logout("User logout");
                // System.out.println("Logout Successfully");

            } else {
                Session.lookupSession(sessionID).disconnect("Done", false);
            }
            this.socketInitiator.stop();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private InputStream configStream(String configFile,String targetCompID,String senderCompID,String userName,String password){
        String sessionString="[session]\n"
                +"SenderCompID="+senderCompID+"\n"
                +"TargetCompID="+targetCompID+"\n"
                +"Username="+userName+"\n"
                +"Password="+password+"\n";

        String configString = null;
        try {
            configString = new BufferedReader(new InputStreamReader(new FileInputStream(configFile))).lines().collect(Collectors.joining(System.lineSeparator()))+sessionString;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        InputStream configStream = new ByteArrayInputStream(configString.getBytes());
        return configStream;
    }

}
