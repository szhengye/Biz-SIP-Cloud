package com.bizmda.bizsip.serveradaptor.protocol;

import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.config.AbstractServerAdaptorConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * @author shizhengye
 */
public abstract class AbstractServerProtocolProcessor {
    public final static Map<String,Class> protocolTypeMap = new HashMap<String,Class>() {{
        put("java",com.bizmda.bizsip.serveradaptor.protocol.JavaServerProtocolProcessor.class);
    }};

    private AbstractServerAdaptorConfig serverAdaptorConfig;

    /**
     * 协议适配处理模块的对外协议对接实现
     * @param inMessage
     * @return
     * @throws BizException
     */
    public abstract Object process(Object inMessage) throws BizException;

    public void init(AbstractServerAdaptorConfig serverAdaptorConfig) throws BizException {
        this.serverAdaptorConfig = serverAdaptorConfig;
    }
}
