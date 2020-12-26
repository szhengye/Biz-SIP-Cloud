package com.bizmda.bizsip.integrator.service;

import cn.hutool.json.JSONObject;
import com.bizmda.bizsip.common.BizMessage;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author shizhengye
 */
@Data
public abstract class AbstractIntegratorService {
    public final static Map<String,Class> SERVICE_SCRIPT_SUFFIX_MAP = new HashMap<String,Class>(){{
        put("script", ScriptIntegratorService.class);
    }};

    private String serviceId;
    private String type;
    private String fileContent;
    public AbstractIntegratorService() {
    }

    public AbstractIntegratorService(String serviceId,String type,String fileContent) {
        this.serviceId = serviceId;
        this.type = type;
        this.fileContent = fileContent;
    }

    /**
     * 服务整合器初始化
     */
    public abstract void init();

    /**
     * 调用聚合服务
     * @param message 传入的消息
     * @return 返回的消息
     */
    public abstract BizMessage doBizService(BizMessage<JSONObject> message);
}
