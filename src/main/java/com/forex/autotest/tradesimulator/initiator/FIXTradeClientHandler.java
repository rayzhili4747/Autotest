package com.forex.autotest.tradesimulator.initiator;

import com.forex.autotest.utility.FIXMessageBuilder;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class FIXTradeClientHandler extends IoHandlerAdapter {
    private final static Logger log = LoggerFactory.getLogger(FIXTradeClientHandler.class);
    private IoSession session=null;
    int seqNum=1;
    private String toAppMsg=null;
    private ArrayList<String> fromAppMsg=new ArrayList<>();
    private ArrayList<String> toAppMsgs=new ArrayList<>();
    private int count=0;
    private int sleepTime=0;

    @Override
    public void messageReceived(IoSession session, Object message) throws InterruptedException {

        String msg = message.toString();
        log.info("=========The message received from Client is:" + msg);
        fromAppMsg.add(msg);
        if(msg.contains("\u000135=0")||msg.contains("\u000135=1")){
            System.out.println(msg);

            Thread.sleep(2100);
            String tag8,tag49,tag56,tag112;
            tag49=msg.substring(msg.indexOf("\u000156=")+4);
            tag49=tag49.substring(0,tag49.indexOf("\u0001"));
            tag56=msg.substring(msg.indexOf("\u000149=")+4);
            tag56=tag56.substring(0,tag56.indexOf("\u0001"));
            tag8=msg.substring(msg.indexOf("\u00018=")+3);
            tag8=tag8.substring(0,tag8.indexOf("\u0001"));
            tag112=msg.substring(msg.indexOf("\u0001112=")+5);
            tag112=tag112.substring(0,tag112.indexOf("\u0001"));
            session.write(FIXMessageBuilder.buildFIXMessage("8="+tag8+";35=0;34="+this.seqNum+";49="+tag49+";52="+getTime()+";56="+tag56+";112="+tag112));
            seqNum++;
            count++;
        }

    }
    @Override
    public void sessionCreated(IoSession session){

        //log.debug("=========Session Created...");
    }
    @Override
    public void sessionOpened(IoSession session){
        this.session=session;
        //log.debug("=========Session Opened...");
    }
    @Override
    public void sessionIdle(IoSession session, IdleStatus status){
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
        this.toAppMsgs.add((String)message);
        log.info("=========messageSent "+System.currentTimeMillis()+" "+message.toString());
    }
    @Override
    public void sessionClosed(IoSession session){

        log.debug("=========Session Closed...");
    }

    ArrayList<String> getFromAppMsg(){
        return this.fromAppMsg;
    }

    String getToAppMsg(){
        return this.toAppMsg;
    }

    ArrayList<String> getToAppMsgs(){return  this.toAppMsgs;}

    void resetFromAppMsg()
    {
        this.fromAppMsg.clear();
    }

    void resetToAppMsgs(){this.toAppMsgs.clear();}

    IoSession getSession(){
        return this.session;
    }

    public String getTime() {
        SimpleDateFormat formatDateTime = new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SSS");
        return formatDateTime.format(Calendar.getInstance().getTime());
    }
}  