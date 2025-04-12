package com.forex.autotest.quotesimulator.initiator;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;


public class FIXClientHandler extends IoHandlerAdapter {
    private final static Logger log = LoggerFactory.getLogger(FIXClientHandler.class);
    private IoSession session=null;
    int seqNum=1;
    private String toAppMsg=null;
    private ArrayList<String> fromAppMsg=new ArrayList<>();
    private int i=0;
    @Override
    public void messageReceived(IoSession session, Object message){
        String msg = (String) message;
        fromAppMsg.add(msg);
        log.info("=========The message received from Server  is:" + msg);
        if(msg.contains("35=W")) {
            i += 1;
            System.out.println(i);
        }
    }
    @Override
    public void sessionCreated(IoSession session){

        log.debug("=========Session Created...");
    }
    @Override
    public void sessionOpened(IoSession session){
        this.session=session;
        log.debug("=========Session Opened...");
    }
    @Override
    public void sessionIdle(IoSession session, IdleStatus status) {
        log.debug(session + "=========Session Idle...");
    }
    @Override
    public void exceptionCaught(IoSession session, Throwable cause){
        log.error(cause.getMessage());
        cause.printStackTrace();
        session.closeNow();
    }
    @Override
    public void messageSent(IoSession session, Object message){
        this.toAppMsg=(String)message;
        log.debug("=========messageSent...");
    }
    @Override
    public void sessionClosed(IoSession session){
        log.debug("=========Session Closed...");
    }

    public ArrayList<String> getFromAppMsg(){
        return this.fromAppMsg;
    }

    public String getToAppMsg(){
        return this.toAppMsg;
    }

    public void resetFromAppMsg()
    {
        this.fromAppMsg.clear();
    }

    public IoSession getSession(){
        return this.session;
    }
}  