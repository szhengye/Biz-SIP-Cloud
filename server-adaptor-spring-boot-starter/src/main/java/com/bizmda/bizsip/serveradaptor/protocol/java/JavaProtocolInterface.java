package com.bizmda.bizsip.serveradaptor.protocol.java;

import com.bizmda.bizsip.common.BizException;

/**
 * @author shizhengye
 */
public interface JavaProtocolInterface {
    /**
     * Java服务调用接口
     * @param inMessage 传入的消息
     * @return 返回值
     * @throws BizException
     */
    public Object process(Object inMessage) throws BizException;
}
