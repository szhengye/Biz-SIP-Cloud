package com.bizmda.bizsip.sample.clientadaptor.client1;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.common.BizUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ELTest {
    public static void main(String args[]) {
        String str = "{\"data\": {\n" +
                "    \"service\":\"/a/doService\",\n" +
                "    \"sex\": 0,\n" +
                "    \"name\": \"小明\"\n" +
                "    }\n" +
                "}";
        Map map = new HashMap();
        map.put("name","小明");
        JSONObject jsonObject = JSONUtil.parseObj(str);
        String express = "#{data.getByPath('data.name')}";
        try {
            String result = BizUtils.getElStringResult("#{#data[data][name]}",jsonObject);
            log.info("result:{}",result);
        } catch (BizException e) {
            e.printStackTrace();
        }
    }
}