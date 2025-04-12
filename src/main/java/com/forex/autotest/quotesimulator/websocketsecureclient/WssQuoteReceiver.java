package com.forex.autotest.quotesimulator.websocketsecureclient;

import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;

import java.net.URI;
import java.net.URISyntaxException;


public class WssQuoteReceiver {
    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        Draft draft = new Draft_17();
        WssClient wssClient=new WssClient(new URI("wss://192.168.1.199:31710/ws"), draft);
        wssClient.setAccount("2001");
        wssClient.setPassword("testtest1");
        wssClient.setTok("ad2cbaba116f429e8d0ae348635a540d");
        wssClient.connect();


        while (!wssClient.getReadyState().toString().equals("OPEN")){
            System.out.println("Connecting......");
            Thread.sleep(100);
        }

        System.out.println(wssClient.getStatusMsg());
        Thread.sleep(300000);
        wssClient.close();
    }
}