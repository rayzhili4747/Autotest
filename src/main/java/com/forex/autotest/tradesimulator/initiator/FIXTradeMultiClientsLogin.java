package com.forex.autotest.tradesimulator.initiator;

public class FIXTradeMultiClientsLogin {
    public static void main(String[] args) throws InterruptedException {
        String[] accounts = {"FIXTDM0_01", "FIXTDM1_01",   "FIXTDM13_01", "FIXTDM13_02",
                "FIXTDM14_01", "FIXTDM15_01", "FIXTDM16_01", "FIXTDM17_01", "FIXTDM2_01", "FIXTDM20_01", "FIXTDM24_01",
                "FIXTDM27_01", "FIXTDM28_01", "FIXTDM29_01", "FIXTDM37_02", "FIXTDM38_01", "FIXTDM39_01", "FIXTDM40_01",
                "FIXTDM41_01", "FIXTDM42_01", "FIXTDM43_01", "FIXTDM44_01","FIXTRADE01", "FIXTRADE02", "FIXTRADE03",
                 "FIXTRADE06"};//"FIXTDM12_02",
        String targetCompID = "187FX";
        String ip = "192.168.1.187";
        int port = 29777;

        for (String account : accounts) {
            FIXClientLoginThread client = new FIXClientLoginThread(ip, port, account, targetCompID);
            new Thread(client).start();
        }

    }
}