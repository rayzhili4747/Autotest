package com.forex.autotest.quotesimulator.websocketsecureclient;

import org.java_websocket.client.DefaultSSLWebSocketClientFactory;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import javax.net.ssl.*;
import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Map;

public class WssClient extends WebSocketClient {
    public String tok;
    public String account;
    public String password;
    public String statusMsg;
    public String msg;
    public String reqConfirm;
    public ArrayList<String> msgs=new ArrayList<>();

    public WssClient(URI serverURI) {
        super(serverURI);
        if (serverURI.toString().contains("wss://"))
            trustAllHosts(this);
    }

    public WssClient(URI serverURI, Draft draft) {
        super(serverURI, draft);
        if (serverURI.toString().contains("wss://"))
            trustAllHosts(this);
    }

    public WssClient(URI serverURI, Draft draft, Map<String, String> headers, int connectTimeout) {
        super(serverURI, draft, headers, connectTimeout);
        if (serverURI.toString().contains("wss://"))
            trustAllHosts(this);
    }

    public void setTok(String tok){
        this.tok=tok;
    }
    public void setAccount(String account){
        this.account=account;
    }
    public void setPassword(String password){
        this.password=password;
    }

    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };


    static void trustAllHosts(WssClient appClient) {
        System.out.println("start...");
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }
            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            }
            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            }
        }};

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            appClient.setWebSocketFactory(new DefaultSSLWebSocketClientFactory(sc));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int arg0, String arg1, boolean arg2) {
        System.out.println(String.format("onClose:【%s】【%s】【%s】", arg0, arg1, arg2));
    }

    @Override
    public void onError(Exception arg0) {
        System.out.println(String.format("onError:%s", arg0));

    }

    @Override
    public void onMessage(String arg0) {
        System.out.println(String.format("onMessage:%s", arg0));
        this.msg=arg0;
        this.msgs.add(arg0);
        if(arg0.contains("\"MT\":\"ReqConfirm\""))
            this.reqConfirm=arg0;
        this.send("{\"MT\":\"Ack\"}");
    }

    @Override
    public void onOpen(ServerHandshake arg0) {
        System.out.println(String.format("onOpen:%s", arg0));
        statusMsg="Connected";
        this.send("{\"AMT\":\"Login\",\"UserInfo\":{\"login\":\""+account+"\",\"password\":\""+password+"\"},\"Tok\":\""+tok+"\"}");
                    
    }

    public String getMsg(){
        return this.msg;
    }

    public String getReqConfirm(){return this.reqConfirm;}

    public ArrayList<String> getMsgs(){return this.msgs;}

    public void resetMsgs(){this.msgs.clear();}

    public String getStatusMsg(){
        return this.statusMsg;
    }
}
