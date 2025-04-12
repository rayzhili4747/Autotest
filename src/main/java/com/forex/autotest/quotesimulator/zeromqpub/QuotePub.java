package com.forex.autotest.quotesimulator.zeromqpub;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

public class QuotePub {
    Socket publisher;
    ZMQQuoteThread quoteThread;

    public QuotePub(int port) {
        Context context = ZMQ.context(1);
        this.publisher = context.socket(ZMQ.PUB);
        this.publisher.bind("tcp://*:"+port);
        quoteThread=new ZMQQuoteThread(this.publisher);
    }
}