package com.bizmda.bizsip.message;

import cn.hutool.json.JSONUtil;
import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.config.AbstractServerAdaptorConfig;

public class SimpleJsonMessageProcessor extends AbstractMessageProcessor {
    public SimpleJsonMessageProcessor(AbstractServerAdaptorConfig serverAdaptor) {
        super(serverAdaptor);
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
