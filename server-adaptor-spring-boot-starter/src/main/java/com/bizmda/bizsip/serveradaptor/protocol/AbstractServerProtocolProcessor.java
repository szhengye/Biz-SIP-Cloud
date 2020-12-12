package com.bizmda.bizsip.serveradaptor.protocol;

import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.config.AbstractServerAdaptorConfig;

public abstract class AbstractServerProtocolProcessor {
    private AbstractServerAdaptorConfig serverAdaptorConfig;
    public abstract Object process(Object inMessage) throws BizException;

    public void init(AbstractServerAdaptorConfig serverAdaptorConfig) throws BizException {
        this.serverAdaptorConfig = serverAdaptorConfig;
    }
}
