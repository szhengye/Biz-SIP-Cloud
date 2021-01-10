package com.bizmda.bizsip.serveradaptor.protocol;

import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.config.AbstractServerAdaptorConfig;
import com.bizmda.bizsip.serveradaptor.protocol.netty.NettyClient;

/**
 * @author 史正烨
 */
public class NettyServerProtocolProcessor extends AbstractServerProtocolProcessor {
    private NettyClient nettyClient;

    @Override
    public void init(AbstractServerAdaptorConfig serverAdaptorConfig) throws BizException {
        super.init(serverAdaptorConfig);
        String host = (String)serverAdaptorConfig.getProtocolMap().get("host");
        Integer port = (Integer)serverAdaptorConfig.getProtocolMap().get("port");
        this.nettyClient = new NettyClient(host,port);
    }

    @Override
    public Object process(Object inMessage) throws BizException {
        return this.nettyClient.call(inMessage);
    }
}
