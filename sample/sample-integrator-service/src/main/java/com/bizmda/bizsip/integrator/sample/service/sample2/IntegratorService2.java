package com.bizmda.bizsip.integrator.sample.service.sample2;

import cn.hutool.json.JSONObject;
import com.bizmda.bizsip.common.BizMessage;
import com.bizmda.bizsip.integrator.service.JavaIntegratorServiceInterface;
import com.bizmda.bizsip.integrator.service.SipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("/sample2/service2")
public class IntegratorService2 implements JavaIntegratorServiceInterface {
    @Autowired
    private SipService sipService;

    @Override
    public void init() {
        // 没有初始化操作
    }

    @Override
    public BizMessage doBizService(BizMessage<JSONObject> message) {
        // 类似于"/openapi/safservice.script"
        this.sipService.setTmDelayTime(1000);
        return this.sipService.doSafService("/openapi/safservice1",message.getData());
    }
}
