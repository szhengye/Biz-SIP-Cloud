package com.bizmda.bizsip.integrator.service;

import cn.hutool.json.JSONObject;
import com.bizmda.bizsip.common.BizMessage;

public abstract class AbstractJavaIntegratorService {
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
