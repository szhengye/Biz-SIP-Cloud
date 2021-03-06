package com.bizmda.bizsip.serveradaptor.protocol.netty;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author 史正烨
 */
@Component
@Slf4j
public class NettyClient {

    private EventLoopGroup group = new NioEventLoopGroup();
    private Bootstrap bootstrap = new Bootstrap();

    @Value("${netty.port}")
    private Integer port;

    @Value("${netty.host}")
    private String host;

    public NettyClient(String host,Integer port) {
        this.host = host;
        this.port = port;
        this.bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new NettyClientInitializer());
    }
    /**
     * 发送消息
     */
    public Object call(Object msg) {
        //连接服务器
        ChannelFuture future = null;
        try {
            future = bootstrap.connect(this.host, this.port).sync();
        } catch (InterruptedException e) {
            log.error("连接服务器失败",e);
            Thread.currentThread().interrupt();
            return null;
        }
        future.channel().writeAndFlush(msg);
        //当通道关闭了，就继续往下走
        try {
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("通联关闭失败",e);
            Thread.currentThread().interrupt();
            return null;
        }
        //接收服务端返回的数据
        AttributeKey<String> key = AttributeKey.valueOf("ServerData");
        return future.channel().attr(key).get();
    }

}