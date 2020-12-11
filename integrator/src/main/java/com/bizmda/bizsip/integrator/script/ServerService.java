package com.bizmda.bizsip.integrator.script;

import com.bizmda.bizsip.common.BizMessage;
import com.bizmda.bizsip.config.RestServerAdaptorConfig;
import com.bizmda.bizsip.config.ServerAdaptorMapping;
import org.springframework.web.client.RestTemplate;
import org.ssssssss.magicapi.config.MagicModule;
import org.ssssssss.script.annotation.Comment;

import java.util.Map;

public class ServerService implements MagicModule {
    public static ServerAdaptorMapping serverAdaptorMapping;

    @Comment("执行适配器服务调用")
    public static Object doService(@Comment("服务ID") String serviceId, @Comment("调用输入参数") Map inData) {
        RestTemplate restTemplate = new RestTemplate();
        BizMessage inMessage = new BizMessage();
        inMessage.setData(inData);
        RestServerAdaptorConfig serverAdaptor = (RestServerAdaptorConfig)serverAdaptorMapping.getServerAdaptor(serviceId);
        BizMessage outMessage = (BizMessage)restTemplate.postForObject(serverAdaptor.getUrl(), inData, BizMessage.class);
        return outMessage;
    }

    @Override
    public String getModuleName() {
        return "server";
    }
}
