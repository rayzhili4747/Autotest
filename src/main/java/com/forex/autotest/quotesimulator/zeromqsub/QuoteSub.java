package com.forex.autotest.quotesimulator.zeromqsub;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import java.util.HashMap;
import java.util.Map;

public class QuoteSub {
    public Socket subscribe;
    Map<String,Double> price;

    public QuoteSub(String ip,String port) {
        Context context = ZMQ.context(1);
        this.subscribe = context.socket(ZMQ.SUB);
        this.subscribe.connect("tcp://"+ip+":"+port);
        this.subscribe.subscribe("");

    }

    public Map<String,Double> getPrice(String symble, String quote){
        int index;
        double bid;
        double ask;
        if (quote.contains(symble)) {
            System.out.println(quote);
            index=quote.indexOf("\u00010\u0001");
            quote=quote.substring(index+3);
            index=quote.indexOf("\u0001");
            quote=quote.substring(index+1);
            index=quote.indexOf("\u0001");
            quote=quote.substring(index+1);


            bid=Double.parseDouble(quote.substring(0,quote.indexOf("\u0001")));

            index=quote.indexOf("\u00011\u0001");
            quote=quote.substring(index+3);
            index=quote.indexOf("\u0001");
            quote=quote.substring(index+1);
            index=quote.indexOf("\u0001");
            quote=quote.substring(index+1);


            ask=Double.parseDouble(quote.substring(0,quote.indexOf("\u0001")));

            this.price=new HashMap<>();
            price.put("bid",bid);
            price.put("ask",ask);
            System.out.println("bid: "+bid);
            System.out.println("ask: "+ask);
            return price;
        }else{
            return null;
        }

    }

}