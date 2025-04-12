package com.forex.autotest.quickfixjengine.quoteinitiator;
import quickfix.SessionID;

import java.io.File;

public class QuoteApp {
    public static void main(String[] args) throws InterruptedException {


        Init quoteClient=new Init(System.getProperty("user.dir") + File.separator+"config\\quote.properties","Quote01","client01","client01","TEST1234");

        System.out.println(System.getProperty("user.dir"));

        QuoteClient quoteClientAPI=quoteClient.getQuoteClientAPIs();

        SessionID sessionId=quoteClient.getSessionId();

        quoteClient.loginSession(sessionId);

        quoteClientAPI.quoteRequest(sessionId,"EUR/USD");
        System.out.println("Awaiting for quotes");
        Thread.sleep(10000);
        for(String quote:quoteClientAPI.getQuotes()){
            System.out.println(quote);
        }

        Thread.sleep(1000);
        quoteClient.logoutSession(sessionId);


    }
}