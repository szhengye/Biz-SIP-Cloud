package com.bizmda.bizsip.integrator.service;

import cn.hutool.json.JSONObject;
import com.bizmda.bizsip.common.BizMessage;

/**
 * @author shizhengye
 */
public interface IntegratorService {
    public BizMessage doBizService(String serviceId, BizMessage<JSONObject> message);
}
