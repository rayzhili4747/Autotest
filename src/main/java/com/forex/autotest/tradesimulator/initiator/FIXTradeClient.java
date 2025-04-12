package com.forex.autotest.tradesimulator.initiator;

import com.forex.autotest.utility.FIXProtocolCodecFactory;
import com.forex.autotest.utility.SSLContextGenerator;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.Executors;


public class FIXTradeClient {
    private static final Logger log = LoggerFactory.getLogger(FIXTradeClient.class);
    FIXTradeClientHandler FIXTradeClientHandler;
    private IoConnector connector;

    FIXTradeClient(String ip, int port){
        NioSocketConnector connector = new NioSocketConnector(Runtime.getRuntime().availableProcessors());
        this.connector=connector;
        connector.getSessionConfig().setReadBufferSize(2048);
        connector.getSessionConfig().setWriteTimeout(0);

        //config filter
        DefaultIoFilterChainBuilder defaultIoFilterChainBuilder = connector.getFilterChain();
        LoggingFilter loggingFilter = new LoggingFilter();
        defaultIoFilterChainBuilder.addLast("loggingFilter", loggingFilter);

        defaultIoFilterChainBuilder.addLast("protocolCodecFilter", new ProtocolCodecFilter(new FIXProtocolCodecFactory()));
        defaultIoFilterChainBuilder.addLast("ThreadPool",new ExecutorFilter(Executors.newCachedThreadPool()));

        //config NioSocketConnector handler
        FIXTradeClientHandler FIXTradeClientHandler = new FIXTradeClientHandler();
        this.FIXTradeClientHandler = FIXTradeClientHandler;
        connector.setHandler(FIXTradeClientHandler);
        connector.connect(new InetSocketAddress(ip, port));

        log.info("=========MINA TCP Client has started============");
    }

    FIXTradeClient(String ip, int port, String keyStorePath, String trustStorePath){
        NioSocketConnector connector = new NioSocketConnector();
        this.connector=connector;
        //config filter
        DefaultIoFilterChainBuilder defaultIoFilterChainBuilder = connector.getFilterChain();

        SSLContext sslContext = new SSLContextGenerator(keyStorePath,trustStorePath).getSslContext();
        System.out.println("SSLContext protocol is: " + sslContext.getProtocol());

        SslFilter sslFilter = new SslFilter(sslContext);
        sslFilter.setUseClientMode(true);
        defaultIoFilterChainBuilder.addFirst("sslFilter", sslFilter);

        LoggingFilter loggingFilter = new LoggingFilter();
        defaultIoFilterChainBuilder.addLast("loggingFilter", loggingFilter);

        defaultIoFilterChainBuilder.addLast("protocolCodecFilter", new ProtocolCodecFilter(new FIXProtocolCodecFactory()));
        FIXTradeClientHandler FIXTradeClientHandler = new FIXTradeClientHandler();
        this.FIXTradeClientHandler = FIXTradeClientHandler;
        connector.setHandler(FIXTradeClientHandler);
        connector.connect(new InetSocketAddress(ip, port));

        log.info("=========MINA TCP SSL Client has started============");
    }

    void sendMessage(String msg) {
        this.FIXTradeClientHandler.getSession().write(msg);
    }

    public String getTime() {
        SimpleDateFormat formatDateTime = new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SSS");
        return formatDateTime.format(Calendar.getInstance().getTime());
    }

    void exit(){

        this.FIXTradeClientHandler.getSession().closeNow();
        this.connector.dispose();

    }
}