package com.bizmda.bizsip.integrator.script;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.bizmda.bizsip.common.BizMessage;
import com.bizmda.bizsip.common.BizUtils;
import com.bizmda.bizsip.config.RestServerAdaptorConfig;
import com.bizmda.bizsip.config.ServerAdaptorConfigMapping;
import com.bizmda.bizsip.integrator.controller.IntegratorController;
import org.springframework.web.client.RestTemplate;
import org.ssssssss.magicapi.config.MagicModule;
import org.ssssssss.script.annotation.Comment;

import java.util.Map;

/**
 * @author shizhengye
 */
public class ServerService implements MagicModule {
    public static ServerAdaptorConfigMapping serverAdaptorConfigMapping;

    @Comment("执行适配器服务调用")
    public static BizMessage<JSONObject> doService(@Comment("服务ID") String serviceId, @Comment("调用输入参数") Object inData) {
        JSONObject jsonObject = JSONUtil.parseObj(inData);
        RestTemplate restTemplate = new RestTemplate();

        BizMessage<JSONObject> inMessage = IntegratorController.currentBizMessage.get();
        inMessage.setData(jsonObject);
        RestServerAdaptorConfig serverAdaptorConfig = (RestServerAdaptorConfig) serverAdaptorConfigMapping.getServerAdaptorConfig(serviceId);
        BizMessage<JSONObject> outMessage = (BizMessage)restTemplate.postForObject(serverAdaptorConfig.getUrl(), inMessage, BizMessage.class);
        return outMessage;
    }

    @Override
    public String getModuleName() {
        return "server";
    }
}
