package com.bizmda.bizsip.sample.serveradaptor.server1.controller;

import cn.hutool.json.JSONObject;
import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.common.BizMessage;
import com.bizmda.bizsip.serveradaptor.ServerAdaptorProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/")
public class ServerAdaptorController {
    @Autowired
    private ServerAdaptorProcessor serverAdaptorProcessor;

    @PostConstruct
    public void init() {
        try {
            this.serverAdaptorProcessor.init("server1");
        } catch (BizException e) {
            log.error("服务端适配器初始化失败！",e);
        }
    }

    @PostMapping(value = "/server1", consumes = "application/json", produces = "application/json")
    public BizMessage doService(@RequestBody JSONObject inMessage, HttpServletResponse response) {
        log.debug("inMessage:{}", inMessage);
        Object outMessage = null;
        try {
            outMessage = this.serverAdaptorProcessor.process(inMessage);
            return BizMessage.success(outMessage);
        } catch (BizException e) {
            log.error("服务端适配器执行出错",e);
            return BizMessage.fail(e);
        }
    }
}