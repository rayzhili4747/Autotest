package com.forex.autotest.quotesimulator.initiator;

import com.forex.autotest.utility.FIXMessageBuilder;


public class FIXClientReceiver {
    public static void main(String[] args) throws InterruptedException {
        int port=29990;
        String ip="192.1.2.218";
        FIXClient fixClient =new FIXClient(ip,port);

        while (fixClient.fixClientHandler.getSession()==null){
            Thread.sleep(1);
            System.out.print('.');
        }


        String msg= FIXMessageBuilder.buildFIXMessage("35=A;34="+fixClient.fixClientHandler.seqNum+";49=FIX_SA01;56=MultiQuote;141=Y;553=FIX_SA01;554=TESTTEST1;");
        fixClient.sendMessage(msg);


        Thread.sleep(100);
        //market data request
        fixClient.fixClientHandler.seqNum += 1;
        msg = FIXMessageBuilder.buildFIXMessage("35=V;49=FIX_SA01;56=MultiQuote;34=" + fixClient.fixClientHandler.seqNum + ";52=20220914-08:42:45.063;262=53;263=1;264=0;265=0;267=2;269=0;269=1;146=1;55=AUD/CAD;");
        fixClient.sendMessage(msg);

    }
}
