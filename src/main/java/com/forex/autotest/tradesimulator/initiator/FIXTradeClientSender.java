package com.forex.autotest.tradesimulator.initiator;

import com.forex.autotest.utility.MessageBuilder;

import java.util.ArrayList;

public class FIXTradeClientSender {
    public static void main(String[] args) throws InterruptedException {
        int port=29777;
        String ip="192.168.1.187";
        FIXTradeClient FIXTradeClient =new FIXTradeClient(ip,port);
        String account="FIX_TRADE02",targetCompID="187FX",msg;

        while (FIXTradeClient.FIXTradeClientHandler.getSession()==null){
            System.out.print("...");
            Thread.sleep(100);
        }
        System.out.println(">>>"+ FIXTradeClient.FIXTradeClientHandler.getSession());


            msg = MessageBuilder.buildFIXMessage("35=A;49=" + account + ";56=" + targetCompID + ";34=" + FIXTradeClient.FIXTradeClientHandler.seqNum +
                    ";52=" + FIXTradeClient.getTime() + ";98=0;553=" + account + ";554=TESTTEST1;108=25;141=Y");
            FIXTradeClient.sendMessage(msg);
            FIXTradeClient.FIXTradeClientHandler.seqNum++;
            Thread.sleep(2000);
            //market order FOR GTC/IOC/FOK

        StringBuilder stringBuilder = new StringBuilder();
        String time = FIXTradeClient.getTime();

            stringBuilder.setLength(0);  // clear the StringBuilder
            stringBuilder.append("35=D;49=").append(account).append(";56=").append(targetCompID).append(";34=")
                    .append(FIXTradeClient.FIXTradeClientHandler.seqNum).append(";52=").append(time).append(";1=").append(account)
                    .append(";11=").append(MessageBuilder.newClOrderID()).append(";38=").append(MessageBuilder.randomQty(100))
                    .append(";40=1;54=").append(MessageBuilder.randomDigit("12")).append(";55=EUR/USD;59=1;60=").append(time)
                    .append(";167=FOR;21=1");
            msg = stringBuilder.toString();
            FIXTradeClient.sendMessage(MessageBuilder.buildFIXMessage(msg));
            FIXTradeClient.FIXTradeClientHandler.seqNum++;
            Thread.sleep(6);



        Thread.sleep(2000);
        //logout
        msg=MessageBuilder.buildFIXMessage("35=5;49="+account+";56="+targetCompID+";34="+ FIXTradeClient.FIXTradeClientHandler.seqNum+
                ";52="+ FIXTradeClient.getTime());
        FIXTradeClient.sendMessage(msg);
                ArrayList<String> s=new ArrayList<>();

        Thread.sleep(2000);
        System.out.println(FIXTradeClient.FIXTradeClientHandler.getToAppMsgs().size());
        System.out.println(FIXTradeClient.FIXTradeClientHandler.getFromAppMsg().size());
        FIXTradeClient.FIXTradeClientHandler.resetToAppMsgs();
        FIXTradeClient.FIXTradeClientHandler.resetFromAppMsg();


    }
}





