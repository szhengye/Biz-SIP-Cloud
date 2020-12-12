package com.bizmda.bizsip.sample.serveradaptor.server1.controller;

import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.serveradaptor.protocol.JavaProtocolInterface;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Server1 implements JavaProtocolInterface {
    @Override
    public Object process(Object inMessage) throws BizException {
        log.debug("Java执行类传入消息:{}",inMessage);
        return inMessage;
    }
}
