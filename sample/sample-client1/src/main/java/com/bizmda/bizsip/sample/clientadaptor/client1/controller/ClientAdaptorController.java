package com.bizmda.bizsip.sample.clientadaptor.client1.controller;

import cn.hutool.json.JSONObject;
import com.bizmda.bizsip.clientadaptor.config.ClientAdaptor;
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

@Slf4j
@RestController
@RequestMapping("/")
public class ClientAdaptorController {
    @Autowired
    private ClientAdaptor clientAdaptor;

    @PostConstruct
    public void init() {
        try {
            this.clientAdaptor.init("client1");
        } catch (BizException e) {
            log.error("客户端适配器初始化失败！",e);
        }
    }

    @PostMapping(value = "/client1", consumes = "application/json", produces = "application/json")
    public Object doService(@RequestBody JSONObject inMessage, HttpServletResponse response) {
        log.debug("inMessage:{}", inMessage);
        Object outMessage = null;
        try {
            outMessage = this.clientAdaptor.process(inMessage);
            return outMessage;
        } catch (BizException e) {
            log.error("客户端适配器执行出错",e);
            return "客户端适配器执行出错！";
        }
    }
}