package com.forex.autotest.tradesimulator.acceptor;

import com.forex.autotest.utility.FIXProtocolCodecFactory;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

public class FIXTradeServer {
        private static final Logger log = LoggerFactory.getLogger(FIXTradeServer.class);
        FIXTradeServerHandler LpHandler;
        private IoAcceptor acceptor;

        FIXTradeServer(int port) throws IOException, InterruptedException {
            IoAcceptor acceptor=new NioSocketAcceptor();
            this.acceptor=acceptor;
            //config filter
            DefaultIoFilterChainBuilder defaultIoFilterChainBuilder = acceptor.getFilterChain();
            LoggingFilter loggingFilter = new LoggingFilter();
            defaultIoFilterChainBuilder.addLast("loggingFilter", loggingFilter);
            ProtocolCodecFilter protocolCodecFilter = new ProtocolCodecFilter(new FIXProtocolCodecFactory());
            defaultIoFilterChainBuilder.addLast("protocolCodecFilter",protocolCodecFilter);

            //config NioSocketAcceptor handler
            FIXTradeServerHandler LpHandler = new FIXTradeServerHandler();
            this.LpHandler = LpHandler;
            acceptor.setHandler(LpHandler);
            acceptor.bind(new InetSocketAddress(port));

            log.info("=========LP Simulator has started============");

         }

        FIXTradeServer(int port, int seqNum) throws IOException, InterruptedException {
            IoAcceptor acceptor=new NioSocketAcceptor();
            this.acceptor=acceptor;
            //config filter
            DefaultIoFilterChainBuilder defaultIoFilterChainBuilder = acceptor.getFilterChain();
            LoggingFilter loggingFilter = new LoggingFilter();
            defaultIoFilterChainBuilder.addLast("loggingFilter", loggingFilter);
            ProtocolCodecFilter protocolCodecFilter = new ProtocolCodecFilter(new FIXProtocolCodecFactory());
            defaultIoFilterChainBuilder.addLast("protocolCodecFilter",protocolCodecFilter);

            //config NioSocketAcceptor handler
            FIXTradeServerHandler LpHandler = new FIXTradeServerHandler();
            this.LpHandler = LpHandler;
            acceptor.setHandler(LpHandler);
            acceptor.bind(new InetSocketAddress(port));
            LpHandler.seqNum=seqNum;
            log.info("=========LP Simulator has started============");

        }

        public void exit(){
            if(LpHandler.session!=null){
                LpHandler.session.closeNow();
            }
            this.acceptor.dispose(true);
        }

        public void sendMessage(String msg){
            this.LpHandler.session.write(msg);
        }

}