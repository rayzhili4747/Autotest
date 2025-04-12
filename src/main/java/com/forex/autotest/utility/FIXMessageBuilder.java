package com.forex.autotest.utility;

import java.util.Random;

public class FIXMessageBuilder {
    public static String buildFIXMessage(String rawString){
        String FIXMessage;
        FIXMessage=rawString.replace(';','\u0001');
        if (FIXMessage.lastIndexOf('\u0001')!=FIXMessage.length()-1){
            FIXMessage=FIXMessage+"\u0001";
        }
        if (!FIXMessage.contains("8=FIX.")){
            FIXMessage="8=FIX.4.4\u00019="+FIXMessage.length()+'\u0001'+FIXMessage;
        }else{
            FIXMessage=FIXMessage.substring(0,10)+"9="+FIXMessage.substring(10).length()+'\u0001'+FIXMessage.substring(10);
        }

        FIXMessage=FIXMessage+"10="+checkSum(FIXMessage)+"\u0001";

        return FIXMessage;
    }

    public static String checkSum(String rawString) {

        String msgBody;
        String checkSum;
        if(rawString.contains('\u0001'+"10=")) {
            msgBody = rawString.substring(0, rawString.indexOf('\u0001'+"10=")+1);
        }else{msgBody=rawString;}
        int sum = 0;
        for (int i = 0; i < msgBody.length(); i++) {

            sum = sum + msgBody.charAt(i);

        }
        if(String.valueOf(sum%256).length()==1){
            checkSum="00"+sum%256;
        }else if(String.valueOf(sum%256).length()==2){
            checkSum="0"+sum%256;
        }else{
            checkSum=String.valueOf(sum%256);
        }
        return checkSum;
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

    static String getQuoteID(){
        String quoteID;
        quoteID=getRandomString(8)+getRandomDigits(17);
        return quoteID;
    }
}
