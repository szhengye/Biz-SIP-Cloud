package com.bizmda.bizsip.integrator.sample.service.server2;

import cn.hutool.json.JSONObject;
import com.bizmda.bizsip.common.BizMessage;
import com.bizmda.bizsip.integrator.service.AbstractJavaIntegratorService;
import com.bizmda.bizsip.integrator.service.SipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("/server2/service1")
public class IntegratorService1 extends AbstractJavaIntegratorService {
    @Autowired
    private SipService sipService;
    @Override
    public void init() {

    }

    @Override
    public BizMessage doBizService(BizMessage<JSONObject> message) {
        BizMessage bizMessage = sipService.doServerService("server3",message.getData());
        log.info("返回消息:{}",bizMessage);
        return bizMessage;

    }
}
