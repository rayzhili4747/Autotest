package com.forex.autotest.tradesimulator.initiator;

public class FIXTradeMultiClientsPlaceOrders {
    public static void main(String[] args) throws InterruptedException {
        String[] accounts = {"FIXTDM12_02", "FIXTDM12_01"};
        String targetCompID = "187FX";
        String ip = "192.168.1.187";
        int port = 29777;

        for (String account : accounts) {
            FIXClientPlaceOrdersThread client = new FIXClientPlaceOrdersThread(ip, port, account, targetCompID);
            new Thread(client).start();
        }

    }
}





