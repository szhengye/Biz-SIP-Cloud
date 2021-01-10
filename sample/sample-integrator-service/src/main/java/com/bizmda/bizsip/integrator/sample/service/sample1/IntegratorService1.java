package com.bizmda.bizsip.integrator.sample.service.sample1;

import cn.hutool.json.JSONObject;
import com.bizmda.bizsip.common.BizMessage;
import com.bizmda.bizsip.integrator.service.JavaIntegratorServiceInterface;
import com.bizmda.bizsip.integrator.service.SipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("sample1.service1")
public class IntegratorService1 implements JavaIntegratorServiceInterface {
    @Autowired
    private SipService sipService;
    @Override
    public void init() {
        // 没有初始化操作内容
    }

    @Override
    public BizMessage doBizService(BizMessage<JSONObject> message) {
        // 类似"/openapi/sample2.script"
        return this.sipService.doServerService("server1",message.getData());
    }
}
