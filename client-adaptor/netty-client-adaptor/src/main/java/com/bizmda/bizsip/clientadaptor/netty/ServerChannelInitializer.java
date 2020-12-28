package com.bizmda.bizsip.clientadaptor.netty;

import com.bizmda.bizsip.clientadaptor.ClientAdaptor;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * @author 史正烨
 */
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    private ClientAdaptor clientAdaptor;

    public ServerChannelInitializer(ClientAdaptor clientAdaptor) {
        super();
        this.clientAdaptor = clientAdaptor;
    }
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        //添加编解码
        socketChannel.pipeline().addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
        socketChannel.pipeline().addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));
        socketChannel.pipeline().addLast(new NettyServerHandler(this.clientAdaptor));
    }
}