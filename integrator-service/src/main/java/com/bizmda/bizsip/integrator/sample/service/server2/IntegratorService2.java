package com.bizmda.bizsip.integrator.sample.service.server2;

import cn.hutool.json.JSONObject;
import com.bizmda.bizsip.common.BizMessage;
import com.bizmda.bizsip.integrator.service.AbstractJavaIntegratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("server2.service2")
public class IntegratorService2 extends AbstractJavaIntegratorService {
    @Override
    public void init() {

    }

    @Override
    public BizMessage doBizService(BizMessage<JSONObject> message) {
        log.info("执行server2.service2");
        return message;

    }
}
