package com.bizmda.bizsip.sample.serveradaptor.server.controller;

import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.serveradaptor.protocol.java.JavaProtocolInterface;
import lombok.extern.slf4j.Slf4j;

/**
 * @author shizhengye
 */
@Slf4j
public class EchoServer implements JavaProtocolInterface {
    @Override
    public Object process(Object inMessage) throws BizException {
        log.debug("EchoServer传入消息:[{}]",inMessage);
        return inMessage;
    }
}
