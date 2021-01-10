package com.bizmda.bizsip.message;

import cn.hutool.core.util.XmlUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.XML;
import com.bizmda.bizsip.common.BizException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author 史正烨
 */
public class SimpleXmlMessageProcessor extends AbstractMessageProcessor<String> {

    @Override
    protected JSONObject biz2json(JSONObject inMessage) throws BizException {
        return inMessage;
    }

    @Override
    protected String json2adaptor(JSONObject inMessage) throws BizException {
        Document document = XmlUtil.beanToXml(inMessage);
        Node node = document.getFirstChild();
        document.renameNode(node,null,"root");
        return XmlUtil.toStr(document);
    }

    @Override
    protected JSONObject adaptor2json(String inMessage) throws BizException {
        JSONObject jsonObject = XML.toJSONObject(inMessage);
        Object[] keys = jsonObject.keySet().toArray();
        if (keys.length == 1) {
            Object jsonObject1 = jsonObject.get(keys[0]);
            if (jsonObject1 instanceof JSONObject) {
                return (JSONObject)jsonObject1;
            }
        }
        return jsonObject;
    }

    @Override
    protected JSONObject json2biz(JSONObject inMessage) throws BizException {
        return inMessage;
    }

}
