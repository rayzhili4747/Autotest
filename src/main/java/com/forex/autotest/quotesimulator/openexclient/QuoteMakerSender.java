package com.forex.autotest.quotesimulator.openexclient;

import java.io.IOException;


public class QuoteMakerSender {
    public static void main(String[] args) throws IOException, InterruptedException {

            QuoteMaker quoteMaker =new QuoteMaker("192.168.1.167",27771);
            System.out.println("Connected to acceptor: "+ quoteMaker.getIp() );

            quoteMaker.sendMsg("LSA02;TESTTEST1;1");

            Thread.sleep(100);
            System.out.println("<-- login: "+ quoteMaker.getMsg());
             quoteMaker.sendMsg("OO;EUR/USD;-0.01000;0.01000;0;1;0;0;");

            Thread.sleep(300);
            System.out.println("<-- QM: "+ quoteMaker.getMsg());

    }
}