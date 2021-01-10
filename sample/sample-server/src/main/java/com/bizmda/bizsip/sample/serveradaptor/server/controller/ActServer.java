package com.bizmda.bizsip.sample.serveradaptor.server.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.serveradaptor.protocol.java.JavaProtocolInterface;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 史正烨
 */
public class ActServer implements JavaProtocolInterface {
    private static final Map<String,Integer> ID_BALANCE_MAP = new HashMap<>();
    static {
        ID_BALANCE_MAP.put("003",300);
        ID_BALANCE_MAP.put("004",400);
        ID_BALANCE_MAP.put("005",500);
    }

    @Override
    public Object process(Object inMessage) throws BizException {
        JSONObject jsonObject = JSONUtil.parseObj(inMessage);
        String accountNo = (String)jsonObject.get("accountNo");
        Integer balance = ID_BALANCE_MAP.get(accountNo);
        if (balance == null) {
            throw new BizException(100,"账户不存在!");
        }
        jsonObject.set("balance",balance);
        return jsonObject.toString();
    }
}
