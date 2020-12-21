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

    public abstract BizMessage doBizService(BizMessage<JSONObject> message);
}
