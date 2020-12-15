package com.bizmda.bizsip.sample.serveradaptor.server1.controller;

import cn.hutool.json.JSONObject;
import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.common.BizMessage;
import com.bizmda.bizsip.common.BizUtils;
import com.bizmda.bizsip.serveradaptor.ServerAdaptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping("/")
public class Server1AdaptorController {
    @Autowired
    private ServerAdaptor serverAdaptor;

    @PostConstruct
    public void init() {
        try {
            this.serverAdaptor.init("server1");
        } catch (BizException e) {
            log.error("服务端适配器初始化失败！",e);
        }
    }

    @PostMapping(value = "/server1", consumes = "application/json", produces = "application/json")
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