package com.forex.autotest.quotesimulator.zeromqpub;

public class initPubReplay {
    public static void main(String[] args) throws InterruptedException {
        QuotePub quotePub=new QuotePub(5561);
        Thread.sleep(1000);

Thread.sleep(100);
        quotePub.quoteThread.setFilePath("A:\\projects\\QUOTESTRESS_0MQSnapshot_Msg_20220226.log");
        quotePub.quoteThread.setSpeed(50);
        quotePub.quoteThread.start();

        Thread.sleep(1000000);

        quotePub.publisher.close();

    }
    static String formatMsg(String msg){
        return msg.replace("|","\u0001");
    }
}
