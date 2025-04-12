package com.forex.autotest.utility;

import java.util.ArrayList;
import java.util.HashMap;


public class Decoder {
    private static long uDecodedVal;
    private static ArrayList<String> vecDecodedVals=new ArrayList<>();


    public static String getRawDecodeValue(String id){
        decodeID(id);
        StringBuilder decodeValue= new StringBuilder();
        for (int i = 0; i < vecDecodedVals.size(); i++) {
            if (i != 0){
                decodeValue.append(", ");
            }
            decodeValue.append(vecDecodedVals.get(i));
        }
        return decodeValue.toString();
    }

    public static String getQuoteIdFormat(String id){
        decodeID(id);
        String uSeq = String.format("%04d", Integer.valueOf(Decoder.vecDecodedVals.get(0))),
               uTimestamp = Decoder.vecDecodedVals.get(1);
        return uSeq+'@'+uTimestamp;
    }

    public static String getFullQuoteIdFormat(String id){
        decodeID(id);
        String uSeq = String.format("%04d", Integer.valueOf(Decoder.vecDecodedVals.get(0))),
                uTimestamp = Decoder.vecDecodedVals.get(1);

        StringBuilder decodeValue= new StringBuilder();
        for (int i = 0; i < vecDecodedVals.size(); i++) {
            if (i != 0){
                decodeValue.append(", ");
            }
            decodeValue.append(vecDecodedVals.get(i));
        }


        return uSeq+'@'+uTimestamp+"|"+decodeValue.toString().split(", ")[2];
    }

    public static String getTimeSeqFormat(String id){
        decodeID(id);
        int uTimestamp = Integer.valueOf(Decoder.vecDecodedVals.get(1));
        int totalSeconds = uTimestamp / 1000;
        int wMilliseconds = uTimestamp % 1000;
        int wDay = totalSeconds / 86400;
        int totalDaySeconds = totalSeconds % 86400;
        int wHour = totalDaySeconds / 3600;
        int wMinute = totalDaySeconds % 3600 / 60;
        int wSecond = totalDaySeconds % 3600 % 60;
        String day=String.format("%02d", wDay),
                hour=String.format("%02d", wHour),
                minute=String.format("%02d", wMinute),
                second=String.format("%02d", wSecond),
                millisecond=String.format("%03d", wMilliseconds),
                uSeq = String.format("%04d", Integer.valueOf(Decoder.vecDecodedVals.get(0)));

        return day+"-"+hour+":"+ minute+":"+ second+"."+millisecond +" - "+uSeq;
    }

    static void decodeID(String pszEncodedVal){
        int nRemainValLen = pszEncodedVal.length(), nTotalComsumedLen = 0;
        vecDecodedVals.clear();

        while(nRemainValLen>0){
            uDecodedVal = 0;
            int nComsumedLen = base64Decode(pszEncodedVal.substring(nTotalComsumedLen), nRemainValLen);
            if (nComsumedLen == 0) {
                break;
            }

            vecDecodedVals.add(String.valueOf(uDecodedVal));
            nRemainValLen -= nComsumedLen;
            nTotalComsumedLen += nComsumedLen;
        }
    }
    static int base64Decode(String pszVal,int nValLen){
        if (nValLen <= 0) {
            uDecodedVal = 0;
            return 0;
        }
        //verify the first byte including a valid encoded text length or not, 1 char for the leading length and 0-6 chars for the value
        int nEncodedLen=(pszVal.charAt(0) < 0 || pszVal.charAt(0)>127) ? -1 : checkDict(pszVal.charAt(0));
        if (nEncodedLen < 0 || nEncodedLen>7 || nEncodedLen > nValLen) {
            return 0;
        }

        //decode
        int i = nEncodedLen - 1;
        uDecodedVal = 0;
        for (; i > 0; i--) {
            if (pszVal.charAt(i) < 0 || pszVal.charAt(i)>127 || checkDict(pszVal.charAt(i)) < 0) {
                break;
            }
            uDecodedVal <<= 6;
            uDecodedVal += checkDict(pszVal.charAt(i));
        }

        return nEncodedLen;
    }

