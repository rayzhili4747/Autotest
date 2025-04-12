package com.forex.autotest.quotesimulator.acceptor;

import com.forex.autotest.utility.FIXMessageBuilder;
import com.forex.autotest.utility.QuoteLog;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class FIXServerHandler extends IoHandlerAdapter{
    private final static Logger log = LoggerFactory.getLogger(FIXServerHandler.class);
    static int seqNum;
    private String loginACK;
    private String tag8;
    private String tag49;
    private String tag56;
    private String testReqID;
    public IoSession session;
    private ArrayList<String> receivedMsg=new ArrayList<>();
    static boolean symbolSwitch;
    private ArrayList<String> sentMsg=new ArrayList<>();




    @Override
    public void messageReceived(IoSession session, Object message) {
        String symbol;
        String reqID;
        String msg = message.toString();
        log.info("=========The message received from Client is:" + msg);


        if(msg.contains("35=A")) {
            receivedMsg.add(msg);
            session.write(this.loginACK);
            seqNum+=1;


        }else if(msg.contains("35=1")){
            receivedMsg.add(msg);
            seqNum=Integer.parseInt(msg.substring(msg.indexOf("34=")+3,msg.indexOf("\u000152=")));
            testReqID=msg.substring(msg.indexOf("112=")+4,msg.indexOf("\u000110="));
            session.write(FIXMessageBuilder.buildFIXMessage(tag8+"35=0;"+tag49+tag56+"34=" + seqNum + ";52=" + getTime()+";112="+testReqID+";60="+ FIXServerHandler.getTime()));
            seqNum+=1;
        }else if(msg.contains("35=V")){
            seqNum=Integer.parseInt(msg.substring(msg.indexOf("34=")+3,msg.indexOf("\u000152=")));
            symbolSwitch=true;
        }else if(msg.contains("35=R")){
            symbol=msg.substring(msg.indexOf("\u000155=")+4,msg.indexOf("\u0001",msg.indexOf("\u000155=")+1));
            reqID=msg.substring(msg.indexOf("\u0001131=")+5,msg.indexOf("\u0001",msg.indexOf("\u0001131=")+1));
            QuoteLog.quoteReqID.put(symbol,reqID);
            seqNum=Integer.parseInt(msg.substring(msg.indexOf("34=")+3,msg.indexOf("\u000152=")));
            symbolSwitch=true;
        }else{
            receivedMsg.add(msg);
            seqNum+=1;
        }


    }
    @Override
    public void sessionClosed(IoSession session)  {
        log.debug("=========Session Closed...");
    }
    @Override
    public void sessionCreated(IoSession session)  {
        this.session=session;
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
        this.sentMsg.add(message.toString());
    }

     static String getTime(){
        SimpleDateFormat formatDateTime = new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SSS");
        return formatDateTime.format(Calendar.getInstance().getTime());
    }

    void setLoginACK(String msg){
        this.loginACK=msg;
    }

    void setTag8(String tag8){this.tag8=tag8;}

    void setTag49(String tag49){this.tag49=tag49;}

    void setTag56(String tag56){this.tag56=tag56;}

    ArrayList<String> getSentMsg(){return this.sentMsg;}

    ArrayList<String> getReceivedMsg(){
        return this.receivedMsg;
    }

    void resetReceivedMsg(){
        this.receivedMsg.clear();
    }
}

