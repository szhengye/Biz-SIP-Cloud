package com.bizmda.bizsip.message;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.XmlUtil;
import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.config.AbstractServerAdaptorConfig;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class SimpleXmlMessageProcessor extends AbstractMessageProcessor {
    @Override
    public void init(AbstractServerAdaptorConfig serverAdaptor) throws BizException {
        super.init(serverAdaptor);
    }

    @Override
    public Object pack(Object inMessage) throws BizException {
        Document document = XmlUtil.beanToXml(inMessage);
        Node node = document.getFirstChild();
        document.renameNode(node,null,"data");
        return XmlUtil.toStr(document);
    }

    @Override
    public Object unpack(Object inMessage) throws BizException {
        return XmlUtil.xmlToMap(XmlUtil.parseXml((String)inMessage));
    }
}
