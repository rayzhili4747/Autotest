package com.forex.autotest.quotesimulator.zeromqsub;

public class InitSub {
    public static void main(String[] args) throws InterruptedException {
        QuoteSub quoteSub=new QuoteSub("192.168.1.128","1234");

        String quote;
        long startTime=System.currentTimeMillis();
        int count1=0,count2=0;

        while (true) {
            quote=new String(quoteSub.subscribe.recv());
            if (quote.contains("\u0001XAU/USD\u0001")){
                count1++;
                System.out.println(quote);
            }
            if(quote.contains("\u0001USD/JPY\u0001")) {
                count2++;
                System.out.println(quote);
            }

            if(System.currentTimeMillis()-startTime>10000)
                break;

        }

            System.out.println("sym1: "+count1);
            System.out.println("sym2: "+count2);

    }

}