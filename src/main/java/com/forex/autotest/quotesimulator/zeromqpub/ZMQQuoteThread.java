package com.forex.autotest.quotesimulator.zeromqpub;
import org.zeromq.ZMQ.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ZMQQuoteThread extends Thread {

    private String filePath;
    private Socket publisher;
    private int speed=0;
    public volatile boolean exit = false;

    ZMQQuoteThread(Socket publisher){
        this.publisher=publisher;
    }

    void setFilePath(String filePath){this.filePath=filePath;}
    void setSpeed(int speed){this.speed=speed;}

    @Override
    public void run(){
        File file = new File(filePath);
        while(!exit) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String reading;
                String quote;
                while ((reading = reader.readLine()) != null) {
                    quote = reading.substring(reading.lastIndexOf(']') + 1);
                    publisher.send(quote);
                    Thread.sleep(this.speed);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
