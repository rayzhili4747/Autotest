package com.forex.autotest.tradesimulator.initiator;

import com.forex.autotest.utility.MessageBuilder;

public class FIXTraderSSLClientSender {
    public static void main(String[] args) throws InterruptedException {
        int port=20000;
        String ip="192.168.1.227";
        FIXTradeClient FIXTradeClient =new FIXTradeClient(ip,port,"A:\\projects\\ssl\\keystore.jks","A:\\projects\\ssl\\truststore.jks");

        while (FIXTradeClient.FIXTradeClientHandler.getSession()==null){
            Thread.sleep(1);
            System.out.print('.');
        }

        String msg=MessageBuilder.buildFIXMessage("35=A;49=XT_USER02;56=XXX;34="+ FIXTradeClient.FIXTradeClientHandler.seqNum+";52="+ FIXTradeClient.getTime()+";98=0;553=XT_USER02;554=TESTTEST1;108=60;141=Y");
        FIXTradeClient.sendMessage(msg);

        Thread.sleep(6000);
        FIXTradeClient.exit();


    }
}
