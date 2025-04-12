package com.forex.autotest.utility;

import com.csvreader.CsvWriter;

import java.io.*;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class QuoteLog {
    public static void main(String args[]) throws IOException{
        QuoteLog quoteLog=new QuoteLog();
        long startTime=0;
        long endTime=0;
        startTime=System.currentTimeMillis();

        trimLogFile("A:\\projects\\LP_LOG\\BANK_FIX_IN_20230526\\QUOTE.FOREX.NET_FIX_INITIATOR_forexmd__FIX_IN_20210526.log","A:\\projects\\LP_LOG\\BANK_FIX_IN_20210526\\XT.log", 10000);

                  endTime=System.currentTimeMillis();
        System.out.println("Total: "+(endTime-startTime)+" Milliseconds");

    }

    public static  HashMap<String,String> quoteReqID=new HashMap<>();

    public static ArrayList<String> getSymbolList(String symbolFile){
        File file = new File(symbolFile);
        BufferedReader reader = null;
        ArrayList<String> symbolList=new ArrayList<>();

        try {

            reader = new BufferedReader(new FileReader(file));
            String reading = null;

            while ((reading = reader.readLine()) != null) {
                symbolList.add(reading);
            }

            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return symbolList;
    }

    public static void createLogCSV(String file,ArrayList<String> symbols){


            try {
                // create csv object example: CsvWriter(filepath，separator，charset);
                CsvWriter csvWriter = new CsvWriter(file, ',', Charset.forName("GBK"));
                TreeMap<String,Integer> sortedSymbols=new TreeMap<>();
                ArrayList<String> head=new ArrayList<>();
                head.add("Client");
                head.add("Time");
                head.add("Speed");
                head.add("TotalSymbol");
                for(int i=0;i<symbols.size();i++){
                    sortedSymbols.put(symbols.get(i),0);
                }
                for (String key : sortedSymbols.keySet())
                {
                    head.add(key);
                }

                String[] headSize=new String[head.size()];
                String[] headers =  head.toArray(headSize);
                csvWriter.writeRecord(headers);


                csvWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public  static void writeLogCSV(String file,String[] log){
        try{
            //append contents
            CsvWriter csvWriter;
            BufferedWriter out = new BufferedWriter(new
            OutputStreamWriter(new FileOutputStream(file,true),"GBK"));
            csvWriter = new CsvWriter(out,',');
            csvWriter.writeRecord(log);
            csvWriter.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    public static String[] logToArray(String client,ArrayList<String> symbolList,ArrayList<String> mdCollection){
        TreeMap<String,Integer> quoteQtyOfAllSymbol=new TreeMap<>();
        ArrayList<String> record=new ArrayList<>();
        SimpleDateFormat formatDateTime = new SimpleDateFormat("yyyy_MM_dd_HH:mm");
        record.add(client);
        record.add(formatDateTime.format(Calendar.getInstance().getTime()));
        record.add(String.valueOf(mdCollection.size()));

        for(int i=0;i<symbolList.size();i++){
            quoteQtyOfAllSymbol.put(symbolList.get(i),0);
        }
        TreeMap<String,Integer> quoteQtyOfSymbol=getQuoteQtyBySymbol(mdCollection);
        record.add(String.valueOf(quoteQtyOfSymbol.size()));


        for (Map.Entry<String, Integer> entry : quoteQtyOfSymbol.entrySet())
        {

            String key = entry.getKey();
            Integer value = entry.getValue();
            if(quoteQtyOfAllSymbol.containsKey(key)){
                quoteQtyOfAllSymbol.replace(key,value);
            }
        }
/*
        for(Map.Entry<String,Integer> entry:quoteQtyOfAllSymbol.entrySet()){
            String key = entry.getKey();
            Integer value = entry.getValue();
            System.out.print(key+":"+value+" ");
        }*/

        for (Integer value : quoteQtyOfAllSymbol.values())
        {
            record.add(value.toString());
        }

        String[] recordSize=new String[record.size()];
        String[] log=record.toArray(recordSize);
        return log;

    }


    public static TreeMap<String,Integer> getQuoteQtyBySymbol(ArrayList<String> mdCollection){
        TreeMap<String,Integer> quoteQtyOfSymbol=new TreeMap<>();
        String symbol;
        for (int i=0;i<mdCollection.size();i++){
            symbol=getSymbol(mdCollection.get(i));
            if(symbol!=null&&(!quoteQtyOfSymbol.containsKey(symbol))){
                quoteQtyOfSymbol.put(symbol,1);
            }else if(symbol!=null){
                quoteQtyOfSymbol.replace(symbol,quoteQtyOfSymbol.get(symbol)+1);
            }
        }
        return quoteQtyOfSymbol;
    }

    public static String getSymbol(String quote){
        int index;
        if (quote!=null&&(quote.contains("\u000135=W\u0001")||quote.contains("\u000135=S\u0001")||quote.contains("\u000135=X\u0001")||quote.contains("\u000135=i\u0001"))){
            index=quote.indexOf("\u000155=");
            quote=quote.substring(index+4);
            return  quote.substring(0,quote.indexOf("\u0001"));

        }else{
            return null;
        }
    }

    public static TreeMap<Integer,String> compare_Tick_0MQ_FIXquote(String tickLog,String zmqLog,String FIXLog,String symbol,String resultLog){
        File tickFile = new File(tickLog);
        BufferedReader tickReader = null;
        File zmqFile=new File(zmqLog);
        BufferedReader zmqReader = null;
        File FIXFile=new File(FIXLog);
        BufferedReader FIXReader=null;
        File result = new File(resultLog);
        TreeMap<Integer,String> duQuotes=new TreeMap<>();
        ArrayList<String> ticks=new ArrayList<>();
        ArrayList<String> zmqQuotes=new ArrayList<>();
        ArrayList<String> FIXQuotes=new ArrayList<>();

        try {
            //read log file
            tickReader = new BufferedReader(new FileReader(tickFile));
            zmqReader = new BufferedReader(new FileReader(zmqFile));
            FIXReader=new BufferedReader(new FileReader(FIXFile));
            String reading = null;

            if(!result.exists()){
                result.createNewFile();
            }
            FileWriter fw = new FileWriter(result.getAbsoluteFile(),true);
            BufferedWriter bw = new BufferedWriter(fw);
//*******************Read tick log*********************************************
            while ((reading = tickReader.readLine()) != null) {
                if (reading.contains(symbol)) {
                    ticks.add(reading);
                }
            }
//*******************Read zmq  log*********************************************
            while ((reading = zmqReader.readLine()) != null) {
                if (reading.contains(symbol)) {
                    zmqQuotes.add(reading);
                }
            }
//*******************Read acceptor  log*********************************************
            String preMsg=null;
            while ((reading = FIXReader.readLine()) != null) {

                if (reading.contains(symbol)&&reading.contains(";L1;")) {
                    if (reading.charAt(0)=='[') {
                        reading = reading.substring(reading.indexOf("]") + 1);
                    }
                    if (preMsg == null) {
                        preMsg = reading;
                    } else {
                        if(reading.contains(preMsg.split(";")[13].split("\\|")[0])){
                            if (reading.split(";")[2].equals("0")) {
                                FIXQuotes.add(reading+" ;"+preMsg);

                            }
                            if (reading.split(";")[2].equals("1")) {
                                FIXQuotes.add(preMsg+";"+reading);
                            }

                        }
                        preMsg=reading;
                    }
                }else if(reading.contains(symbol)&&reading.contains("278=L1")&&reading.contains("279=1")) {
                    if (preMsg == null) {
                        preMsg = reading;
                    } else {
                        if(reading.contains(preMsg.substring(preMsg.indexOf("9619=")+5,preMsg.indexOf("|")))){
                            if (reading.contains(" 269=0 ")) {
                                FIXQuotes.add(reading+";"+preMsg);

                            }
                            if (reading.contains(" 269=1 ")) {
                                FIXQuotes.add(preMsg+";"+reading);
                            }
                        }
                        preMsg=reading;
                    }
                }
            }

            System.out.println("Tick size: "+ticks.size());
            System.out.println("0MQ size: "+zmqQuotes.size());
            System.out.println("acceptor size: "+FIXQuotes.size());
            int countMatch=0,countMatch_N=0,caseNum=1;
            ArrayList<String> matchByID=new ArrayList<>();
            int startTime,endTime;

            int c_t=0,c_0=0,c_f=0,c_m=0;
            if(FIXQuotes.get(0).contains("278=L1")){
                startTime=Math.max(getTickMillisecond(ticks.get(0)),Math.max(get0MQMillisecond(zmqQuotes.get(0)),getFIXQuoteillisecond_F(FIXQuotes.get(0))));
                endTime=Math.min(getTickMillisecond(ticks.get(ticks.size()-1)),Math.min(get0MQMillisecond(zmqQuotes.get(zmqQuotes.size()-1)),getFIXQuoteillisecond_F(FIXQuotes.get(FIXQuotes.size()-1))));

                for(String zmqQuote:zmqQuotes){
                    if(startTime <= get0MQMillisecond(zmqQuote)&& get0MQMillisecond(zmqQuote)<= endTime ){
                        c_0++;
                        countMatch++;
                        matchByID.add(zmqQuote);
                        for(String tick:ticks){
                            if(startTime<=getTickMillisecond(tick)&&getTickMillisecond(tick)<=endTime&&tick.contains(getBookId_0MQ(zmqQuote))){
                                c_t++;
                                matchByID.add(tick);
                                ticks.remove(tick);
                                break;

                            }
                        }

                        for(String FIXQuote:FIXQuotes){
                            if(startTime<=getFIXQuoteillisecond_F(FIXQuote)&&getFIXQuoteillisecond_F(FIXQuote)<=endTime&&FIXQuote.contains(getBookId_0MQ(zmqQuote))){
                                c_f++;
                                matchByID.add(FIXQuote);
                                FIXQuotes.remove(FIXQuote);
                                break;

                            }
                        }
                        if(matchByID.size()==3){
                            c_m++;
                            if(!(getCompareKey_0MQquote(matchByID.get(0)).equals(getCompareKey_tick(matchByID.get(1)))&&getCompareKey_FIXQuote_F(matchByID.get(2)).equals(getCompareKey_tick(matchByID.get(1))))){

                                bw.write("case: "+caseNum);
                                bw.newLine();
                                bw.write(matchByID.get(0));
                                bw.newLine();
                                bw.write(matchByID.get(1));
                                bw.newLine();
                                bw.write(matchByID.get(2));
                                bw.newLine();
                                bw.write(getCompareKey_0MQquote(matchByID.get(0)));
                                bw.newLine();
                                bw.write(getCompareKey_tick(matchByID.get(1)));
                                bw.newLine();
                                bw.write(getCompareKey_FIXQuote_F(matchByID.get(2)));
                                bw.newLine();
                                bw.newLine();
                                System.out.println(getCompareKey_0MQquote(matchByID.get(0)));
                                System.out.println(getCompareKey_tick(matchByID.get(1)));
                                System.out.println(getCompareKey_FIXQuote_F(matchByID.get(2)));
                                System.out.println("");
                                countMatch_N++;
                                caseNum++;

                            }


                            matchByID.clear();
                        }else {
                            matchByID.clear();
                        }

                    }
                }
            }else{
                startTime=Math.max(getTickMillisecond(ticks.get(0)),Math.max(get0MQMillisecond(zmqQuotes.get(0)),getFIXQuoteillisecond(FIXQuotes.get(0))));
                endTime=Math.min(getTickMillisecond(ticks.get(ticks.size()-1)),Math.min(get0MQMillisecond(zmqQuotes.get(zmqQuotes.size()-1)),getFIXQuoteillisecond(FIXQuotes.get(FIXQuotes.size()-1))));


                for(String zmqQuote:zmqQuotes){
                    if(startTime <= get0MQMillisecond(zmqQuote)&& get0MQMillisecond(zmqQuote)<= endTime ){
                        countMatch++;
                        matchByID.add(zmqQuote);
                        for(String tick:ticks){
                            if(startTime<=getTickMillisecond(tick)&&getTickMillisecond(tick)<=endTime&&tick.contains(getBookId_0MQ(zmqQuote))){
                                matchByID.add(tick);
                                ticks.remove(tick);
                                break;

                            }
                        }

                        for(String FIXQuote:FIXQuotes){
                            if(startTime<=getFIXQuoteillisecond(FIXQuote)&&getFIXQuoteillisecond(FIXQuote)<=endTime&&FIXQuote.contains(getBookId_0MQ(zmqQuote))){
                                matchByID.add(FIXQuote);
                                FIXQuotes.remove(FIXQuote);
                                break;

                            }
                        }
                        if(matchByID.size()==3){

                            if(!(getCompareKey_0MQquote(matchByID.get(0)).equals(getCompareKey_tick(matchByID.get(1)))&&getCompareKey_FIXQuote(matchByID.get(2)).equals(getCompareKey_tick(matchByID.get(1))))){

                                bw.write("case: "+caseNum);
                                bw.newLine();
                                bw.write(matchByID.get(0));
                                bw.newLine();
                                bw.write(matchByID.get(1));
                                bw.newLine();
                                bw.write(matchByID.get(2));
                                bw.newLine();
                                bw.write(getCompareKey_0MQquote(matchByID.get(0)));
                                bw.newLine();
                                bw.write(getCompareKey_tick(matchByID.get(1)));
                                bw.newLine();
                                bw.write(getCompareKey_FIXQuote(matchByID.get(2)));
                                bw.newLine();
                                bw.newLine();
                                System.out.println(getCompareKey_0MQquote(matchByID.get(0)));
                                System.out.println(getCompareKey_tick(matchByID.get(1)));
                                System.out.println(getCompareKey_FIXQuote(matchByID.get(2)));
                                System.out.println("");
                                countMatch_N++;
                                caseNum++;

                            }


                            matchByID.clear();
                        }else {
                            matchByID.clear();
                        }

                    }
                }
            }
            DecimalFormat df = new DecimalFormat("0.##");
            System.out.println("0mq: "+c_0);
            System.out.println("tick: "+c_t);
            System.out.println("FIX: "+c_f);
            System.out.println("match"+c_m);
            bw.newLine();
            bw.write("Not match percentage: "+df.format(((float)countMatch_N/countMatch)*100)+" %");

            tickReader.close();
            zmqReader.close();
            FIXReader.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (tickReader != null) {
                try {
                    tickReader.close();
                } catch (IOException ignored) {
                }
            }
            if (zmqReader != null) {
                try {
                    zmqReader.close();
                } catch (IOException ignored) {
                }
            }
            if (FIXReader != null) {
                try {
                    FIXReader.close();
                } catch (IOException ignored) {
                }
            }
        }


        return duQuotes;
    }

    public static TreeMap<Integer,String> filterDuplicatedQuote_FIX(String FIXLog,String symbol,String resultLog){
        File file = new File(FIXLog);
        BufferedReader reader = null;
        File result = new File(resultLog);
        TreeMap<Integer,String> duQuotes=new TreeMap<>();
        ArrayList<String> quotes=new ArrayList<>();
        int totalquotes=0,duQuoteCounter=0;


        try {
            //read log file
            reader = new BufferedReader(new FileReader(file));
            String reading = null;
            String bidMsg=null, askMsg=null,previousMsg=null;


            if(!result.exists()){
                result.createNewFile();
            }
            FileWriter fw = new FileWriter(result.getAbsoluteFile(),true);
            BufferedWriter bw = new BufferedWriter(fw);

            int num=1,count=0;
            while ((reading = reader.readLine()) != null) {
                if (reading.contains(symbol)&&reading.contains(";L1;")) {
                    count++;
                    if (reading.indexOf("]") != -1) {
                        reading = reading.substring(reading.indexOf("]") + 1);
                    }
                    if (previousMsg == null) {
                        previousMsg = reading;
                    } else {
                        if(reading.contains(previousMsg.split(";")[13].split("\\|")[0])){
                            if (reading.split(";")[2].equals("0")) {
                                quotes.add(reading+" ;"+previousMsg);

                            }
                            if (reading.split(";")[2].equals("1")) {
                                quotes.add(previousMsg+";"+reading);
                            }

                        }
                        previousMsg=reading;
                    }
                }
            }
            //System.out.println("size; "+quotes.size());
            //System.out.println("count msg: "+count);

            previousMsg=quotes.get(0);
            totalquotes=quotes.size();
            for(int i=1;i<(quotes.size()-1);i++){
                if(getCompareKey_FIXQuote(quotes.get(i)).contains(getCompareKey_FIXQuote(previousMsg))){
                    bw.write("Case: "+num);
                    bw.newLine();
                    bw.write(previousMsg);
                    bw.newLine();
                    bw.write(quotes.get(i));
                    bw.newLine();
                    duQuoteCounter++;
                    num++;

                }
                previousMsg=quotes.get(i);

            }


            DecimalFormat df = new DecimalFormat("0.##");
            bw.newLine();
            bw.newLine();
            bw.write("Duplicated "+symbol+ " quotesimulator Percentage: "+duQuoteCounter+" / "+totalquotes+" = "+df.format(((float)duQuoteCounter/(totalquotes-duQuoteCounter))*100)+" %");

            reader.close();
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return duQuotes;
    }

    public static TreeMap<Integer,String> filterDuplicatedTick(String tickLog,String symbol,String resultLog){
        File file = new File(tickLog);
        BufferedReader reader = null;
        File result = new File(resultLog);
        TreeMap<Integer,String> duQuotes=new TreeMap<>();
        int totalquotes=0,duQuoteCounter=0;

        try {
            //read log file
            reader = new BufferedReader(new FileReader(file));
            String reading = null;
            String buyMsg=null,sellMsg=null,buyTemp,sellTemp,previousMsg=null;


            if(!result.exists()){
                result.createNewFile();
            }
            FileWriter fw = new FileWriter(result.getAbsoluteFile(),true);
            BufferedWriter bw = new BufferedWriter(fw);

            int num=1;
            while ((reading = reader.readLine()) != null) {
                if (reading.contains(symbol)) {
                    totalquotes++;


                    if (buyMsg==null ||sellMsg==null) {
                        //   System.out.println(reading);
                        buyMsg = getBidMsg_tick(reading);
                        sellMsg=getAskMsg_tick(reading);
                        previousMsg=reading;

                    }else{

                        if(buyMsg.contains(getBidMsg_tick(reading))&&sellMsg.contains(getAskMsg_tick(reading))){
                            bw.write("Case: "+num);
                            bw.newLine();
                            bw.write(previousMsg);
                            bw.newLine();
                            bw.write(reading);
                            bw.newLine();
                            bw.newLine();
                            duQuoteCounter++;
                            num++;
                        }

                        previousMsg=reading;
                        buyMsg=getBidMsg_tick(reading);
                        sellMsg=getAskMsg_tick(reading);
                    }
                }
            }



            DecimalFormat df = new DecimalFormat("0.##");
            bw.newLine();
            bw.newLine();
            bw.write("Duplicated "+symbol+ " quotesimulator Percentage: "+duQuoteCounter+" / "+totalquotes+" = "+df.format(((float)duQuoteCounter/(totalquotes-duQuoteCounter))*100)+" %");

            reader.close();
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return duQuotes;
    }

    public static TreeMap<Integer,String> filterDuplicatedTick_MT(String tickLog,String symbol,String resultLog){
        File file = new File(tickLog);
        BufferedReader reader = null;
        File result = new File(resultLog);
        TreeMap<Integer,String> duQuotes=new TreeMap<>();
        int totalquotes=0,duQuoteCounter=0;

        try {
            //read log file
            reader = new BufferedReader(new FileReader(file));
            String reading = null;
            String buyMsg=null,sellMsg=null,previousMsg=null;


            if(!result.exists()){
                result.createNewFile();
            }
            FileWriter fw = new FileWriter(result.getAbsoluteFile(),true);
            BufferedWriter bw = new BufferedWriter(fw);

            int num=1;
            while ((reading = reader.readLine()) != null) {
                if (reading.contains(symbol)) {
                    totalquotes++;


                    if (previousMsg==null) {
                        previousMsg=reading;

                    }else{

                        if(getPrice_tick(reading).equals(getPrice_tick(previousMsg))){
                            bw.write("Case: "+num);
                            bw.newLine();
                            bw.write(previousMsg);
                            bw.newLine();
                            bw.write(reading);
                            bw.newLine();
                            bw.newLine();
                            duQuoteCounter++;
                            num++;
                        }

                        previousMsg=reading;

                    }
                }
            }



            DecimalFormat df = new DecimalFormat("0.##");
            bw.newLine();
            bw.newLine();
            bw.write("Duplicated "+symbol+ " quotesimulator Percentage: "+duQuoteCounter+" / "+totalquotes+" = "+df.format(((float)duQuoteCounter/(totalquotes-duQuoteCounter))*100)+" %");

            reader.close();
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return duQuotes;
    }

    public static TreeMap<Integer,String> filterDuplicatedTickQuoteID(String tickLog,String symbol,String resultLog){
        File file = new File(tickLog);
        BufferedReader reader = null;
        File result = new File(resultLog);
        TreeMap<Integer,String> duQuotes=new TreeMap<>();
        int totalquotes=0,duQuoteCounter=0;

        try {
            //read log file
            reader = new BufferedReader(new FileReader(file));
            String reading = null;
            String previousMsg=null;


            if(!result.exists()){
                result.createNewFile();
            }
            FileWriter fw = new FileWriter(result.getAbsoluteFile(),true);
            BufferedWriter bw = new BufferedWriter(fw);

            int num=1;
            while ((reading = reader.readLine()) != null) {
                if (reading.contains(symbol)) {
                    totalquotes++;


                    if (previousMsg==null) {
                        //   System.out.println(reading);
                        previousMsg=reading;

                    }else{
                        if(reading.contains(getQuoteId_tick(previousMsg))){
                            System.out.println(getQuoteId_tick(reading));
                            bw.write("Case: "+num);
                            bw.newLine();
                            bw.write(previousMsg);
                            bw.newLine();
                            bw.write(reading);
                            bw.newLine();
                            bw.newLine();
                            duQuoteCounter++;
                            num++;
                        }

                        previousMsg=reading;
                    }
                }
            }



            DecimalFormat df = new DecimalFormat("0.##");
            bw.newLine();
            bw.newLine();
            bw.write("Duplicated "+symbol+ " SanpshotID Percentage: "+duQuoteCounter+" / "+totalquotes+" = "+df.format(((float)duQuoteCounter/(totalquotes-duQuoteCounter))*100)+" %");

            reader.close();
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return duQuotes;
    }

    public static TreeMap<Integer,String> findDuplicatedZeroMQinTickLog(String zmqLog,String tickLog,String symbol,String resultLog){
        File zmqFile = new File(zmqLog);
        BufferedReader zmqReader = null;
        File tickFile = new File(tickLog);
        BufferedReader tickReader = null;
        File result = new File(resultLog);
        TreeMap<Integer,String> duBooks=new TreeMap<>();
        int totalBooks=0,duBookCounter=-1;


        try {
            //read log file
            zmqReader = new BufferedReader(new FileReader(zmqFile));
            tickReader = new BufferedReader(new FileReader(tickFile));

            String reading = null;
            String buyMsg=null,sellMsg=null,buyTemp,sellTemp,previousMsg=null;


            if(!result.exists()){
                result.createNewFile();
            }
            FileWriter fw = new FileWriter(result.getAbsoluteFile(),true);
            BufferedWriter bw = new BufferedWriter(fw);


            while ((reading = zmqReader.readLine()) != null) {
                if (reading.contains(symbol)) {
                    totalBooks++;
                    if (buyMsg==null ||sellMsg==null) {
                        //   System.out.println(reading);
                        buyMsg = getBidMsg_0MQ(reading);
                        sellMsg=getAskMsg_0MQ(reading);
                        previousMsg=reading;

                    }else{
                        buyTemp = getBidMsg_0MQ(reading);
                        sellTemp=getAskMsg_0MQ(reading);
                        if(isSamebook_0MQ(buyTemp,buyMsg,sellTemp,sellMsg)){
                            duBooks.put(Integer.parseInt(previousMsg.substring(previousMsg.indexOf('[')+1,previousMsg.indexOf(']'))),previousMsg);
                            duBooks.put(Integer.parseInt(reading.substring(reading.indexOf('[')+1,reading.indexOf(']'))),reading);
                        }

                        previousMsg=reading;
                        buyMsg=buyTemp;
                        sellMsg=sellTemp;
                    }
                }
            }



                if(duBooks.size()!=0) {
                String orgMsg = duBooks.firstEntry().getValue(),
                        groupKey = getMMID_0MQ(getBidMsg_0MQ(orgMsg)) +
                                getPrice_0MQ(getBidMsg_0MQ(orgMsg)) +
                                getQty_0MQ(getBidMsg_0MQ(orgMsg)) +
                                getMMID_0MQ(getAskMsg_0MQ(orgMsg)) +
                                getPrice_0MQ(getAskMsg_0MQ(orgMsg)) +
                                getQty_0MQ(getAskMsg_0MQ(orgMsg));
                int caseNo = 1;
                bw.write("Case: " + caseNo);
                bw.newLine();
                while ((reading = tickReader.readLine()) != null) {
                    if (reading.contains(symbol)) {
                        for (Map.Entry<Integer, String> entry : duBooks.entrySet()) {
                             String entryKey = getMMID_0MQ(getBidMsg_0MQ(entry.getValue())) +
                                    getPrice_0MQ(getBidMsg_0MQ(entry.getValue())) +
                                    getQty_0MQ(getBidMsg_0MQ(entry.getValue())) +
                                    getMMID_0MQ(getAskMsg_0MQ(entry.getValue())) +
                                    getPrice_0MQ(getAskMsg_0MQ(entry.getValue())) +
                                    getQty_0MQ(getAskMsg_0MQ(entry.getValue()));
                            if (reading.contains(getBookId_0MQ(entry.getValue()))) {

                                if (groupKey.equals(entryKey)) {
                                    bw.write(entry.getValue());
                                    bw.newLine();
                                    bw.write(reading);
                                    bw.newLine();
                                    duBookCounter++;
                                } else {


                                    caseNo++;
                                    groupKey = entryKey;
                                    bw.newLine();
                                    bw.write("Case: " + caseNo);
                                    bw.newLine();
                                    bw.write(entry.getValue());
                                    bw.newLine();
                                    bw.write(reading);
                                    bw.newLine();

                                }

                                duBooks.remove(entry.getKey());
                                break;
                            }
                        }
                    }
                }
            }

            DecimalFormat df = new DecimalFormat("0.##");
            bw.newLine();
            bw.newLine();
            bw.write("Duplicated "+symbol+ " Books Percentage: "+duBookCounter+" / "+totalBooks+" = "+df.format(((float)duBookCounter/(totalBooks-duBookCounter))*100)+" %");

            zmqReader.close();
            tickReader.close();
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zmqReader != null) {
                try {
                    zmqReader.close();
                } catch (IOException e1) {
                }
            }
        }
        return duBooks;
    }

    public static TreeMap<Integer,String> filterDuplicatedQuote_0MQ(String inLog,String symbol,String resultLog){
        File file = new File(inLog);
        BufferedReader reader = null;
        File result = new File(resultLog);
        TreeMap<Integer,String> duBooks=new TreeMap<>();
        int totalBooks=0,duBookCounter=-1;

        try {
            //read log file
            reader = new BufferedReader(new FileReader(file));
            String reading = null;
            String buyMsg=null,sellMsg=null,buyTemp,sellTemp,previousMsg=null;


            if(!result.exists()){
                result.createNewFile();
            }
            FileWriter fw = new FileWriter(result.getAbsoluteFile(),true);
            BufferedWriter bw = new BufferedWriter(fw);


            while ((reading = reader.readLine()) != null) {
                if (reading.contains(symbol)) {
                    totalBooks++;
                    if (buyMsg==null ||sellMsg==null) {
                        buyMsg = getBidMsg_0MQ(reading);
                        sellMsg=getAskMsg_0MQ(reading);
                        previousMsg=reading;

                    }else{
                        buyTemp = getBidMsg_0MQ(reading);
                        sellTemp=getAskMsg_0MQ(reading);
                        if(isSamebook_0MQ(buyTemp,buyMsg,sellTemp,sellMsg)){
                            duBooks.put(Integer.parseInt(previousMsg.substring(previousMsg.indexOf('[')+1,previousMsg.indexOf(']'))),previousMsg);
                            duBooks.put(Integer.parseInt(reading.substring(reading.indexOf('[')+1,reading.indexOf(']'))),reading);
                        }

                        previousMsg=reading;
                        buyMsg=buyTemp;
                        sellMsg=sellTemp;
                    }
                }
            }


            if (duBooks.size()!=0) {
                String orgMsg = duBooks.firstEntry().getValue(),
                        groupKey = getMMID_0MQ(getBidMsg_0MQ(orgMsg)) +
                                getPrice_0MQ(getBidMsg_0MQ(orgMsg)) +
                                getQty_0MQ(getBidMsg_0MQ(orgMsg)) +
                                getMMID_0MQ(getAskMsg_0MQ(orgMsg)) +
                                getPrice_0MQ(getAskMsg_0MQ(orgMsg)) +
                                getQty_0MQ(getAskMsg_0MQ(orgMsg));
                int num=1;
                bw.write("Case: "+num);
                bw.newLine();
                for (Map.Entry<Integer, String> entry : duBooks.entrySet()) {

                    String entryKey = getMMID_0MQ(getBidMsg_0MQ(entry.getValue())) +
                            getPrice_0MQ(getBidMsg_0MQ(entry.getValue())) +
                            getQty_0MQ(getBidMsg_0MQ(entry.getValue())) +
                            getMMID_0MQ(getAskMsg_0MQ(entry.getValue())) +
                            getPrice_0MQ(getAskMsg_0MQ(entry.getValue())) +
                            getQty_0MQ(getAskMsg_0MQ(entry.getValue()));
                    if (groupKey.equals(entryKey)) {
                        bw.write(entry.getValue());
                        bw.newLine();
                        duBookCounter++;
                    } else {
                        groupKey = entryKey;
                        bw.newLine();
                        num++;
                        bw.write("Case: "+num);
                        bw.newLine();
                        bw.write(entry.getValue());
                        bw.newLine();

                    }
                }
            }


            DecimalFormat df = new DecimalFormat("0.##");
            bw.newLine();
            bw.newLine();
            bw.write("Duplicated "+symbol+ " Books Percentage: "+duBookCounter+" / "+totalBooks+" = "+df.format(((float)duBookCounter/(totalBooks-duBookCounter))*100)+" %");

            reader.close();
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return duBooks;
    }

    public static TreeMap<Integer,String> filterDuplicatedQuote_0MQ_MT(String inLog,String symbol,String resultLog){
        File file = new File(inLog);
        BufferedReader reader = null;
        File result = new File(resultLog);
        TreeMap<Integer,String> duBooks=new TreeMap<>();
        int totalBooks=0,duBookCounter=-1;

        try {
            //read log file
            reader = new BufferedReader(new FileReader(file));
            String reading = null;
            String buyMsg=null,sellMsg=null,buyTemp,sellTemp,previousMsg=null;


            if(!result.exists()){
                result.createNewFile();
            }
            FileWriter fw = new FileWriter(result.getAbsoluteFile(),true);
            BufferedWriter bw = new BufferedWriter(fw);


            while ((reading = reader.readLine()) != null) {
                if (reading.contains(symbol)) {
                    totalBooks++;
                    if (buyMsg==null ||sellMsg==null) {
                        buyMsg = getBidMsg_0MQ(reading);
                        sellMsg=getAskMsg_0MQ(reading);
                        previousMsg=reading;

                    }else{
                        buyTemp = getBidMsg_0MQ(reading);
                        sellTemp=getAskMsg_0MQ(reading);
                        if(getPrice_0MQ(buyTemp)==getPrice_0MQ(buyMsg)&&getPrice_0MQ(sellTemp)==getPrice_0MQ(sellMsg)){
                            duBooks.put(Integer.parseInt(previousMsg.substring(previousMsg.indexOf('[')+1,previousMsg.indexOf(']'))),previousMsg);
                            duBooks.put(Integer.parseInt(reading.substring(reading.indexOf('[')+1,reading.indexOf(']'))),reading);
                        }

                        previousMsg=reading;
                        buyMsg=buyTemp;
                        sellMsg=sellTemp;
                    }
                }
            }


            if (duBooks.size()!=0) {
                String orgMsg = duBooks.firstEntry().getValue(),
                        groupKey = getPrice_0MQ(getBidMsg_0MQ(orgMsg))+"|" +
                                getPrice_0MQ(getAskMsg_0MQ(orgMsg));

                int num=1;
                bw.write("Case: "+num);
                bw.newLine();
                for (Map.Entry<Integer, String> entry : duBooks.entrySet()) {

                    String entryKey = getPrice_0MQ(getBidMsg_0MQ(entry.getValue())) +"|"+
                            getPrice_0MQ(getAskMsg_0MQ(entry.getValue())) ;
                    if (groupKey.equals(entryKey)) {
                        bw.write(entry.getValue());
                        bw.newLine();
                        duBookCounter++;
                    } else {
                        groupKey = entryKey;
                        bw.newLine();
                        num++;
                        bw.write("Case: "+num);
                        bw.newLine();
                        bw.write(entry.getValue());
                        bw.newLine();

                    }
                }
            }


            DecimalFormat df = new DecimalFormat("0.##");
            bw.newLine();
            bw.newLine();
            bw.write("Duplicated "+symbol+ " Books Percentage: "+duBookCounter+" / "+totalBooks+" = "+df.format(((float)duBookCounter/(totalBooks-duBookCounter))*100)+" %");

            reader.close();
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return duBooks;
    }

    public static TreeMap<Integer,String> filterDuplicatedQuote_0MQ_xRing(String inLog,String symbol,String resultLog){
        File file = new File(inLog);
        BufferedReader reader = null;
        File result = new File(resultLog);
        TreeMap<Integer,String> duBooks=new TreeMap<>();
        int totalBooks=0,duBookCounter=-1;

        try {
            //read log file
            reader = new BufferedReader(new FileReader(file));
            String reading = null;
            String buyMsg=null,sellMsg=null,buyTemp,sellTemp,previousMsg=null;


            if(!result.exists()){
                result.createNewFile();
            }
            FileWriter fw = new FileWriter(result.getAbsoluteFile(),true);
            BufferedWriter bw = new BufferedWriter(fw);

            int preNum=0;
            while ((reading = reader.readLine()) != null) {
                preNum++;
                reading="["+preNum+"]"+reading;
               // System.out.println(reading);
                if (reading.contains(symbol)) {
                    totalBooks++;
                    if (buyMsg==null ||sellMsg==null) {
                        buyMsg = getBidMsg_0MQ(reading);
                        sellMsg=getAskMsg_0MQ(reading);
                        previousMsg=reading;

                    }else{
                        buyTemp = getBidMsg_0MQ(reading);
                        sellTemp=getAskMsg_0MQ(reading);
                        if(isSamebook_0MQ(buyTemp,buyMsg,sellTemp,sellMsg)){
                            duBooks.put(Integer.parseInt(previousMsg.substring(previousMsg.indexOf('[')+1,previousMsg.indexOf(']'))),previousMsg);
                            duBooks.put(Integer.parseInt(reading.substring(reading.indexOf('[')+1,reading.indexOf(']'))),reading);
                        }

                        previousMsg=reading;
                        buyMsg=buyTemp;
                        sellMsg=sellTemp;
                    }
                }
            }


            if (duBooks.size()!=0) {
                String orgMsg = duBooks.firstEntry().getValue(),
                        groupKey = getMMID_0MQ(getBidMsg_0MQ(orgMsg)) +
                                getPrice_0MQ(getBidMsg_0MQ(orgMsg)) +
                                getQty_0MQ(getBidMsg_0MQ(orgMsg)) +
                                getMMID_0MQ(getAskMsg_0MQ(orgMsg)) +
                                getPrice_0MQ(getAskMsg_0MQ(orgMsg)) +
                                getQty_0MQ(getAskMsg_0MQ(orgMsg));
                int num=1;
                bw.write("Case: "+num);
                bw.newLine();
                for (Map.Entry<Integer, String> entry : duBooks.entrySet()) {

                    String entryKey = getMMID_0MQ(getBidMsg_0MQ(entry.getValue())) +
                            getPrice_0MQ(getBidMsg_0MQ(entry.getValue())) +
                            getQty_0MQ(getBidMsg_0MQ(entry.getValue())) +
                            getMMID_0MQ(getAskMsg_0MQ(entry.getValue())) +
                            getPrice_0MQ(getAskMsg_0MQ(entry.getValue())) +
                            getQty_0MQ(getAskMsg_0MQ(entry.getValue()));
                    if (groupKey.equals(entryKey)) {
                        bw.write(entry.getValue());
                        bw.newLine();
                        duBookCounter++;
                    } else {
                        groupKey = entryKey;
                        bw.newLine();
                        num++;
                        bw.write("Case: "+num);
                        bw.newLine();
                        bw.write(entry.getValue());
                        bw.newLine();

                    }
                }
            }


            DecimalFormat df = new DecimalFormat("0.##");
            bw.newLine();
            bw.newLine();
            bw.write("Duplicated "+symbol+ " Books Percentage: "+duBookCounter+" / "+totalBooks+" = "+df.format(((float)duBookCounter/(totalBooks-duBookCounter))*100)+" %");

            reader.close();
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return duBooks;
    }

    public static void analyseTickGap(String tickLog,String symbol,String resultLog){
        File file = new File(tickLog);
        BufferedReader reader = null;
        File result = new File(resultLog);
        int totalTicks=0, count_0=0,count_1=0,count_1_5=0,count_over5=0;

        try {
            //read log file
            reader = new BufferedReader(new FileReader(file));
            String reading = null;

            if(!result.exists()){
                result.createNewFile();
            }
            FileWriter fw = new FileWriter(result.getAbsoluteFile(),true);
            BufferedWriter bw = new BufferedWriter(fw);

            int ms;
            while ((reading = reader.readLine()) != null) {
                totalTicks++;
                  //  System.out.println(checkTickLatency(reading));
                 ms=checkTickLatency(reading);
                 if(ms==0){count_0++;}
                 else if(ms==1){count_1++;}
                 else if(ms>1&&ms<=5){count_1_5++;}
                 else {
                     if(count_over5==0){bw.write("Tick Latency is over 5 ms: ");}
                     bw.newLine();
                     bw.write(reading);
                     count_over5++;
                 }
            }


            bw.newLine();
            bw.newLine();
            DecimalFormat df = new DecimalFormat("0.##");
            bw.write("Gap = 0ms Percentage: "+count_0+" / "+totalTicks+" = "+df.format(((float)count_0/totalTicks)*100)+" %");
            bw.newLine();
            bw.write("Gap = 1ms Percentage: "+count_1+" / "+totalTicks+" = "+df.format(((float)count_1/totalTicks)*100)+" %");
            bw.newLine();
            bw.write("Gap > 1ms and <=5ms Percentage: "+count_1_5+" / "+totalTicks+" = "+df.format(((float)count_1_5/totalTicks)*100)+" %");
            bw.newLine();
            bw.write("Gap > 5ms Percentage: "+count_over5+" / "+totalTicks+" = "+df.format(((float)count_over5/totalTicks)*100)+" %");
            reader.close();
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    public static void filterSpecifiedTicks(String tickLog,String symbol,String resultLog){
        File file = new File(tickLog);
        BufferedReader reader = null;
        File result = new File(resultLog);

        try {
            //read log file
            reader = new BufferedReader(new FileReader(file));
            String reading = null;
            String buyMsg=null,sellMsg=null,buyTemp,sellTemp,previousMsg=null;


            if(!result.exists()){
                result.createNewFile();
            }
            FileWriter fw = new FileWriter(result.getAbsoluteFile(),true);
            BufferedWriter bw = new BufferedWriter(fw);

            int num=1;
            while ((reading = reader.readLine()) != null) {
                if (reading.contains(symbol)) {
                    bw.write(reading);
                    bw.newLine();
                }
            }



            reader.close();
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }

    }

    //pending
    public static String countTicksByMinute(String tickLog,String symbol,String minute,String resultLog){
        File file = new File(tickLog);
        BufferedReader reader = null;
        File result = new File(resultLog);
        int tickCounter=0;

        try {
            //read log file
            reader = new BufferedReader(new FileReader(file));
            String reading = null;
            String buyMsg=null,sellMsg=null,previousMsg=null;


            if(!result.exists()){
                result.createNewFile();
            }
            FileWriter fw = new FileWriter(result.getAbsoluteFile(),true);
            BufferedWriter bw = new BufferedWriter(fw);

            int num=1;
            while ((reading = reader.readLine()) != null) {
                System.out.println(reading);
                if (reading.split("\\|")[1].contains(minute)&&reading.contains(symbol)) {
                    System.out.println(reading);
                    tickCounter++;


                }
            }



            DecimalFormat df = new DecimalFormat("0.##");
            bw.newLine();
            bw.newLine();
            bw.write("total ticks : "+tickCounter+ " in: "+minute);

            reader.close();
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return "hello tick";
    }


    static int checkTickLatency(String tick){
        String time1=tick.split("\\|")[0].split("-")[1];
        int hour=Integer.parseInt(time1.split(":")[0])*60*60*1000;
        int minute=Integer.parseInt(time1.split(":")[1])*60*1000;
        int second=Integer.parseInt(time1.split(":")[2].split("\\.")[0])*1000;
        int millisecond=Integer.parseInt(time1.split(":")[2].split("\\.")[1]);
        int milliTime1=hour+minute+second+millisecond;
        String time2=tick.split("\\|")[1].split("-")[1];
        hour=Integer.parseInt(time2.split(":")[0])*60*60*1000;
        minute=Integer.parseInt(time2.split(":")[1])*60*1000;
        second=Integer.parseInt(time2.split(":")[2].split("\\.")[0])*1000;
        millisecond=Integer.parseInt(time2.split(":")[2].split("\\.")[1]);
        int milliTime2=hour+minute+second+millisecond;
        return milliTime1-milliTime2;

    }

    static int getTickMillisecond(String tick){
        String time=tick.split("\\|")[1].split("-")[1];
        int hour=Integer.parseInt(time.split(":")[0])*60*60*1000;
        int minute=Integer.parseInt(time.split(":")[1])*60*1000;
        int second=Integer.parseInt(time.split(":")[2].split("\\.")[0])*1000;
        int millisecond=Integer.parseInt(time.split(":")[2].split("\\.")[1]);
        return hour+minute+second+millisecond;

    }

    static int get0MQMillisecond(String zmqQuote){
        String time=zmqQuote.substring(zmqQuote.indexOf("]")+1).substring(1,zmqQuote.substring(zmqQuote.indexOf("]")+1).indexOf("]")).split("-")[1];
        int hour=Integer.parseInt(time.split(":")[0])*60*60*1000;
        int minute=Integer.parseInt(time.split(":")[1])*60*1000;
        int second=Integer.parseInt(time.split(":")[2].split("\\.")[0])*1000;
        int millisecond=Integer.parseInt(time.split(":")[2].split("\\.")[1]);
        return hour+minute+second+millisecond;
    }

    static int getFIXQuoteillisecond(String FIXQuote){
        String time=FIXQuote.split(";")[5].split("-")[1];
        int hour=Integer.parseInt(time.split(":")[0])*60*60*1000;
        int minute=Integer.parseInt(time.split(":")[1])*60*1000;
        int second=Integer.parseInt(time.split(":")[2].split("\\.")[0])*1000;
        int millisecond=Integer.parseInt(time.split(":")[2].split("\\.")[1]);
        return hour+minute+second+millisecond;
    }

    static int getFIXQuoteillisecond_F(String FIXQuote){
        String time=FIXQuote.substring(FIXQuote.indexOf("-")+1,FIXQuote.indexOf(" 278=L1"));
        int hour=Integer.parseInt(time.split(":")[0])*60*60*1000;
        int minute=Integer.parseInt(time.split(":")[1])*60*1000;
        int second=Integer.parseInt(time.split(":")[2].split("\\.")[0])*1000;
        int millisecond=Integer.parseInt(time.split(":")[2].split("\\.")[1]);
        return hour+minute+second+millisecond;
    }

    static String getCompareKey_0MQquote(String zmpQuote){
        String compareKey=getMMID_0MQ(getBidMsg_0MQ(zmpQuote))+" "+getPrice_0MQ(getBidMsg_0MQ(zmpQuote))+" "+getQty_0MQ(getBidMsg_0MQ(zmpQuote))
                +" "+getMMID_0MQ(getAskMsg_0MQ(zmpQuote))+" "+getPrice_0MQ(getAskMsg_0MQ(zmpQuote))+" "+getQty_0MQ(getAskMsg_0MQ(zmpQuote));
        return compareKey;
    }

    static String getCompareKey_tick(String tick){
        String[] msg=tick.split("\\|");
        return msg[4]+" "+Double.parseDouble(msg[5])+" "+Double.parseDouble(msg[6])*1000+" "+msg[9]+" "+Double.parseDouble(msg[10])+" "+Double.parseDouble(msg[11])*1000;

    }

    static String getCompareKey_FIXQuote(String FIXQuote){
        String[] msg=FIXQuote.split(";");
        //return msg[1]+" "+msg[3]+" "+msg[4]+" "+msg[19]+" "+msg[21]+" "+msg[22];
        return msg[1]+" "+Double.parseDouble(msg[3])+" "+Double.parseDouble(msg[4])+" "+msg[19]+" "+Double.parseDouble(msg[21])+" "+Double.parseDouble(msg[22]);
    }

    static String getCompareKey_FIXQuote_F(String FIXQuote){
        String bid=FIXQuote.split(";")[0];
        String ask=FIXQuote.split(";")[1];
        String mmid_bid=bid.substring(bid.indexOf("9611=")+5,bid.indexOf(" 9616=0"));
        String mmid_ask=ask.substring(ask.indexOf("9611=")+5,ask.indexOf(" 9616=0"));
        double price_bid=Double.parseDouble(bid.substring(bid.indexOf("270=")+4,bid.indexOf(" 271=")));
        double price_ask=Double.parseDouble(ask.substring(ask.indexOf("270=")+4,ask.indexOf(" 271=")));
        double qty_bid=Double.parseDouble(bid.substring(bid.indexOf("271=")+4,bid.indexOf(" 273=")));
        double qty_ask=Double.parseDouble(ask.substring(ask.indexOf("271=")+4,ask.indexOf(" 273=")));

        return mmid_bid+" "+price_bid+" "+qty_bid+" "+mmid_ask+" "+price_ask+" "+qty_ask;
    }

    static String getBidMsg_tick(String tick){
        String[] msg=tick.split("\\|");
        return msg[4]+'|'+msg[5]+'|'+msg[6];
    }

    static String getAskMsg_tick(String tick){
        String[] msg=tick.split("\\|");
        return msg[9]+'|'+msg[10]+'|'+msg[11];
    }

    static String getPrice_tick(String tick){
        String[] msg=tick.split("\\|");
        return msg[5]+'|'+msg[10];
    }



    static String getQuoteId_tick(String tick){
        String[] msg=tick.split("\\|");
        return msg[3];
    }

    static String getBookId_0MQ(String book){
        for(int i=0;i<2;i++) {
            book=book.substring(book.indexOf("\u0001")+1);
        }

        return Decoder.getQuoteIdFormat(book.substring(0,book.indexOf("\u0001")));

    }

    static boolean isSamebook_0MQ(String buyTemp,String buyMsg,String sellTemp,String sellMsg){
        return (getMMID_0MQ(buyTemp).equals(getMMID_0MQ(buyMsg))&&getMMID_0MQ(sellTemp).equals(getMMID_0MQ(sellMsg)))
                && (getPrice_0MQ(buyTemp)==getPrice_0MQ(buyMsg)&&getPrice_0MQ(sellTemp)==getPrice_0MQ(sellMsg))
                && (getQty_0MQ(buyTemp)==getQty_0MQ(buyMsg)&&getQty_0MQ(sellTemp)==getQty_0MQ(sellMsg));
    }

    static String getBidMsg_0MQ(String book){
        return book.substring(book.indexOf("\u00010\u0001"), book.indexOf("\u00011\u0001"));
    }

    static String getAskMsg_0MQ(String book){
        if (book.indexOf("\u00011\u0001") - book.indexOf("\u00010\u0001") > 2) {
            return book.substring(book.indexOf("\u00011\u0001"));
        } else {
            return book.substring(book.indexOf("\u00011\u0001")).substring(book.indexOf("\u00011\u0001"));
        }
    }

    static String getMMID_0MQ(String msg){
        String mmid="";
        try{
        for(int i=0;i<3;i++) {
            msg=msg.substring(msg.indexOf("\u0001")+1);

        }
            mmid=msg.substring(0,msg.indexOf("\u0001"));
        }catch(StringIndexOutOfBoundsException e){
            System.out.println("NO MMID");
        }

        return mmid;
    }

    static double getPrice_0MQ(String msg){
        for(int i=0;i<4;i++) {
            msg=msg.substring(msg.indexOf("\u0001")+1);
        }
        double price=Double.parseDouble(msg.substring(0,msg.indexOf("\u0001")));
        return price;
    }

    static double getQty_0MQ(String msg){
        for(int i=0;i<5;i++) {
            msg=msg.substring(msg.indexOf("\u0001")+1);
        }
        double qty=Double.parseDouble(msg.substring(0,msg.indexOf("\u0001")));
        return qty;
    }


    public static String buildQuote(String rawQuote,String tag49,String tag56,int tag34,String tag131){
        String quote="";
        String quoteBody="";
        String regex;

    //    ArrayList<String> tags=new ArrayList<>();
        SimpleDateFormat formatDateTime = new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SSS");

            if(rawQuote.contains("\u000149=")){
                regex=rawQuote.substring(rawQuote.indexOf("\u000149="),rawQuote.indexOf("\u0001",rawQuote.indexOf("\u000149=")+1));
                quote=rawQuote.replaceAll(regex,"\u000149="+tag49);
            }
            if(rawQuote.contains("\u000156=")){
                regex=rawQuote.substring(rawQuote.indexOf("\u000156="),rawQuote.indexOf("\u0001",rawQuote.indexOf("\u000156=")+1));
                quote=quote.replaceAll(regex,"\u000156="+tag56);
            }
            if(rawQuote.contains("\u000134=")){
                regex=rawQuote.substring(rawQuote.indexOf("\u000134="),rawQuote.indexOf("\u0001",rawQuote.indexOf("\u000134=")+1));
                quote=quote.replaceAll(regex,"\u000134="+tag34);
            }
            if(rawQuote.contains("\u000152=")){
                regex=rawQuote.substring(rawQuote.indexOf("\u000152="),rawQuote.indexOf("\u0001",rawQuote.indexOf("\u000152=")+1));
                quote=quote.replaceAll(regex,"\u000152="+formatDateTime.format(Calendar.getInstance().getTime()));
            }
            if(rawQuote.contains("\u0001131")){
                regex=rawQuote.substring(rawQuote.indexOf("\u0001131="),rawQuote.indexOf("\u0001",rawQuote.indexOf("\u0001131")+1));
                quote=quote.replaceAll(regex,"\u0001131="+tag131);
            }
            if(rawQuote.contains("\u0001117")){
                regex=rawQuote.substring(rawQuote.indexOf("\u0001117="),rawQuote.indexOf("\u0001",rawQuote.indexOf("\u0001117")+1));
                quote=quote.replaceAll(regex,"\u0001117="+FIXMessageBuilder.getQuoteID());
            }

            quoteBody=quote.substring(rawQuote.indexOf("\u0001",rawQuote.indexOf("\u00019=")+1)+1)+'\u0001';

            regex=rawQuote.substring(rawQuote.indexOf("\u00019="),rawQuote.indexOf("\u0001",rawQuote.indexOf("\u00019=")+1));
            quote=quote.replaceAll(regex,"\u00019="+quoteBody.length())+'\u0001';


            quote=quote+"10="+FIXMessageBuilder.checkSum(quote)+'\u0001';

            return quote;
    }

    public static String buildBook(String rawBook,String tag49,String tag56,int tag34){
        String book="";
        String bookBody="";
        String regex;

        //    ArrayList<String> tags=new ArrayList<>();
        SimpleDateFormat formatDateTime = new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SSS");

        if(rawBook.contains("\u000149=")){
            regex=rawBook.substring(rawBook.indexOf("\u000149="),rawBook.indexOf("\u0001",rawBook.indexOf("\u000149=")+1));
            book=rawBook.replaceAll(regex,"\u000149="+tag49);
        }
        if(rawBook.contains("\u000156=")){
            regex=rawBook.substring(rawBook.indexOf("\u000156="),rawBook.indexOf("\u0001",rawBook.indexOf("\u000156=")+1));
            book=book.replaceAll(regex,"\u000156="+tag56);
        }
        if(rawBook.contains("\u000134=")){
            regex=rawBook.substring(rawBook.indexOf("\u000134="),rawBook.indexOf("\u0001",rawBook.indexOf("\u000134=")+1));
            book=book.replaceAll(regex,"\u000134="+tag34);
        }
        if(rawBook.contains("\u000152=")){
            regex=rawBook.substring(rawBook.indexOf("\u000152="),rawBook.indexOf("\u0001",rawBook.indexOf("\u000152=")+1));
            book=book.replaceAll(regex,"\u000152="+formatDateTime.format(Calendar.getInstance().getTime()));
        }

        bookBody=book.substring(rawBook.indexOf("\u0001",rawBook.indexOf("\u00019=")+1)+1)+'\u0001';

        regex=rawBook.substring(rawBook.indexOf("\u00019="),rawBook.indexOf("\u0001",rawBook.indexOf("\u00019=")+1));
        book=book.replaceAll(regex,"\u00019="+bookBody.length())+'\u0001';


        book=book+"10="+FIXMessageBuilder.checkSum(book)+'\u0001';

        return book;
    }

    public static String buildBookNQS(String rawBook,String tag49,String tag56,int tag34,String timeZone){
        String book="";
        String bookBody="";
        String regex;

        //    ArrayList<String> tags=new ArrayList<>();
        SimpleDateFormat formatDateTime = new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SSS");

        if(rawBook.contains("\u000149=")){
            regex=rawBook.substring(rawBook.indexOf("\u000149="),rawBook.indexOf("\u0001",rawBook.indexOf("\u000149=")+1));
            book=rawBook.replaceAll(regex,"\u000149="+tag49);
        }
        if(rawBook.contains("\u000156=")){
            regex=rawBook.substring(rawBook.indexOf("\u000156="),rawBook.indexOf("\u0001",rawBook.indexOf("\u000156=")+1));
            book=book.replaceAll(regex,"\u000156="+tag56);
        }
        if(rawBook.contains("\u000134=")){
            regex=rawBook.substring(rawBook.indexOf("\u000134="),rawBook.indexOf("\u0001",rawBook.indexOf("\u000134=")+1));
            book=book.replaceAll(regex,"\u000134="+tag34);
        }

        if(rawBook.contains("\u000152=")){
            regex=rawBook.substring(rawBook.indexOf("\u000152="),rawBook.indexOf("\u0001",rawBook.indexOf("\u000152=")+1));
            book=book.replaceAll(regex,"\u000152="+MessageBuilder.getTime(timeZone));
        }

        bookBody=book.substring(rawBook.indexOf("\u0001",rawBook.indexOf("\u00019=")+1)+1)+'\u0001';

        regex=rawBook.substring(rawBook.indexOf("\u00019="),rawBook.indexOf("\u0001",rawBook.indexOf("\u00019=")+1));
        book=book.replaceAll(regex,"\u00019="+bookBody.length())+'\u0001';


        book=book+"10="+FIXMessageBuilder.checkSum(book)+'\u0001';

        return book;
    }


    public static int getSentTime(String quote){
        String time=quote.substring(quote.indexOf("\u000152=")+4);
        time=time.substring(0,time.indexOf("\u0001")).split("-")[1];
        int hour=Integer.parseInt(time.split(":")[0])*60*60*1000;
        int minute=Integer.parseInt(time.split(":")[1])*60*1000;
        int second=Integer.parseInt(time.split(":")[2].split("\\.")[0])*1000;
        int millisecond=Integer.parseInt(time.split(":")[2].split("\\.")[1]);
        return hour+minute+second+millisecond;

    }

    public static String getQuoteReqID(String quote){
        String quoteReqID=null;
        String symbol;

        if(quote.contains("\u000135=i")){
            symbol = quote.substring(quote.indexOf("\u000155=") + 4);
        }else {
            symbol = quote.substring(quote.indexOf("\u000155=") + 4, quote.indexOf("\u0001", quote.indexOf("\u000155=") + 1));
        }
            quoteReqID=QuoteLog.quoteReqID.get(symbol);

        return quoteReqID;
    }

    public static String getQuoteReqIDbySymbol(String symbol){
        String quoteReqID=null;
        quoteReqID=QuoteLog.quoteReqID.get(symbol);

        return quoteReqID;
    }

    public static boolean ifSubscribe(String quote){
        String symbol;
        if(quote.contains("\u000135=i")){
            symbol = quote.substring(quote.indexOf("\u000155=") + 4);
        }else {
            symbol = quote.substring(quote.indexOf("\u000155=") + 4, quote.indexOf("\u0001", quote.indexOf("\u000155=") + 1));
        }
        return QuoteLog.quoteReqID.containsKey(symbol);
    }

    public static String buildSnapshot(String rawQuote,String tag49,String tag56,int tag34){
        String quote=null;
        String quoteBody;
        int bodyLenght;
        Map<String,String> tags=new HashMap<>();
        for(String tag:rawQuote.substring(0,rawQuote.indexOf("\u000155=")).split("\u0001")){
            tags.put(tag.split("=")[0],tag.split("=")[1]);
        }
        tags.replace("49",tag49);
        tags.replace("56",tag56);
        tags.replace("34",String.valueOf(tag34));
        SimpleDateFormat formatDateTime = new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SSS");
        tags.replace("52",formatDateTime.format(Calendar.getInstance().getTime()));
        quoteBody="35="+tags.get("35")+"\u000149="+tags.get("49")+"\u000156="+tags.get("56")+"\u000134="+tags.get("34")+"\u000152="+tags.get("52")+rawQuote.substring(rawQuote.indexOf("\u000155="))+'\u0001';
        bodyLenght=quoteBody.length();
        quote="8="+tags.get("8")+"\u00019="+bodyLenght+'\u0001'+quoteBody;
        quote=quote+"10="+FIXMessageBuilder.checkSum(quote)+'\u0001';
        //System.out.println(quotesimulator);

        return quote;
    }



    public static ArrayList<String> trimQuoteLog(String fileName,int totalQuotes) {
        File file = new File(fileName);
        BufferedReader reader = null;
        int line = 0;
        ArrayList<String> quoteLogs=new ArrayList<>();

        try {
            reader = new BufferedReader(new FileReader(file));
            String reading = null;
            String quote=null;

            while ((reading = reader.readLine()) != null) {
                quote=reading.substring(reading.indexOf("8=FIX"),reading.indexOf('\u0001'+"10="));
                if(quote.contains("35=S")||quote.contains("35=W")){
                    quoteLogs.add(quote);
                    line++;
                }


                if (line==totalQuotes){break;}
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return quoteLogs;
    }

    public static void trimLogFile(String logFile,String trimedLog, int totalLines) {
        File file = new File(logFile);
        BufferedReader reader = null;
        File result = new File(trimedLog);
        int lines=0;

        try {
            //read log file
            reader = new BufferedReader(new FileReader(file));
            String reading = null;

            if(!result.exists()){
                result.createNewFile();
            }
            FileWriter fw = new FileWriter(result.getAbsoluteFile(),true);
            BufferedWriter bw = new BufferedWriter(fw);

            while ((reading = reader.readLine()) != null) {
                   bw.write(reading);
                   bw.newLine();
                   lines++;
                   if (lines == totalLines) {
                       break;
                   }

            }

            reader.close();
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }

    }

    public static ArrayList<String> readQuoteLog(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        int line = 0;
        ArrayList<String> quoteLogs=new ArrayList<>();

        try {
            reader = new BufferedReader(new FileReader(file));
            String reading = null;
            String quote=null;

            while ((reading = reader.readLine()) != null) {
                quote=reading.substring(reading.indexOf("8=FIX"),reading.indexOf('\u0001'+"10="));
                if(quote.contains("35=S")||quote.contains("35=W")){
                    quoteLogs.add(quote);
                    line++;
                }
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return quoteLogs;
    }
}
