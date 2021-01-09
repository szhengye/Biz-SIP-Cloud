package com.bizmda.bizsip.integrator.executor;

import cn.hutool.json.JSONObject;
import com.bizmda.bizsip.common.BizMessage;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 史正烨
 */
@Data
public abstract class AbstractIntegratorExecutor {
    public final static Map<String,Class> SERVICE_SCRIPT_SUFFIX_MAP = new HashMap<String,Class>(){{
        put("script", ScriptIntegratorExecutor.class);
    }};

    private String serviceId;
    private String type;
    private String content;
    public AbstractIntegratorExecutor() {
    }

    public AbstractIntegratorExecutor(String serviceId, String type, String content) {
        this.serviceId = serviceId;
        this.type = type;
        this.content = content;
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
