package com.forex.autotest.quotesimulator.websocketserver;

import com.forex.autotest.utility.ZipUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@ChannelHandler.Sharable
public class WebSocketFrameInHandler extends SimpleChannelInboundHandler<WebSocketFrame>
{
    private final static Logger logger= Logger.getLogger(WebSocketFrameInHandler.class);
    private List<String> receivedMsg=new ArrayList<String>();
    private List<String> sentMsg=new ArrayList<>();
    private boolean isClientConnected=false;

    @Override
    public void channelRead0(ChannelHandlerContext ctx,WebSocketFrame msg)
    {
        System.out.println("receive:" +msg);
        if (msg instanceof PingWebSocketFrame){
            ctx.channel().writeAndFlush(new PongWebSocketFrame(msg.content().retain()));
        }
        else if (msg instanceof BinaryWebSocketFrame) {
            BinaryWebSocketFrame bFrame=(BinaryWebSocketFrame)msg;
            logger.info("Received binary message: "+bFrame.content().toString());
            receivedMsg.add(bFrame.content().toString());
        }
        else if (msg instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame=(TextWebSocketFrame)msg;
            logger.info("Received text message: "+textFrame.text());
            receivedMsg.add(textFrame.text());

            if(textFrame.text().contains("login"))
            {
                String tmpMsg="{\"event\":\"login\",\"code\":\"0\",\"msg\":\"\"}";
                sendMsg(ctx,tmpMsg);
                logger.info("wsServer send out text message: "+tmpMsg);
            }else if(textFrame.text().contains("ping"))
            {
                sendMsg(ctx,"pong");
                //ctx.writeAndFlush(new TextWebSocketFrame("pong"));
                logger.info("wsServer sent out text message: "+"pong");
            }
        }
        logger.info("ws received: "+msg);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    {
        logger.error(cause.getMessage());
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception
    {
        System.out.println(ctx.channel().remoteAddress()+" Client connected.");
        isClientConnected=true;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
        isClientConnected=false;
        System.out.println(ctx.channel().remoteAddress()+" Client disconnected.");
    }

    public void sendMsg(ChannelHandlerContext ctx,String msg)
    {
        byte[] byteMsg= ZipUtil.compress(msg.getBytes());
        ByteBuf byteBuf= Unpooled.buffer();
        byteBuf.writeBytes(byteMsg);
        ctx.writeAndFlush(new BinaryWebSocketFrame(byteBuf));
        logger.info("ws sent: "+msg);
        this.sentMsg.add(msg);
    }


    public List<String> getReceivedMsg()
    {
        return this.receivedMsg;
    }
    public List<String> getSentMsg(){return this.sentMsg;}
    public void resetReceivedMsg(){this.receivedMsg.clear();}
    public void resetSentMsg(){this.sentMsg.clear();}




    protected boolean getClientStatus()
    {
        return isClientConnected;
    }

}
