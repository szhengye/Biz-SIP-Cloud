package com.bizmda.bizsip.clientadaptor.netty;
import com.bizmda.bizsip.clientadaptor.ClientAdaptor;
import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.common.BizMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 史正烨
 */
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    private ClientAdaptor clientAdaptor;
    public NettyServerHandler(ClientAdaptor clientAdaptor) {
        super();
        this.clientAdaptor = clientAdaptor;
    }

    /**
     * 客户端连接会触发
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("Channel active......");
    }

    /**
     * 客户端发消息会触发
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //TODO:TCP传输过程中的分包拆包问题有待后期解决，建议采用添加不同解码器的方式来处理
        log.debug("服务器收到消息: {}", msg.toString());
        BizMessage outMessage = null;
        try {
            outMessage = this.clientAdaptor.process(msg);
        } catch (BizException e) {
            log.error("客户端适配器执行出错!",e);
            ctx.disconnect();
            return;
        }
        ctx.write(outMessage.getData());
        ctx.flush();
        ctx.close();
    }

    /**
     * 发生异常触发
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
