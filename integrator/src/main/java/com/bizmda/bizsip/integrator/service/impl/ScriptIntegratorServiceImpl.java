package com.bizmda.bizsip.integrator.service.impl;

import cn.hutool.json.JSONObject;
import com.bizmda.bizsip.common.BizMessage;
import com.bizmda.bizsip.integrator.service.IntegratorService;

/**
 * @author shizhengye
 */
public class ScriptIntegratorServiceImpl implements IntegratorService {
    @Override
    public BizMessage doBizService(String serviceId, BizMessage<JSONObject> message) {
        return null;
    }
}
