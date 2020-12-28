package com.bizmda.bizsip.integrator.controller;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.json.JSONObject;
import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.common.BizMessage;
import com.bizmda.bizsip.common.BizResultEnum;
import com.bizmda.bizsip.integrator.config.IntegratorServiceMapping;
import com.bizmda.bizsip.config.ServerAdaptorConfigMapping;
import com.bizmda.bizsip.integrator.service.script.ServerService;
import com.bizmda.bizsip.integrator.service.AbstractIntegratorService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;
import org.ssssssss.magicapi.functions.AssertFunctions;
import org.ssssssss.magicapi.functions.RequestFunctions;
import org.ssssssss.magicapi.provider.ResultProvider;
import org.ssssssss.magicapi.provider.impl.DefaultResultProvider;
import org.ssssssss.script.MagicModuleLoader;
import org.ssssssss.script.MagicScript;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author 史正烨
 */
@Slf4j
@RestController
@RequestMapping("/")
public class IntegratorController {
    @Autowired
    IntegratorServiceMapping integratorServiceMapping;

    public static ThreadLocal<BizMessage> currentBizMessage = new ThreadLocal<BizMessage>();

    private ResultProvider resultProvider = new DefaultResultProvider();
    private boolean throwException = false;

    @PostMapping(value="/api",consumes = "application/json", produces = "application/json")
    public BizMessage<JSONObject> doApiService(HttpServletRequest request, HttpServletResponse response,
                                   @RequestBody JSONObject inJsonObject,
                                   @PathVariable(required = false) Map<String, Object> pathVariables,
                                   @RequestParam(required = false) Map<String, Object> parameters) throws BizException {
        BizMessage<JSONObject> inMessage = BizMessage.createNewTransaction();
        inMessage.setData(inJsonObject);
        IntegratorController.currentBizMessage.set(inMessage);

        String serviceId = request.getHeader("Biz-Service-Id");
        if (!serviceId.startsWith("/")) {
            serviceId = "/" + serviceId;
        }
        AbstractIntegratorService integratorService = this.integratorServiceMapping.getIntegratorService(serviceId);
        if (integratorService == null) {
            throw new BizException(BizResultEnum.INTEGRATOR_SERVICE_NOT_FOUND,
                    StrFormatter.format("聚合服务不存在:{}",serviceId));
        }

        BizMessage outMessage = integratorService.doBizService(inMessage);

        IntegratorController.currentBizMessage.remove();
        return outMessage;
    }

//    private void setupMagicModules() {
//        // 设置脚本import时 class加载策略
//        MagicModuleLoader.setClassLoader((className) -> {
//            try {
//                return springContext.getBean(className);
//            } catch (Exception e) {
//                Class<?> clazz = null;
//                try {
//                    clazz = Class.forName(className);
//                    return springContext.getBean(clazz);
//                } catch (Exception ex) {
//                    return clazz;
//                }
//            }
//        });
//        log.info("注册模块:{} -> {}", "log", Logger.class);
//        MagicModuleLoader.addModule("log", LoggerFactory.getLogger(MagicScript.class));
//        log.info("注册模块:{} -> {}", "request", RequestFunctions.class);
//        MagicModuleLoader.addModule("request", new RequestFunctions());
//        log.info("注册模块:{} -> {}", "assert", AssertFunctions.class);
//        MagicModuleLoader.addModule("assert", AssertFunctions.class);
//        log.info("注册模块:{} -> {}", "server", ServerService.class);
//        MagicModuleLoader.addModule("server", ServerService.class);
//    }
}
