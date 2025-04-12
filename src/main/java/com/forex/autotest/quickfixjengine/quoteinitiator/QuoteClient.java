package com.forex.autotest.quickfixjengine.quoteinitiator;

import com.forex.autotest.utility.MessageBuilder;
import org.apache.log4j.Logger;
import quickfix.*;
import quickfix.Message;
import quickfix.field.*;
import quickfix.fix44.*;
import quickfix.fix44.MessageCracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QuoteClient
        extends MessageCracker implements Application {
    private SessionSettings settings = null;
    private ArrayList<String> toAdminMsg = new ArrayList<>();
    private String fromAdminMsg = null;
    private String toAppMsg = null;
    private ArrayList<String> marketDataSnapshotFullRefresh= new ArrayList<>();
    //private String businessMessageReject;
    private String rejectMsg=null;
    private Map<String,Double> price;
    private ArrayList<String> quote=new ArrayList<>();


    public void setter(SessionSettings setting) {
        this.settings = setting;
    }

    public ArrayList<String> getToAdminMsg() {
        return this.toAdminMsg;
    }

    public void resetToAdminMsg() {
        this.toAdminMsg.clear();
    }

    public String getFromAdminMsg() {
        return this.fromAdminMsg;
    }

    public String getToAppMsg() {
        return this.toAppMsg;
    }

    public ArrayList<String> getMarketDataSnapshotFullRefresh() {
        return this.marketDataSnapshotFullRefresh;
    }

    public ArrayList<String> getQuotes(){return  this.quote;}

    public void resetMarketDataSnapshotFullRefresh(){this.marketDataSnapshotFullRefresh.clear();}

    public void resetQuotes(){this.quote.clear();}

    public String getRejectMsg() {
        return this.rejectMsg;
    }


    public void onCreate(SessionID sessionId) {
       // System.out.println("Call onCreate successfully: " + sessionId);
    }

    public void onLogon(SessionID sessionId) {
        //System.out.println("Call onLogon successfully: " + sessionId);
    }

    public void onLogout(SessionID sessionId) {
       // System.out.println("Call onLogout successfully: " + sessionId);
    }

    public void toAdmin(Message message, SessionID sessionId) {
        try {
            if (message.getHeader().getString(35).equals("A")) {
                String userName = this.settings.getString(sessionId, "Username");
                String password = this.settings.getString(sessionId, "Password");
                message.setString(553, userName);
                message.setString(554, password);
            }
            this.toAdminMsg.add(message.toString());
        } catch (FieldNotFound e) {
            Logger.getLogger("EventError").error("toAdmin occurs an error.", e);
        } catch (ConfigError configError) {
            configError.printStackTrace();
        } catch (FieldConvertError fieldConvertError) {
            fieldConvertError.printStackTrace();
        }
    }

    public void fromAdmin(Message message, SessionID sessionId)
            throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
        this.fromAdminMsg = message.toString();
        //System.out.println("From Admin<--" + message.toString());
    }

    public void toApp(Message message, SessionID sessionId)
            throws DoNotSend {

        this.toAppMsg = message.toString();
        //System.out.println("To Application-->" + message.toString());

    }

    public void fromApp(Message message, SessionID sessionId)
            throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue,
            UnsupportedMessageType {
        crack(message, sessionId);
        //System.out.println("From Application<--" + message.toString());
    }




    public void onMessage(MarketDataSnapshotFullRefresh message, SessionID sessionID)
            throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {

        this.marketDataSnapshotFullRefresh.add(message.toString());
    }

    public void onMessage(Quote message,SessionID sessionID)
            throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        this.quote.add(message.toString());

    }

    public void onMessage(MarketDataRequestReject message, SessionID sessionID)
            throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        this.rejectMsg=message.toString();
        //System.out.println("Received MarketDataReject<--" + message.toString());
    }

    public void onMessage(QuoteRequestReject message, SessionID sessionID)
            throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        this.rejectMsg=message.toString();
        //System.out.println("Received QuoteReject<--" + message.toString());
    }


    public void onMessage(Reject message, SessionID sessionID)
            throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        this.rejectMsg=message.toString();

        //System.out.println("Reject<--" + message.toString());
    }

    public void onMessage(BusinessMessageReject message, SessionID sessionID)
            throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {

        //System.out.println("BusinessMessageReject<--" + message.toString());
        this.rejectMsg=message.toString();
    }

    public void marketDataRequest(SessionID sessionID, String symbol,int marketDepth){
        MarketDataRequest marketDataRequest = new MarketDataRequest();
        marketDataRequest.set(new MDReqID(String.valueOf(System.currentTimeMillis())));
        marketDataRequest.set(new SubscriptionRequestType('1'));//tag=263, 1:Snapshot + Updates (Subscribe)
        marketDataRequest.set(new MarketDepth(marketDepth));
        marketDataRequest.set(new MDUpdateType(0));//tag=265, 0:Full Refresh
        MarketDataRequest.NoMDEntryTypes noMDEntryTypes = new MarketDataRequest.NoMDEntryTypes();
        noMDEntryTypes.set(new MDEntryType('0') );
        marketDataRequest.addGroup(noMDEntryTypes);
        noMDEntryTypes.set(new MDEntryType('1'));
        marketDataRequest.addGroup(noMDEntryTypes);
        MarketDataRequest.NoRelatedSym relatedSymbol = new MarketDataRequest.NoRelatedSym();
        relatedSymbol.set(new Symbol(symbol));
        marketDataRequest.addGroup(relatedSymbol);
        marketDataRequest.setField(new SecurityType("FOR"));

        try {
            Session.sendToTarget(marketDataRequest, sessionID);
        } catch (SessionNotFound e) {
            e.printStackTrace();
        }
    }

    public void quoteRequest(SessionID sessionID,String symbol){
        QuoteRequest quoteRequest=new QuoteRequest();
        quoteRequest.setField(new QuoteReqID(MessageBuilder.newQuoteReqID()));
        quoteRequest.setField(new QuoteRequestType(1));
        quoteRequest.setField(new Symbol(symbol));

        try{
            Session.sendToTarget(quoteRequest,sessionID);
        }catch (SessionNotFound e){
            e.printStackTrace();
        }
    }




    public Map<String,Double> getPrice(String symbol, String quote){
        int index;
        double bid;
        double ask;
        if (quote.contains(symbol)&&quote.contains("\u0001269=1\u0001")&&quote.contains("\u0001269=0\u0001")) {
            if(quote.indexOf("\u0001269=1\u0001")<quote.indexOf("\u0001269=0\u0001")) {
                //System.out.println(quotesimulator);
                index = quote.indexOf("\u0001269=1\u0001");
                quote = quote.substring(index + 11);
                ask = Double.parseDouble(quote.substring(0, quote.indexOf("\u0001")));

                index = quote.indexOf("\u0001269=0\u0001");
                quote = quote.substring(index + 11);
                bid = Double.parseDouble(quote.substring(0, quote.indexOf("\u0001")));
            }else{
                index = quote.indexOf("\u0001269=0\u0001");
                quote = quote.substring(index + 11);
                bid = Double.parseDouble(quote.substring(0, quote.indexOf("\u0001")));

                index = quote.indexOf("\u0001269=1\u0001");
                quote = quote.substring(index + 11);
                ask = Double.parseDouble(quote.substring(0, quote.indexOf("\u0001")));
            }
            this.price=new HashMap<>();
            price.put("bid",bid);
            price.put("ask",ask);
            //System.out.println("bid: "+bid);
            //System.out.println("ask: "+ask);
            return price;
        }else{
            return null;
        }

    }

    public String getSymbol(String quote){
        int index;
        if (quote.contains("\u000135=W\u0001")){
            index=quote.indexOf("\u000155=");
            quote=quote.substring(index+4);
            return  quote.substring(0,quote.indexOf("\u0001"));

        }else{
            return null;
        }
    }
}

