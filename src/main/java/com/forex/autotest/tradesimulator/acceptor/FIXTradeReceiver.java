package com.forex.autotest.tradesimulator.acceptor;

import java.io.IOException;


public class FIXTradeReceiver {
    public static void main(String[] args) throws IOException, InterruptedException {
        FIXTradeServer lp = new FIXTradeServer(11153);
        FIXTradeMsg.orderStatus="2";
        FIXTradeMsg.marketPrice="1.08488";

        Thread.sleep(1200000);
        lp.exit();
    }
}
