package com.forex.autotest.quickfixjengine.tradeinitiator;

import org.apache.log4j.Logger;
import quickfix.*;
import quickfix.Message;
import quickfix.MessageCracker;
import quickfix.field.*;
import quickfix.fix44.*;
//import quickfix.fix44.*;
//import quickfix.fix44.MessageCracker;

import java.util.ArrayList;

public class TradeClient
        extends MessageCracker implements Application {
    private SessionSettings settings = null;
    private ArrayList<String> toAdminMsg = new ArrayList<>();
    private String fromAdminMsg = null;
    private String toAppMsg = null;
    private ArrayList<String> executionReport = new ArrayList<>();
    private String cancelOrderReject = null;
    private String marketDataSnapshotFullRefresh;
    private String businessMessageReject;
    private String reject;


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

    public ArrayList<String> getExecutionReport() {
        return this.executionReport;
    }

    public void resetExecutionReport() {
        this.executionReport.clear();
    }

    public String getOrderCancelReject() {
        return this.cancelOrderReject;
    }

    public String getMarketDataSnapshotFullRefresh() {
        return this.marketDataSnapshotFullRefresh;
    }

    public String getBusinessMessageReject() {
        return this.businessMessageReject;
    }

    public String getReject() {
        return this.reject;
    }


    public void onCreate(SessionID sessionId) {
        System.out.println("Call onCreate successfully: " + sessionId);
    }

    public void onLogon(SessionID sessionId) {
        System.out.println("Call onLogon successfully: " + sessionId);
    }

    public void onLogout(SessionID sessionId) {
        System.out.println("Call onLogout successfully: " + sessionId);
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
        System.out.println("\n" + "To Admin-->" + message.toString());
    }

    public void fromAdmin(Message message, SessionID sessionId)
            throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
        this.fromAdminMsg = message.toString();
        System.out.println("From Admin<--" + message.toString());
    }

    public void toApp(Message message, SessionID sessionId)
            throws DoNotSend {

        this.toAppMsg = message.toString();
        System.out.println("To Application-->" + message.toString());

    }

    public void fromApp(Message message, SessionID sessionId)
            throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue,
            UnsupportedMessageType {
        crack(message, sessionId);
        System.out.println("From Application<--" + message.toString());
    }


    public void onMessage(ExecutionReport message, SessionID sessionID)
            throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {

        System.out.println("Received Execution report<--" + message.toString());
        this.executionReport.add(message.toString());
    }

    public void onMessage(OrderCancelReject message, SessionID sessionID)
            throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {

        System.out.println("Received OrderCancelReject<--" + message.toString());
        this.cancelOrderReject = message.toString();
    }

    public void onMessage(MarketDataSnapshotFullRefresh message, SessionID sessionID)
            throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {

        System.out.println("Received MarketDataSnapshotFullRefresh<--" + message.toString());
        this.marketDataSnapshotFullRefresh = message.toString();
    }

    public void onMessage(MarketDataRequestReject message, SessionID sessionID)
            throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {

        System.out.println("Received MarketDataReject<--" + message.toString());
    }

    public void onMessage(Reject message, SessionID sessionID)
            throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        this.reject = message.toString();

        System.out.println("Reject<--" + message.toString());
    }

    public void onMessage(BusinessMessageReject message, SessionID sessionID)
            throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {

        System.out.println("BusinessMessageReject<--" + message.toString());
        this.businessMessageReject = message.toString();
    }


    //place market order
    public void newOrderSingle_MARKET(SessionID sessionID, String account, Double orderQty,
                                     char orderType, char side, String symbol, char timeInForce, String securityType) {

        NewOrderSingle newOrderSingle = new NewOrderSingle();
        //Account:Tag=1: Maker02
        newOrderSingle.set(new Account(account));
        //ClOrdID:Tag=11: current time
        newOrderSingle.set(new ClOrdID(String.valueOf(System.currentTimeMillis())));
        //Quantity:Tag=38: Double
        newOrderSingle.set(new OrderQty(orderQty));
        //order type:Tag=40: '1' is Market;'2' is Limit; '3' is Stop; '4' is StopLimit
        newOrderSingle.set(new OrdType(orderType));
        //side:Tag=54: '1' is Buy; '2' is Sell
        newOrderSingle.set(new Side(side));
        //Symbol:Tag=55
        newOrderSingle.set(new Symbol(symbol));
        //Time in force:Tag=59: '1' is GOOD_TILL_CANCEL; '3' is IMMEDIATE_OR_CANCEL; '4' is FILL_OR_KILL
        newOrderSingle.set(new TimeInForce(timeInForce));
        //Security Type:Tag=167: "FOR" is FOREIGN_EXCHANGE_CONTRACT
        newOrderSingle.set(new SecurityType(securityType));
        //Time of transaction :Tag=60
        newOrderSingle.set(new TransactTime());
        //HandlInst (Tag = 21, Type: char) Instructions for order handling on Broker trading floor
        //newOrderSingle.set(new HandlInst(HandlInst.AUTOMATED_EXECUTION_ORDER_PRIVATE_NO_BROKER_INTERVENTION));
        newOrderSingle.set(new HandlInst(HandlInst.AUTOMATED_EXECUTION_ORDER_PRIVATE));
        try {
            Session.sendToTarget(newOrderSingle, sessionID);
        } catch (SessionNotFound e) {
            e.printStackTrace();
        }
    }


    //place limit order
    public void newOrderSingle_LIMIT(SessionID sessionID, String account, Double orderQty,
                                    char orderType, char side, String symbol, double price, char timeInForce, String securityType) {

        NewOrderSingle newOrderSingle = new NewOrderSingle();
        //Account:Tag=1: Maker02
        newOrderSingle.set(new Account(account));
        //ClOrdID:Tag=11: current time
        newOrderSingle.set(new ClOrdID(String.valueOf(System.currentTimeMillis())));
        //Quantity:Tag=38: Double
        newOrderSingle.set(new OrderQty(orderQty));
        //order type:Tag=40: '1' is Market;'2' is Limit; '3' is Stop; '4' is StopLimit
        newOrderSingle.set(new OrdType(orderType));
        //side:Tag=54: '1' is Buy; '2' is Sell
        newOrderSingle.set(new Side(side));
        //Symbol:Tag=55
        newOrderSingle.set(new Symbol(symbol));
        //Price: Tag=44
        newOrderSingle.set(new Price(price));
        //Time in force:Tag=59: '1' is GOOD_TILL_CANCEL; '3' is IMMEDIATE_OR_CANCEL; '4' is FILL_OR_KILL
        newOrderSingle.set(new TimeInForce(timeInForce));
        //Security Type:Tag=167: "FOR" is FOREIGN_EXCHANGE_CONTRACT
        newOrderSingle.set(new SecurityType(securityType));
        //Time of transaction :Tag=60
        newOrderSingle.set(new TransactTime());
        //HandlInst (Tag = 21, Type: char) Instructions for order handling on Broker trading floor
        //newOrderSingle.set(new HandlInst(HandlInst.AUTOMATED_EXECUTION_ORDER_PRIVATE_NO_BROKER_INTERVENTION));
        newOrderSingle.set(new HandlInst(HandlInst.AUTOMATED_EXECUTION_ORDER_PRIVATE));

        try {
            Session.sendToTarget(newOrderSingle, sessionID);
        } catch (SessionNotFound e) {
            e.printStackTrace();
        }
    }


    //build a customized fix message by string connected via ';'
    public void newFixMessage(SessionID sessionID, String messageType, String fixMessageBody) {
        Message message = new Message();
        Message.Header header = message.getHeader();
        header.setField(new MsgType(messageType));

        String[] messageBody = fixMessageBody.split(";");
        for (int i = 0; i < messageBody.length; i++) {
            String[] tagValue;
            tagValue = messageBody[i].split("=");
            if (tagValue.length == 2 && tagValue[0].matches("\\d+")) {
                message.setString(Integer.parseInt(tagValue[0]), tagValue[1]);
            } else if (tagValue.length == 1 && tagValue[0].matches("\\d+")) {
                message.setString(Integer.parseInt(tagValue[0]), "");
            }
        }

        try {
            Session.sendToTarget(message, sessionID);
        } catch (SessionNotFound e) {
            e.printStackTrace();
        }
    }
}

