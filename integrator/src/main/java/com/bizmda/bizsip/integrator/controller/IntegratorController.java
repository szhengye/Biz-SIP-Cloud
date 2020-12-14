package com.bizmda.bizsip.integrator.controller;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.common.BizMessage;
import com.bizmda.bizsip.common.BizResultEnum;
import com.bizmda.bizsip.common.BizUtils;
import com.bizmda.bizsip.config.ScriptServiceMapping;
import com.bizmda.bizsip.config.ServerAdaptorConfigMapping;
import com.bizmda.bizsip.integrator.script.ServerService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.ssssssss.magicapi.context.CookieContext;
import org.ssssssss.magicapi.context.HeaderContext;
import org.ssssssss.magicapi.context.RequestContext;
import org.ssssssss.magicapi.context.SessionContext;
import org.ssssssss.magicapi.functions.AssertFunctions;
import org.ssssssss.magicapi.functions.RequestFunctions;
import org.ssssssss.magicapi.functions.ResponseFunctions;
import org.ssssssss.magicapi.provider.ResultProvider;
import org.ssssssss.magicapi.provider.impl.DefaultResultProvider;
import org.ssssssss.magicapi.script.ScriptManager;
import org.ssssssss.script.MagicModuleLoader;
import org.ssssssss.script.MagicScript;
import org.ssssssss.script.MagicScriptContext;
import org.ssssssss.script.exception.MagicScriptAssertException;

import javax.annotation.PostConstruct;
import javax.script.SimpleScriptContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/")
public class IntegratorController {
    @Autowired
    ScriptServiceMapping scriptServiceMapping;
    @Autowired
    ServerAdaptorConfigMapping serverAdaptorConfigMapping;
    @Autowired(required = false)
    private List<HttpMessageConverter<?>> httpMessageConverters;
    @Autowired
    private ApplicationContext springContext;

    private ResultProvider resultProvider = new DefaultResultProvider();
    private boolean throwException = false;

    @PostConstruct
    public void init() {
        setupMagicModules();
        ServerService.serverAdaptorConfigMapping = this.serverAdaptorConfigMapping;
    }

    @PostMapping(value={"/sip/{path1}","/sip/{path1}/{path2}","/sip/{path1}/{path2}/{path3}"},consumes = "application/json", produces = "application/json")
    public BizMessage doBizService(HttpServletRequest request, HttpServletResponse response,
                               @RequestBody BizMessage inMessage,
                               @PathVariable(required = false) Map<String, Object> pathVariables,
                               @RequestParam(required = false) Map<String, Object> parameters) throws BizException {
        BizUtils.currentBizMessage.set(inMessage);
        String serviceId = "";
        for(int i=1;;i++) {
            String a = (String)pathVariables.get("path"+String.valueOf(i));
            if (a == null) {
                break;
            }
            serviceId = serviceId + "/" + a;
        }

        String script = this.scriptServiceMapping.getScript(serviceId);
        if (script == null) {
            throw new BizException(BizResultEnum.INTEGRATOR_SERVICE_NOT_FOUND,
                    StrFormatter.format("聚合服务不存在:{}",serviceId));
        }
        MagicScriptContext context = new MagicScriptContext();;
        context.set("bizmessage", inMessage);
        Object result = executeScript(script, context);
        JSONObject jsonObject = JSONUtil.parseObj(result);

        inMessage.success(jsonObject);

        BizUtils.currentBizMessage.remove();
        return inMessage;
    }

    private Object executeScript(String script, MagicScriptContext context) {
        SimpleScriptContext simpleScriptContext = new SimpleScriptContext();
        simpleScriptContext.setAttribute("ROOT", context, 100);
        return ScriptManager.compile("MagicScript", script).eval(simpleScriptContext);
    }

    private void setupMagicModules() {
        // 设置脚本import时 class加载策略
        MagicModuleLoader.setClassLoader((className) -> {
            try {
                return springContext.getBean(className);
            } catch (Exception e) {
                Class<?> clazz = null;
                try {
                    clazz = Class.forName(className);
                    return springContext.getBean(clazz);
                } catch (Exception ex) {
                    return clazz;
                }
            }
        });
        log.info("注册模块:{} -> {}", "log", Logger.class);
        MagicModuleLoader.addModule("log", LoggerFactory.getLogger(MagicScript.class));
//        List<String> importModules = properties.getAutoImportModuleList();
//        log.info("注册模块:{} -> {}", "env", EnvFunctions.class);
//        MagicModuleLoader.addModule("env", new EnvFunctions(environment));
        log.info("注册模块:{} -> {}", "request", RequestFunctions.class);
        MagicModuleLoader.addModule("request", new RequestFunctions());
//        log.info("注册模块:{} -> {}", "response", ResponseFunctions.class);
//        MagicModuleLoader.addModule("response", new ResponseFunctions(resultProvider));
        log.info("注册模块:{} -> {}", "assert", AssertFunctions.class);
        MagicModuleLoader.addModule("assert", AssertFunctions.class);
        log.info("注册模块:{} -> {}", "server", ServerService.class);
        MagicModuleLoader.addModule("server", ServerService.class);
    }
}
