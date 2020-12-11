package com.bizmda.bizsip.serveradaptor.protocol;

import com.bizmda.bizsip.common.BizException;

public interface JavaProtocolInterface {
    public Object process(Object inMessage) throws BizException;
}
