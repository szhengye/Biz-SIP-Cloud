package com.bizmda.bizsip.sample.clientadaptor.client.controller;

import cn.hutool.json.JSONObject;
import com.bizmda.bizsip.clientadaptor.config.ClientAdaptor;
import com.bizmda.bizsip.common.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

/**
 * @author shizhengye
 */
@Slf4j
@RestController
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
        Object outMessage = null;
        try {
            outMessage = this.clientAdaptor.process(inMessage);
            return outMessage;
        } catch (BizException e) {
            return "客户端适配器执行出错:"
                    + "\ncode:" + e.getCode()
                    + "\nmessage:" + e.getMessage()
                    + "\nextMessage:" + e.getExtMessage();
        }
    }
}