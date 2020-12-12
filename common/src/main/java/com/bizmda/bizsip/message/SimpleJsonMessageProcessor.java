package com.bizmda.bizsip.message;

import cn.hutool.json.JSONUtil;
import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.config.AbstractServerAdaptorConfig;

import java.util.Map;

public class SimpleJsonMessageProcessor extends AbstractMessageProcessor {
    @Override
    public void init(Map messageMap) throws BizException{
        super.init(messageMap);
    }

    @Override
    public Object pack(Object inMessage) throws BizException {
        return JSONUtil.toJsonStr(inMessage);
    }

    @Override
    public Object unpack(Object inMessage) throws BizException {
        return JSONUtil.parseObj((String)inMessage);
    }
}
