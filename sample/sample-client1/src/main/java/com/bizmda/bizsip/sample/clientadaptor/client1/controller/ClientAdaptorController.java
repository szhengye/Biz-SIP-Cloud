package com.bizmda.bizsip.sample.clientadaptor.client1.controller;

import com.bizmda.bizsip.clientadaptor.config.ClientAdaptorProcessor;
import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.common.BizMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/")
public class ClientAdaptorController {
    @Autowired
    private ClientAdaptorProcessor clientAdaptorProcessor;

    @PostConstruct
    public void init() {
        try {
            this.clientAdaptorProcessor.init("client1");
        } catch (BizException e) {
            log.error("客户端适配器初始化失败！",e);
        }
    }

    @PostMapping(value = "/server1", consumes = "application/json", produces = "application/json")
    public BizMessage doService(@RequestBody Map inMessage, HttpServletResponse response) {
        log.debug("inMessage:{}", inMessage);
        Object outMessage = null;
        try {
            outMessage = this.clientAdaptorProcessor.process(inMessage);
            return BizMessage.success(outMessage);
        } catch (BizException e) {
            log.error("服务端适配器执行出错",e);
            return BizMessage.fail(e);
        }
    }
}