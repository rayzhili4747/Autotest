package com.forex.autotest.quotesimulator.zeromqpub;

public class initPub {
    public static void main(String[] args) throws InterruptedException {
        QuotePub quotePub=new QuotePub(5561);
        Thread.sleep(1000);
        String msg;

        for(int i=0;i<10000;i++) {
Thread.sleep(1000);
            msg="2|EUR/USD|nnnnnnnnn" +
                    "|0|5|ABC|1.13513|1000000|xxx|\t|DEF|1.13512|1000000|xxx|\t|XXX|1.13512|1000000|xxx|\t|JEEF|1.13512|500000|xxx|\t|JEEF|1.13511|4500000|xxx|\t" +
                    "|1|5|DEF|1.13514|1000000|xxx|\t|ABC|1.13514|1000000|xxx|\t|GSM|1.13514|500000|xxx|\t|USBR|1.13515|3000000|xxx|\t|USBR|1.13515|1000000|xxx|\t" +
                    "\4\n";
            quotePub.publisher.send(formatMsg(msg));
            System.out.println(formatMsg(msg));
        }

        quotePub.publisher.close();

    }
    static String formatMsg(String msg){
        return msg.replace("|","\u0001");
    }
}
