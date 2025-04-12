package com.forex.autotest.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MessageBuilder {
     public static String buildFIXMessage(String rawString) {
        StringBuilder fixMessageBuilder = new StringBuilder(rawString.replace(';', '\u0001'));

        if (fixMessageBuilder.lastIndexOf("\u0001") != fixMessageBuilder.length() - 1) {
            fixMessageBuilder.append("\u0001");
        }

        int prefixIndex = fixMessageBuilder.indexOf("8=FIX.");
        if (prefixIndex == -1) {
            fixMessageBuilder.insert(0, "8=FIX.4.4\u00019=" + fixMessageBuilder.length() + "\u0001");
        } else {
            fixMessageBuilder.insert(prefixIndex + 10, "9=" + (fixMessageBuilder.length() - (prefixIndex + 10)) + "\u0001");
        }

        fixMessageBuilder.append("10=").append(checkSum(fixMessageBuilder.toString())).append("\u0001");

        return fixMessageBuilder.toString();
    }

    public static String checkSum(String rawString) {
        int sum = 0;
        int length = rawString.length();
        int endIndex = rawString.contains("\u000110=") ? rawString.indexOf("\u000110=") + 1 : length;

        for (int i = 0; i < endIndex; i++) {
            sum = (sum + rawString.charAt(i)) % 256;
        }

        return String.format("%03d", sum);
    }


    static String getRandomString(int length){
        String str="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random=new Random();
        StringBuffer temp=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(26);
            temp.append(str.charAt(number));
        }
        return temp.toString();
    }

    static String getRandomDigits(int length){
        String str="0123456789";
        Random random=new Random();
        StringBuffer temp=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(10);
            temp.append(str.charAt(number));
        }
        return temp.toString();
    }

    public static String newQuoteID(){
        String quoteID;
        quoteID=getRandomString(8)+getRandomDigits(17);
        return quoteID;
    }

    public static String newClOrderID(){
        String orderID;
        orderID=getRandomString(2)+getRandomDigits(8);
        return orderID;
    }

    public static String newQuoteReqID(){
        String orderID;
        orderID=getRandomString(1)+getRandomDigits(9);
        return orderID;
    }


    public static String newExecID(){
        return "EXEC"+getRandomDigits(26);
    }

    public static String transferTimeToGMT(String time, String NowTimeZone) {
        if(time.isEmpty()){
            return "";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(NowTimeZone));
        Date date;
        try {
            date = simpleDateFormat.parse(time);
        } catch (ParseException e) {
            return "0";
        }
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        return simpleDateFormat.format(date);
    }

    public static String getTime(){
        SimpleDateFormat formatDateTime = new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SSS");
        return formatDateTime.format(Calendar.getInstance().getTime());
    }

    public static String getFIXTagValue(String msg,String tagNum){
        String tagValue;
        int startIndex;
        startIndex=tagNum.length()+2;
        try {
            tagValue = msg.substring(msg.indexOf("\u0001" + tagNum + "=") + startIndex);
            tagValue = tagValue.substring(0, tagValue.indexOf("\u0001"));
        }catch (Exception e){
            return null;
        }
        return tagValue;
    }

    public static char randomDigit(String digits){
        Random random=new Random();
        return digits.charAt(random.nextInt(digits.length()));
    }

    public static double randomQty(double maxDouble){
        Random random=new Random();
        double d=random.nextDouble();
        while(d<0.1) d=random.nextDouble();
        return (double)Math.round(d*maxDouble*10)/10;
    }

    public static String getTime(String timeZone){//"Asia/Shanghai"
        SimpleDateFormat formatDateTime = new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SSS");
        return MessageBuilder.transferTimeToGMT(formatDateTime.format(Calendar.getInstance().getTime()),timeZone);
    }
}
