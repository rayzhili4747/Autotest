package com.forex.autotest.quotesimulator.acceptor;

import com.forex.autotest.utility.FIXProtocolCodecFactory;
import com.forex.autotest.utility.QuoteLog;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

public class FIXServer {
        private static final Logger log = LoggerFactory.getLogger(FIXServer.class);
        FIXServerHandler fixServerHandler;
        private IoAcceptor acceptor;

        FIXServer(int port) throws IOException {
            IoAcceptor acceptor=new NioSocketAcceptor();
            this.acceptor=acceptor;
            //config filter
            DefaultIoFilterChainBuilder defaultIoFilterChainBuilder = acceptor.getFilterChain();
            LoggingFilter loggingFilter = new LoggingFilter();
            defaultIoFilterChainBuilder.addLast("loggingFilter", loggingFilter);
            ProtocolCodecFilter protocolCodecFilter = new ProtocolCodecFilter(new FIXProtocolCodecFactory());
            defaultIoFilterChainBuilder.addLast("protocolCodecFilter",protocolCodecFilter);

            //config NioSocketAcceptor handler
            FIXServerHandler fixServerHandler = new FIXServerHandler();
            this.fixServerHandler = fixServerHandler;
            acceptor.setHandler(fixServerHandler);
            acceptor.bind(new InetSocketAddress(port));

            log.info("=========quotesimulator Server has started============");
            FIXServerHandler.seqNum = 1;
            FIXServerHandler.symbolSwitch = false;
            QuoteLog.quoteReqID.clear();

        }

        public void exit(){
            fixServerHandler.session.closeNow();
            this.acceptor.dispose(true);
        }

        public void sendMessage(String msg){
            this.fixServerHandler.session.write(msg);
        }





}