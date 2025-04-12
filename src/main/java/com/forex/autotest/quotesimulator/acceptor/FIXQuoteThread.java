package com.forex.autotest.quotesimulator.acceptor;

import com.forex.autotest.utility.QuoteLog;
import org.apache.mina.core.session.IoSession;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FIXQuoteThread extends Thread {
    private IoSession session;
    private String filePath;
    private String tag49;
    private String tag56;
    private int speed=0;

    FIXQuoteThread(IoSession session){
        this.session=session;
    }

    void setFilePath(String filePath){this.filePath=filePath;}

    void setTag49(String tag49){this.tag49=tag49;}

    void setTag56(String tag56){this.tag56=tag56;}


    void setSpeed(int speed){this.speed=speed;}

    @Override
    public void run(){
            System.out.println(QuoteLog.quoteReqID.get("USD/JPY"));
            System.out.println(QuoteLog.quoteReqID.get("AUD/USD"));


            File file = new File(filePath);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String reading = null;
            String quote = null;
            while ((reading = reader.readLine()) != null) {
                quote = reading.substring(reading.indexOf("8=FIX"), reading.indexOf("\u000110="));
                if (quote.contains("35=S") || quote.contains("35=W")) {
                    if(QuoteLog.ifSubscribe(quote)) {
                        session.write(QuoteLog.buildQuote(quote, tag49, tag56, FIXServerHandler.seqNum += 1, QuoteLog.getQuoteReqID(quote)));
                        Thread.sleep(this.speed);
                    }
                }
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
