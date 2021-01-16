package com.bizmda.bizsip.integrator.executor;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import com.bizmda.bizsip.common.BizMessage;
import com.bizmda.bizsip.integrator.service.JavaIntegratorServiceInterface;

public class JavaIntegratorExecutor extends AbstractIntegratorExecutor {
    private JavaIntegratorServiceInterface javaIntegratorService;

    public JavaIntegratorExecutor(String serviceId, String type, String content) {
        super(serviceId, type, content);
        this.javaIntegratorService = (JavaIntegratorServiceInterface)SpringUtil.getBean(serviceId);
    }

    @Override
    public void init() {
        // 没有初始化内容
    }

    @Override
    public BizMessage<JSONObject> doBizService(BizMessage<JSONObject> message) {
        BizMessage<JSONObject> bizMessage = this.javaIntegratorService.doBizService(message);
        return BizMessage.buildJSONObjectMessage(bizMessage,bizMessage.getData());
    }
}
