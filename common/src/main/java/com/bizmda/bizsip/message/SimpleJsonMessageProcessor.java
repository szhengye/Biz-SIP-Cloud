package com.bizmda.bizsip.message;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.bizmda.bizsip.common.BizException;

/**
 * @author 史正烨
 */
public class SimpleJsonMessageProcessor extends AbstractMessageProcessor<String> {
    @Override
    protected JSONObject biz2json(JSONObject inMessage) throws BizException {
        return inMessage;
    }

    @Override
    protected String json2adaptor(JSONObject inMessage) throws BizException {
        return JSONUtil.toJsonStr(inMessage);
    }

    @Override
    protected JSONObject adaptor2json(String inMessage) throws BizException {
        return JSONUtil.parseObj(inMessage);
    }

    @Override
    protected JSONObject json2biz(JSONObject inMessage) throws BizException {
        return inMessage;
    }
}
