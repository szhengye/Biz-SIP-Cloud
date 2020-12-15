package com.bizmda.bizsip.serveradaptor.protocol;

import com.bizmda.bizsip.common.BizException;

/**
 * @author shizhengye
 */
public interface JavaProtocolInterface {
    public Object process(Object inMessage) throws BizException;
}
