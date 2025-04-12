package com.forex.autotest.quotesimulator.initiator;

import com.forex.autotest.utility.FIXProtocolCodecFactory;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class FIXClient {
    private static final Logger log = LoggerFactory.getLogger(FIXClient.class);
    FIXClientHandler fixClientHandler;
    private IoConnector connector;


    FIXClient(String ip, int port){
        IoConnector connector = new NioSocketConnector();
        this.connector=connector;
        //config filter
        DefaultIoFilterChainBuilder defaultIoFilterChainBuilder = connector.getFilterChain();
        LoggingFilter loggingFilter = new LoggingFilter();
        defaultIoFilterChainBuilder.addLast("loggingFilter", loggingFilter);

        defaultIoFilterChainBuilder.addLast("protocolCodecFilter", new ProtocolCodecFilter(new FIXProtocolCodecFactory()));
        //config NioSocketConnector handler
        FIXClientHandler fixClientHandler = new FIXClientHandler();
        this.fixClientHandler = fixClientHandler;
        connector.setHandler(fixClientHandler);
        connector.connect(new InetSocketAddress(ip, port));

        log.info("=========quotesimulator Client has started============");
    }

    void sendMessage(String msg) {
        this.fixClientHandler.getSession().write(msg);

    }

    public String getTime() {
        SimpleDateFormat formatDateTime = new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SSS");
        return formatDateTime.format(Calendar.getInstance().getTime());
    }

    public void exit(){
        this.fixClientHandler.getSession().closeNow();
        this.connector.dispose(true);


    }
}