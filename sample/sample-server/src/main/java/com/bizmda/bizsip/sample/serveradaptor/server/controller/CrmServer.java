package com.bizmda.bizsip.sample.serveradaptor.server.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.serveradaptor.protocol.java.JavaProtocolInterface;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 史正烨
 */
@Slf4j
public class CrmServer implements JavaProtocolInterface {
    private static final Map<String,String> ID_NAME_MAP = new HashMap<>();
    static {
        ID_NAME_MAP.put("003","张三");
        ID_NAME_MAP.put("004","李四");
        ID_NAME_MAP.put("005","王五");
    }

    @Override
    public Object process(Object inMessage) throws BizException {
        JSONObject jsonObject = JSONUtil.parseObj(inMessage);
        String accountNo = (String)jsonObject.get("accountNo");
        String accountName = ID_NAME_MAP.get(accountNo);
        if (accountName == null) {
            throw new BizException(100,"账户不存在!");
        }
        jsonObject.set("accountName",accountName);
        return jsonObject.toString();
    }
}
