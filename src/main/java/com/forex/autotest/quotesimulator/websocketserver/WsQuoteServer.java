package com.forex.autotest.quotesimulator.websocketserver;

import com.forex.autotest.utility.ZipUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class WsQuoteServer implements Runnable
{
    private static Logger logger=Logger.getLogger(WsQuoteServer.class);
    public WebSocketFrameInHandler webSocketFrameInHandler;
    private int port;
    private String wsUri;
    private SocketChannel channel;
    private ArrayList<String> MessageSent=new ArrayList<>();
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public WsQuoteServer(int port, String uri)
    {
        this.port=port;
        this.wsUri=uri;
    }
    @Override
    public void run()
    {
        bossGroup=new NioEventLoopGroup();
        workerGroup=new NioEventLoopGroup();
        try
        {
            ServerBootstrap serverBootstrap=new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.handler(new LoggingHandler(LogLevel.INFO));
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>()
            {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception
                {
                    channel=ch;
                    webSocketFrameInHandler=new WebSocketFrameInHandler();
                    ChannelPipeline pipeline=ch.pipeline();
                    pipeline.addLast(new HttpServerCodec());
                    pipeline.addLast(new ChunkedWriteHandler());
                    pipeline.addLast(new HttpObjectAggregator(64*1024));
                    pipeline.addLast(new WebSocketServerProtocolHandler(wsUri));
                    pipeline.addLast(webSocketFrameInHandler);
                }
            });

            ChannelFuture future=serverBootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        }catch (InterruptedException e){e.printStackTrace();}
        finally
        {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public boolean isClientConnected()
    {
        if(webSocketFrameInHandler==null||webSocketFrameInHandler.getClientStatus()==false)
            return false;
        else
            return webSocketFrameInHandler.getClientStatus();
    }

    public void sendMsg(String msg)
    {
        byte[] byteMsg= ZipUtil.compress(msg.getBytes());
        ByteBuf byteBuf= Unpooled.buffer();
        byteBuf.writeBytes(byteMsg);
        channel.writeAndFlush(new BinaryWebSocketFrame(byteBuf));
        logger.info("wsServer sent out message: "+msg);
        this.MessageSent.add(msg);

    }

    public List<String> getSentMsg()
    {
        return webSocketFrameInHandler.getSentMsg();
    }

    public void resetSentMsg(){webSocketFrameInHandler.resetSentMsg();}

    public List<String> getReceivedMsg()
    {
        return webSocketFrameInHandler.getReceivedMsg();
    }

    public void resetReceivedMsg(){webSocketFrameInHandler.resetReceivedMsg();}

    public void exit()
    {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }



}
