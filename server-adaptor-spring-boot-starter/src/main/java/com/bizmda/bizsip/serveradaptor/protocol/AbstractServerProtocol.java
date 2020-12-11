package com.bizmda.bizsip.serveradaptor.protocol;

import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.config.AbstractServerAdaptorConfig;

public abstract class AbstractServerProtocol {
    private AbstractServerAdaptorConfig serverAdaptor;
    public abstract Object process(Object inMessage) throws BizException;

    public AbstractServerProtocol(AbstractServerAdaptorConfig serverAdaptor) {
        this.serverAdaptor = serverAdaptor;
    }
}
