package com.forex.autotest.tradesimulator.initiator;
import com.forex.autotest.utility.MessageBuilder;


public class FIXClientLoginThread implements Runnable {
    private final String ip;
    private final int port;
    private final String account;
    private final String targetCompID;

    public FIXClientLoginThread(String ip, int port, String account, String targetCompID) {
        this.ip = ip;
        this.port = port;
        this.account = account;
        this.targetCompID = targetCompID;
    }

    @Override
    public void run() {
        FIXTradeClient FIXTradeClient =new FIXTradeClient(this.ip,this.port);
        String account=this.account,targetCompID=this.targetCompID,msg;

        while (FIXTradeClient.FIXTradeClientHandler.getSession()==null){
            System.out.print("...");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        msg = MessageBuilder.buildFIXMessage("35=A;49=" + account + ";56=" + targetCompID + ";34=" + FIXTradeClient.FIXTradeClientHandler.seqNum +
                ";52=" + FIXTradeClient.getTime() + ";98=0;553=" + account + ";554=TESTTEST1;108=25;141=Y");
        FIXTradeClient.sendMessage(msg);
        FIXTradeClient.FIXTradeClientHandler.seqNum++;
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        while(true) {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (FIXTradeClient.FIXTradeClientHandler.getSession().isClosing()){
                FIXTradeClient = new FIXTradeClient(ip, port);

                while (FIXTradeClient.FIXTradeClientHandler.getSession()==null){
                    System.out.print("...");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                msg = MessageBuilder.buildFIXMessage("35=A;49=" + account + ";56=" + targetCompID + ";34=" + FIXTradeClient.FIXTradeClientHandler.seqNum +
                        ";52=" + FIXTradeClient.getTime() + ";98=0;553=" + account + ";554=TESTTEST1;108=25;141=Y");
                FIXTradeClient.sendMessage(msg);
                FIXTradeClient.FIXTradeClientHandler.seqNum++;
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}