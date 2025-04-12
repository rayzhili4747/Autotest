package com.forex.autotest.tradesimulator.acceptor;

import com.forex.autotest.utility.MessageBuilder;
import org.apache.mina.core.session.IoSession;

import java.util.Objects;

public class FIXTradeMsg {
    IoSession session;
    int seqNum;
    String time = null,tag75 = null;
    public static String orderStatus;
    public static String marketPrice=null;

    FIXTradeMsg(IoSession session, int seqNum) {
        this.session = session;
        this.seqNum = seqNum;
    }

    void responseLogin(String msg) {
        String tag49, tag56;
        tag49=MessageBuilder.getFIXTagValue(msg,"56");
        tag56=MessageBuilder.getFIXTagValue(msg,"49");
        switch(tag49) {
            case "FXDEMO":
                session.write(MessageBuilder.buildFIXMessage("8=FIX.4.4;35=A;49=" + tag49 + ";56=" + tag56 + ";34=" + seqNum + ";52=" + MessageBuilder.getTime() + ";98=0;108=30;"));
                //send test request;
                session.write(MessageBuilder.buildFIXMessage("8=FIX.4.4;35=1;49=" + tag49 + ";56=" + tag56 + ";34=" + (seqNum+1) + ";52=" + MessageBuilder.getTime()+";112="+MessageBuilder.getTime()));
                break;
            case "MultiFX":
                session.write(MessageBuilder.buildFIXMessage("8=FIX.4.4;35=A;49=" + tag49 + ";56=" + tag56 + ";34=" + seqNum + ";52=" + MessageBuilder.getTime() + ";98=0;108=30;789=2;"));
                break;
        }
    }

    void responseExecutionReport(String msg) throws InterruptedException {
        String tag49;
        tag49=MessageBuilder.getFIXTagValue(msg,"56");

        switch (tag49) {
            case "FXDEMO":
                session.write(getExeReport_SAB(msg,orderStatus));
                break;
            case "MultFX":
                session.write(getExeReport_MultiFX(msg,"0"));
                Thread.sleep(1000);
                session.write(getExeReport_MultiFX(msg,orderStatus));
                break;
        }
    }

    void responseHeartBeat(String msg) {
        String tag8, tag49, tag56;
        tag49=MessageBuilder.getFIXTagValue(msg,"56");
        tag56=MessageBuilder.getFIXTagValue(msg,"49");
        tag8=MessageBuilder.getFIXTagValue(msg,"8");
        session.write(MessageBuilder.buildFIXMessage("8=" + tag8 + ";35=0;34=" + seqNum + ";49=" + tag49 + ";52=" + MessageBuilder.getTime() + ";56=" + tag56));
    }



    String getExeReport_SAB(String msg,String orderStatus){
        String tag49, tag56,tag11,tag1,tag44,tag64,tag21,tag55,tag54,tag38,tag40,tag15,tag167,tag460,tag1056;
       try {
           tag40 = MessageBuilder.getFIXTagValue(msg, "40");

           if(tag40.equals("1")) {
               tag44 = marketPrice;
               tag1056=marketPrice;
           }else{
               tag44 = MessageBuilder.getFIXTagValue(msg, "44");
               tag1056 = String.valueOf((Double.parseDouble(tag44) * 1000));
           }
           tag49 = MessageBuilder.getFIXTagValue(msg, "56");
           tag56 = MessageBuilder.getFIXTagValue(msg, "49");
           tag11 = MessageBuilder.getFIXTagValue(msg, "11");
           tag1 = MessageBuilder.getFIXTagValue(msg, "1");

           tag64 = MessageBuilder.getFIXTagValue(msg, "64");
           tag21 = MessageBuilder.getFIXTagValue(msg, "21");
           tag55 = MessageBuilder.getFIXTagValue(msg, "55");
           tag54 = MessageBuilder.getFIXTagValue(msg, "54");
           tag38 = MessageBuilder.getFIXTagValue(msg, "38");

           tag15 = MessageBuilder.getFIXTagValue(msg, "15");
           tag167 = MessageBuilder.getFIXTagValue(msg, "167");
           tag460 = MessageBuilder.getFIXTagValue(msg, "460");

       }catch (Exception e){return null;}

        String exeReport=null;
        switch (orderStatus) {
            case "2":
                exeReport=MessageBuilder.buildFIXMessage("8=FIX.4.4;35=8;56="+tag56+";49="+tag49+";34="+seqNum+";52="+MessageBuilder.getTime()+";37="+MessageBuilder.newClOrderID()+";11="+tag11+";150=F;" +
                        "17="+MessageBuilder.newExecID()+";39=2;1="+tag1+";64="+tag64+";7250=SP;55="+tag55+";460="+tag460+";167="+tag167+";54="+tag54+";38="+tag38+";15="+tag15+";40="+tag40+";44="+tag44+";32="+tag38+
                        ";1056="+tag1056+";31="+tag44+";194="+tag44+";151=0;14="+tag38+";6="+tag44+";60="+MessageBuilder.getTime()+";21="+tag21);
                System.out.println(exeReport);
                break;
            case "8":
                System.out.println("Reject");
                break;
        }
        return exeReport;
    }

