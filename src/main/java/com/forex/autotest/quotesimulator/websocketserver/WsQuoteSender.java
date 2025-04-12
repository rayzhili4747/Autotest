package com.forex.autotest.quotesimulator.websocketserver;

public class WsQuoteSender {

    public static void main(String[] arg) throws Exception
    {
        WsQuoteServer server=new WsQuoteServer(8777,"/ws/v3?brokerId=7799");
        Thread thread=new Thread(server);
        thread.start();

        String partial="{\"table\":\"spot/depth\",\"action\":\"partial\",\"data\":[{\"instrument_id\":\"BBB-USD\",\"asks\":[[\"56201.2\",\"0.001\",\"0\"],[\"56202.6\",\"0.01\",\"0\"],[\"56202.9\",\"1\",\"0\"],[\"56203.7\",\"1\",\"0\"],[\"56203.8\",\"1.2\",\"0\"]],\"bids\":[[\"56200.2\",\"0.001\",\"0\"],[\"56198.5\",\"0.01\",\"0\"],[\"56198.1\",\"1\",\"0\"],[\"56197.9\",\"1\",\"0\"],[\"56197.6\",\"1.2\",\" 0\"]],\"timestamp\":\"2022-04-26T04:10:16.963Z\",\"checksum\":-1175183496}]}";
        String update="{\"table\":\"spot/depth\",\"action\":\"update\",\"data\":[{\"instrument_id\":\"BBB-USD\",\"asks\":[[\"56202.8\",\"1.4\",\"0\"]],\"bids\":[[\"56198.6\",\"1.5\",\"0\"]],\"timestamp\":\"2022-04-26T04:10:22.074Z\",\"checksum\":709655830}]}";

        while(!server.isClientConnected()){
            Thread.sleep(1000);
            System.out.println("##connection status: "+server.isClientConnected());
        }
        System.out.println("##connection status: "+server.isClientConnected());

        server.sendMsg(partial);
        server.sendMsg(update);

    }
}
