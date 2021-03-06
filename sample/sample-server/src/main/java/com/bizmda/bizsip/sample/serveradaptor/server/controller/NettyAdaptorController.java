package com.bizmda.bizsip.sample.serveradaptor.server.controller;

import cn.hutool.json.JSONObject;
import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.common.BizMessage;
import com.bizmda.bizsip.serveradaptor.ServerAdaptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 史正烨
 */
@Slf4j
@RestController
public class NettyAdaptorController {
    @Autowired
    private ServerAdaptor serverAdaptor;

    @PostConstruct
    public void init() {
        try {
            this.serverAdaptor.init("netty");
        } catch (BizException e) {
            log.error("服务端适配器初始化失败！",e);
        }
    }

    @PostMapping(value = "/netty", consumes = "application/json", produces = "application/json")
    public BizMessage<JSONObject> doService(@RequestBody BizMessage<JSONObject> inMessage, HttpServletResponse response) {
        JSONObject outMessage = null;
        BizMessage<JSONObject> bizMessage;
        try {
            outMessage = this.serverAdaptor.process(inMessage.getData());
            bizMessage = BizMessage.buildSuccessMessage(inMessage,outMessage);
            return bizMessage;
        } catch (BizException e) {
            log.error("服务端适配器执行出错",e);
            bizMessage = BizMessage.buildFailMessage(inMessage,e);
            return bizMessage;
        }
    }
}