    String getExeReport_MultiFX(String msg,String orderStatus){
        String tag49, tag56,tag11,tag1,tag64,tag55,tag54,tag59,tag38,tag40,tag167,tag75,tag60;
        tag49=MessageBuilder.getFIXTagValue(msg,"56");
        tag56=MessageBuilder.getFIXTagValue(msg,"49");
        tag11=MessageBuilder.getFIXTagValue(msg,"11");
        tag1=MessageBuilder.getFIXTagValue(msg,"1");
        tag55=MessageBuilder.getFIXTagValue(msg, "55");
        tag54=MessageBuilder.getFIXTagValue(msg, "54");
        tag59=MessageBuilder.getFIXTagValue(msg, "59");
        tag38=MessageBuilder.getFIXTagValue(msg, "38");
        tag40=MessageBuilder.getFIXTagValue(msg, "40");
        tag60=MessageBuilder.getFIXTagValue(msg, "60");
        tag167=MessageBuilder.getFIXTagValue(msg,"167");
        tag64=Objects.requireNonNull(MessageBuilder.getFIXTagValue(msg, "52")).split("-")[0];
        tag75=Objects.requireNonNull(MessageBuilder.getFIXTagValue(msg, "52")).split("-")[0];


        String exeReport=null;
        switch (orderStatus) {
            case "0":
                exeReport=MessageBuilder.buildFIXMessage("8=FIX.4.4;35=8;49="+tag56+";56="+tag49+";34="+seqNum+";52="+MessageBuilder.getTime()+";1="+tag1+";6=0;11="+tag11+";14=0;17="+MessageBuilder.newExecID()+";18=u;37="+
                        tag11+";39=0;40="+tag40+";44=0;54="+tag54+";55="+tag55+";59="+tag59+";60="+tag60+";100=NTX;150=0;151="+tag38+";167="+tag167+";640=0;9164=FIX;9166="+tag49+";9175=0;9176="+tag11+";9177="+tag11+";9201=1;");
                break;
            case "2":
                exeReport=MessageBuilder.buildFIXMessage("8=FIX.4.4;35=8;49="+tag56+";56="+tag49+";34="+(seqNum+1)+";52="+MessageBuilder.getTime()+";1="+tag1+";6="+marketPrice+";11="+tag11+";12=0;14="+tag38+
                        ";17="+MessageBuilder.newExecID()+";31="+marketPrice+";32="+tag38+";37="+MessageBuilder.newClOrderID()+";39=2;40=1;54="+tag54+";55="+tag55+";60="+tag60+";64="+tag64+
                        ";75="+tag75+";100=NTX;150=F;151=0;167="+tag167+";9164=FIX;9166="+tag49+";9173=1;9175=0;9176="+tag11+";9177="+tag11+";9329=1.0");
                break;
            case "8":
                System.out.println("Reject");
                break;
        }
        return exeReport;
    }
}
