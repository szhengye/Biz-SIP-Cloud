package com.bizmda.bizsip.sample.serveradaptor.server2.controller;

import cn.hutool.json.JSONObject;
import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.common.BizMessage;
import com.bizmda.bizsip.common.BizUtils;
import com.bizmda.bizsip.serveradaptor.ServerAdaptor;
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
public class Server2AdaptorController {
    @Autowired
    private ServerAdaptor serverAdaptor;

    @PostConstruct
    public void init() {
        try {
            this.serverAdaptor.init("server2");
        } catch (BizException e) {
            log.error("服务端适配器初始化失败！",e);
        }
    }

    @PostMapping(value = "/server2", consumes = "application/json", produces = "application/json")
    public BizMessage doService(@RequestBody BizMessage inMessage, HttpServletResponse response) {
        log.debug("inMessage:{}", inMessage);
//        BizUtils.currentBizMessage.set(inMessage);
        JSONObject outMessage = null;
        try {
            outMessage = this.serverAdaptor.process(inMessage.getData());
            inMessage.success(outMessage);
            return inMessage;
        } catch (BizException e) {
            log.error("服务端适配器执行出错",e);
            inMessage.fail(e);
            return inMessage;
        }
    }
}