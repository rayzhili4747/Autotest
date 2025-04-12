package com.forex.autotest.tradesimulator.acceptor;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;


public class FIXTradeServerHandler extends IoHandlerAdapter{
    private final static Logger log = LoggerFactory.getLogger(FIXTradeServerHandler.class);
    int seqNum=1;
    public IoSession session;
    private ArrayList<String> receivedMsg=new ArrayList<>();
    private ArrayList<String> sentMsg=new ArrayList<>();
    FIXTradeMsg reMsg;

    @Override
    public void messageReceived(IoSession session, Object message) throws InterruptedException {
        String msg = message.toString();

        log.info("=========The message received from Client is:" + msg);


        if(msg.contains("\u000135=A\u0001")) {
            reMsg.responseLogin(msg);
        }else if(msg.contains("\u000135=0\u0001")||msg.contains("\u000135=1\u0001")){
            receivedMsg.add(msg);
            reMsg.responseHeartBeat(msg);
        }else if(msg.contains("\u000135=D\u0001")) {
            receivedMsg.add(msg);
            reMsg.responseExecutionReport(msg);
        }else{
            receivedMsg.add(msg);
        }

    }
    @Override
    public void sessionClosed(IoSession session)  {
        log.debug("=========Session Closed...");
    }
    @Override
    public void sessionCreated(IoSession session)  {
        this.session=session;
        this.reMsg=new FIXTradeMsg(session,seqNum);
        log.debug("=========Session Created..."+session.toString());
    }
    @Override
    public void sessionIdle(IoSession session, IdleStatus status)  {
        log.debug(session + "=========Session Idle..."
        );
    }
    @Override
    public void sessionOpened(IoSession session)  {
        log.debug("=========Session Opened...");
    }
    @Override
    public void exceptionCaught(IoSession session, Throwable cause)  {
        log.error(cause.getMessage());
        cause.printStackTrace();
        session.closeNow();
    }
    @Override
    public void messageSent(IoSession session, Object message)  {
        log.debug("=========messageSent..."+ message.toString());
        this.seqNum+=1;
        this.reMsg.seqNum=this.seqNum;
        this.sentMsg.add(message.toString());
    }

    public ArrayList<String> getSentMsg(){return this.sentMsg;}

    public ArrayList<String> getReceivedMsg(){return this.receivedMsg;}

    public void resetReceivedMsg(){this.receivedMsg.clear();}

    public void resetSentdMsg() {this.sentMsg.clear();}

}

