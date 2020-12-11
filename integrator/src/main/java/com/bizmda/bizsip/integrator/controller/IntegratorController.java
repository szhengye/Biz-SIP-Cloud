package com.bizmda.bizsip.integrator.controller;

import com.bizmda.bizsip.config.ScriptServiceMapping;
import com.bizmda.bizsip.config.ServerAdaptorMapping;
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
    ServerAdaptorMapping serverAdaptorMapping;
    @Autowired(required = false)
    private List<HttpMessageConverter<?>> httpMessageConverters;
    @Autowired
    private ApplicationContext springContext;

    private ResultProvider resultProvider = new DefaultResultProvider();
    private boolean throwException = false;

    @PostConstruct
    public void init() {
        setupMagicModules();
        ServerService.serverAdaptorMapping = this.serverAdaptorMapping;
    }

    @GetMapping(value={"/sip/{path1}","/sip/{path1}/{path2}","/sip/{path1}/{path2}/{path3}"})
    public Object doBizService(HttpServletRequest request, HttpServletResponse response,
                               @PathVariable(required = false) Map<String, Object> pathVariables,
                               @RequestParam(required = false) Map<String, Object> parameters) throws Throwable {
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
            log.error("服务不存在:{}",serviceId);
            return this.resultProvider.buildResult(1001, "fail", "接口不存在");
        }
        MagicScriptContext context = this.createMagicScriptContext(request, pathVariables, parameters);
        Object value;
//            if ((value = this.doPreHandle(info, context)) != null) {
//                return value;
//            } else if (requestedFromTest) {
//                return this.isRequestedFromContinue(request) ? this.invokeContinueRequest(request, response) : this.invokeTestRequest(info, (MagicScriptDebugContext)context, request, response);
//            } else {
        return this.invokeRequest(script, context, request, response);
    }

    private MagicScriptContext createMagicScriptContext(HttpServletRequest request, Map<String, Object> pathVariables, Map<String, Object> parameters) throws IOException {
//        MagicScriptContext context = this.isRequestedFromTest(request) ? new MagicScriptDebugContext() : new MagicScriptContext();
        MagicScriptContext context = new MagicScriptContext();
//        Object wrap = info.getOptionValue("wrap_request_parameter");
//        if (wrap != null && StringUtils.isNotBlank(wrap.toString())) {
//            ((MagicScriptContext)context).set(wrap.toString(), parameters);
//        }

        ((MagicScriptContext)context).putMapIntoContext(parameters);
        ((MagicScriptContext)context).putMapIntoContext(pathVariables);
        ((MagicScriptContext)context).set("cookie", new CookieContext(request));
        ((MagicScriptContext)context).set("header", new HeaderContext(request));
        ((MagicScriptContext)context).set("session", new SessionContext(request.getSession()));
        ((MagicScriptContext)context).set("path", pathVariables);
        Object requestBody = this.readRequestBody(request);
        if (requestBody != null) {
            ((MagicScriptContext)context).set("body", requestBody);
        }

        return (MagicScriptContext)context;
    }

    private Object readRequestBody(HttpServletRequest request) throws IOException {
        if (this.httpMessageConverters != null && request.getContentType() != null) {
            MediaType mediaType = MediaType.valueOf(request.getContentType());
            Class clazz = Map.class;
            Iterator var4 = this.httpMessageConverters.iterator();

            while(var4.hasNext()) {
                HttpMessageConverter<?> converter = (HttpMessageConverter)var4.next();
                if (converter.canRead(clazz, mediaType)) {
                    return converter.read(clazz, new ServletServerHttpRequest(request));
                }
            }
        }

        return null;
    }

    private Object invokeRequest(String script, MagicScriptContext context, HttpServletRequest request, HttpServletResponse response) throws Throwable {
        try {
            RequestContext.setRequestAttribute(request, response);
            Object result = executeScript(script, context);
            Object value = result;
            // 执行后置拦截器
//            if ((value = doPostHandle(info, context, value)) != null) {
//                return value;
//            }
            // 对返回结果包装处理
            return response(result);
        } catch (Throwable root) {
            Throwable parent = root;
            do {
                if (parent instanceof MagicScriptAssertException) {
                    MagicScriptAssertException sae = (MagicScriptAssertException) parent;
                    return resultProvider.buildResult(sae.getCode(), sae.getMessage());
                }
            } while ((parent = parent.getCause()) != null);
            if (throwException) {
                throw root;
            }
            log.error("接口{}请求出错", request.getRequestURI(),root);
            return resultProvider.buildResult(-1, "系统内部出现错误");
        } finally {
            RequestContext.remove();
        }
    }

    private Object executeScript(String script, MagicScriptContext context) {
        SimpleScriptContext simpleScriptContext = new SimpleScriptContext();
        simpleScriptContext.setAttribute("ROOT", context, 100);
        return ScriptManager.compile("MagicScript", script).eval(simpleScriptContext);
    }

    private Object response(Object value) {
        if (value instanceof ResponseEntity) {
            return value;
        } else {
            return value instanceof ResponseFunctions.NullValue ? null : this.resultProvider.buildResult(value);
        }
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
//        ServerService.serverAdaptorsConfig = this.serverAdaptorsConfig;
//        if (magicModules != null) {
//            for (MagicModule module : magicModules) {
//                logger.info("注册模块:{} -> {}", module.getModuleName(), module.getClass());
//                MagicModuleLoader.addModule(module.getModuleName(), module);
//            }
//        }
//        Set<String> moduleNames = MagicModuleLoader.getModuleNames();
//        for (String moduleName : moduleNames) {
//            if (importModules.contains(moduleName)) {
//                log.info("自动导入模块：{}", moduleName);
//                MagicScriptEngine.addDefaultImport(moduleName, MagicModuleLoader.loadModule(moduleName));
//            }
//        }
//        List<String> importPackages = properties.getAutoImportPackageList();
//        for (String importPackage : importPackages) {
//            logger.info("自动导包：{}", importPackage);
//            MagicPackageLoader.addPackage(importPackage);
//        }
//        if (extensionMethods != null) {
//            for (ExtensionMethod extension : extensionMethods) {
//                List<Class<?>> supports = extension.supports();
//                for (Class<?> support : supports) {
//                    logger.info("注册扩展:{} -> {}", support, extension.getClass());
//                    AbstractReflection.getInstance().registerExtensionClass(support, extension.getClass());
//                }
//            }
//        }
    }
}
