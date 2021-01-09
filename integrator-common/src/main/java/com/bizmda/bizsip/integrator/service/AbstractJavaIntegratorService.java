package com.bizmda.bizsip.integrator.service;

import cn.hutool.json.JSONObject;
import com.bizmda.bizsip.common.BizMessage;

/**
 * Java聚合服务抽象父类
 */
public abstract class AbstractJavaIntegratorService {
    /**
     * 聚合服务类的初始化
     */
    public abstract void init();

    /**
     * 执行聚合服务
     * @param message 传入的消息
     * @return 返回的消息
     */
    public abstract BizMessage doBizService(BizMessage<JSONObject> message);
}
