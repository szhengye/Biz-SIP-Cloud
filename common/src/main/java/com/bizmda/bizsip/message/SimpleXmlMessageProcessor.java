package com.bizmda.bizsip.message;

import cn.hutool.core.util.XmlUtil;
import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.config.AbstractServerAdaptorConfig;

public class SimpleXmlMessageProcessor extends AbstractMessageProcessor {
    @Override
    public void init(AbstractServerAdaptorConfig serverAdaptor) throws BizException {
        super.init(serverAdaptor);
    }

    @Override
    public Object pack(Object inMessage) throws BizException {
        return XmlUtil.toStr(XmlUtil.beanToXml(inMessage));
    }

    @Override
    public Object unpack(Object inMessage) throws BizException {
        return XmlUtil.xmlToMap(XmlUtil.parseXml((String)inMessage));
    }
}