    static int checkDict(char c) {
// Parameterize the HashMap to use String as both key and value types
ArrayList<HashMap<String, String>> oct = new ArrayList<HashMap<String, String>>();

        HashMap<String,String> zero=new HashMap<>();
        zero.put("0","-1");zero.put("1","-1");zero.put("2","-1");zero.put("3","-1");zero.put("4","-1");zero.put("5","-1");zero.put("6","-1");zero.put("7","-1");
        zero.put("8","-1");zero.put("9","-1");zero.put("a","-1");zero.put("b","-1");zero.put("c","-1");zero.put("d","-1");zero.put("e","-1");zero.put("f","-1");
        oct.add(zero);
        HashMap<String,String> one=new HashMap<>();
        one.put("0","-1");one.put("1","-1");one.put("2","-1");one.put("3","-1");one.put("4","-1");one.put("5","-1");one.put("6","-1");one.put("7","-1");
        one.put("8","-1");one.put("9","-1");one.put("a","-1");one.put("b","-1");one.put("c","-1");one.put("d","-1");one.put("e","-1");one.put("f","-1");
        oct.add(one);
        HashMap<String,String> two=new HashMap<>();
        two.put("0","-1");two.put("1","-1");two.put("2","-1");two.put("3","-1");two.put("4","-1");two.put("5","-1");two.put("6","-1");two.put("7","-1");
        two.put("8","-1");two.put("9","-1");two.put("a","-1");two.put("b","62");two.put("c","-1");two.put("d","-1");two.put("e","-1");two.put("f","63");
        oct.add(two);
        HashMap<String,String> three=new HashMap<>();
        three.put("0","0");three.put("1","1");three.put("2","2");three.put("3","3");three.put("4","4");three.put("5","5");three.put("6","6");three.put("7","7");
        three.put("8","8");three.put("9","9");three.put("a","-1");three.put("b","-1");three.put("c","-1");three.put("d","-1");three.put("e","-1");three.put("f","-1");
        oct.add(three);
        HashMap<String,String> four=new HashMap<>();
        four.put("0","-1");four.put("1","36");four.put("2","37");four.put("3","38");four.put("4","39");four.put("5","40");four.put("6","41");four.put("7","42");
        four.put("8","43");four.put("9","44");four.put("a","45");four.put("b","46");four.put("c","47");four.put("d","48");four.put("e","49");four.put("f","50");
        oct.add(four);
        HashMap<String,String> five=new HashMap<>();
        five.put("0","51");five.put("1","52");five.put("2","53");five.put("3","54");five.put("4","55");five.put("5","56");five.put("6","57");five.put("7","58");
        five.put("8","59");five.put("9","60");five.put("a","61");five.put("b","-1");five.put("c","-1");five.put("d","-1");five.put("e","-1");five.put("f","-1");
        oct.add(five);
        HashMap<String,String> six=new HashMap<>();
        six.put("0","-1");six.put("1","10");six.put("2","11");six.put("3","12");six.put("4","13");six.put("5","14");six.put("6","15");six.put("7","16");
        six.put("8","17");six.put("9","18");six.put("a","19");six.put("b","20");six.put("c","21");six.put("d","22");six.put("e","23");six.put("f","24");
        oct.add(six);
        HashMap<String,String> seven=new HashMap<>();
        seven.put("0","25");seven.put("1","26");seven.put("2","27");seven.put("3","28");seven.put("4","29");seven.put("5","30");seven.put("6","31");seven.put("7","32");
        seven.put("8","33");seven.put("9","34");seven.put("a","35");seven.put("b","35");seven.put("c","-1");seven.put("d","-1");seven.put("e","-1");seven.put("f","-1");
        oct.add(seven);


        String ascii=Integer.toHexString((int) c);
        return Integer.parseInt(oct.get(Integer.parseInt(String.valueOf(ascii.charAt(0)))).get(String.valueOf(ascii.charAt(1))).toString());
    }

}

