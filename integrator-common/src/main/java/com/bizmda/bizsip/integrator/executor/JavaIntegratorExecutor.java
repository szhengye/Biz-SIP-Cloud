package com.bizmda.bizsip.integrator.executor;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import com.bizmda.bizsip.common.BizMessage;
import com.bizmda.bizsip.integrator.service.AbstractJavaIntegratorService;

public class JavaIntegratorExecutor extends AbstractIntegratorExecutor {
    private AbstractJavaIntegratorService javaIntegratorService;

    public JavaIntegratorExecutor(String serviceId, String type, String content) {
        super(serviceId, type, content);
        this.javaIntegratorService = (AbstractJavaIntegratorService)SpringUtil.getBean(serviceId);
    }

    @Override
    public void init() {

    }

    @Override
    public BizMessage doBizService(BizMessage<JSONObject> message) {
        return this.javaIntegratorService.doBizService(message);
    }
}
