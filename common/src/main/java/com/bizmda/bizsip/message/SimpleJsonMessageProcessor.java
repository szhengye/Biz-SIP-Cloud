package com.bizmda.bizsip.message;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.bizmda.bizsip.common.BizException;

import java.util.Map;

/**
 * @author 史正烨
 */
public class SimpleJsonMessageProcessor<String> extends AbstractMessageProcessor {
//    @Override
//    public void init(Map messageMap) throws BizException{
//        super.init(messageMap);
//    }

    @Override
    protected JSONObject biz2json(JSONObject inMessage) throws BizException {
        return inMessage;
    }

    @Override
    protected String json2adaptor(JSONObject inMessage) throws BizException {
        return (String)JSONUtil.toJsonStr(inMessage);
    }

    @Override
    protected JSONObject adaptor2json(Object inMessage) throws BizException {
        return JSONUtil.parseObj((String)inMessage);
    }

    @Override
    protected JSONObject json2biz(JSONObject inMessage) throws BizException {
        return inMessage;
    }
}
