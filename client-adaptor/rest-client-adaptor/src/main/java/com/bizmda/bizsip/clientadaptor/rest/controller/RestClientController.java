package com.bizmda.bizsip.clientadaptor.rest.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.bizmda.bizsip.clientadaptor.config.ClientAdaptor;
import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.common.BizMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
public class RestClientController {
    @Value(("${bizsip.client-adaptor-id}"))
    private String clientAdaptorId;

    @Autowired
    private ClientAdaptor clientAdaptor;

    @PostConstruct
    public void init() {
        try {
            this.clientAdaptor.init(this.clientAdaptorId);
        } catch (BizException e) {
            log.error("客户端适配器初始化失败！",e);
        }
    }

    @PostMapping(value = "/rest", consumes = "application/json", produces = "application/json")
    public Object doService(@RequestBody JSONObject inMessage, HttpServletResponse response) {
        Object outMessage = null;
        try {
            outMessage = this.clientAdaptor.process(inMessage);
            return outMessage;
        } catch (BizException e) {
            JSONObject result = JSONUtil.createObj()
                    .put("code", e.getCode())
                    .put("message", e.getMessage())
                    .put("extMessage", e.getExtMessage());
            return result;
        }
    }
}