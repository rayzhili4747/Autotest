package com.forex.autotest.quotesimulator.acceptor;

import com.forex.autotest.utility.MessageBuilder;

import java.io.IOException;

public class FIXQuoteReplayer {

    public static void main(String[] args) throws IOException, InterruptedException {
        FIXServer fixServer = new FIXServer(54744);

        String loginACK=MessageBuilder.buildFIXMessage("8=FIX.4.2;35=A;49=BBB-PRICES-TEST;56=AAA_FOR-PRICES-TEST;34="+ FIXServerHandler.seqNum+";52="+ FIXServerHandler.getTime()+";98=0;108=30;141=Y;384=1;372=V;385=R;60="+ FIXServerHandler.getTime());
        fixServer.fixServerHandler.setLoginACK(loginACK);
        fixServer.fixServerHandler.setTag8("8=FIX.4.2;");
        fixServer.fixServerHandler.setTag49("49=BBB-PRICES-TEST;");
        fixServer.fixServerHandler.setTag56("56=AAA_FOR-PRICES-TEST;");

        while(!FIXServerHandler.symbolSwitch){
            Thread.sleep(100);
            System.out.print('.');
        }


        Thread.sleep(15000);
        FIXQuoteThread quoteThread=new FIXQuoteThread(fixServer.fixServerHandler.session);
        quoteThread.setFilePath("A:\\projects\\BBB_USDJPY_AllDuplicatedQuotes_20230303.txt");

        quoteThread.setTag49("BBB-PRICES-TEST");
        quoteThread.setTag56("AAA-PRICES-TEST");
        quoteThread.setSpeed(100);
        quoteThread.start();

    }

}
