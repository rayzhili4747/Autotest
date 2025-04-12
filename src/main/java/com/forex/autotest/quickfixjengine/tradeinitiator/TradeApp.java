package com.forex.autotest.quickfixjengine.tradeinitiator;

import quickfix.SessionID;
import quickfix.field.OrdType;
import quickfix.field.Side;
import quickfix.field.TimeInForce;

import java.io.File;

public class TradeApp {
    public static void main(String[] args) throws InterruptedException {
        Init init=new Init(System.getProperty("user.dir") + File.separator+"config\\trade\\Trade.properties","192.168.1.123","USER_ADMIN","USER_ADMIN","TEST1234");

        System.out.println(System.getProperty("user.dir"));
        TradeClient tradeClient=init.getAPIs();

        SessionID sessionId=init.getSessionId();
        init.loginSession(sessionId);

        System.out.println("place Order");
        tradeClient.newOrderSingle_MARKET(sessionId,"trade01",100.01, OrdType.MARKET, Side.BUY,"AUD/USD", TimeInForce.GOOD_TILL_CANCEL,"FOR");

        Thread.sleep(3000);
        init.logoutSession(sessionId);

    }
}