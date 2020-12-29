package com.bizmda.bizsip.integrator.service;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.bizmda.bizsip.common.BizMessage;
import com.bizmda.bizsip.integrator.service.script.ServerService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.ssssssss.magicapi.functions.AssertFunctions;
import org.ssssssss.magicapi.functions.RedisFunctions;
import org.ssssssss.magicapi.functions.RequestFunctions;
import org.ssssssss.magicapi.functions.SQLExecutor;
import org.ssssssss.magicapi.script.ScriptManager;
import org.ssssssss.script.MagicModuleLoader;
import org.ssssssss.script.MagicScript;
import org.ssssssss.script.MagicScriptContext;

import javax.script.SimpleScriptContext;

/**
 * @author 史正烨
 */
@Slf4j
public class ScriptIntegratorService extends AbstractIntegratorService {
    private static boolean isSetupMagicModules = false;

    public ScriptIntegratorService(String serviceId,String type,String fileContent) {
        super(serviceId, type, fileContent);
    }

    @Override
    public void init() {

    }

    @Override
    public BizMessage doBizService(BizMessage<JSONObject> message) {
        if (!isSetupMagicModules) {
            this.setupMagicModules();
            isSetupMagicModules = true;
        }
        MagicScriptContext context = new MagicScriptContext();;
        context.set("bizmessage", message);
        Object result = executeScript(this.getFileContent(), context);
        JSONObject jsonObject = JSONUtil.parseObj(result);

        message.success(jsonObject);
        return message;
    }

    private Object executeScript(String script, MagicScriptContext context) {
        SimpleScriptContext simpleScriptContext = new SimpleScriptContext();
        simpleScriptContext.setAttribute("ROOT", context, 100);
        return ScriptManager.compile("MagicScript", script).eval(simpleScriptContext);
    }

    private void setupMagicModules() {
        // 设置脚本import时 class加载策略
        ApplicationContext springContext = SpringUtil.getApplicationContext();
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
        log.info("注册模块:{} -> {}", "request", RequestFunctions.class);
        MagicModuleLoader.addModule("request", new RequestFunctions());
        log.info("注册模块:{} -> {}", "assert", AssertFunctions.class);
        MagicModuleLoader.addModule("assert", AssertFunctions.class);
        log.info("注册模块:{} -> {}", "server", ServerService.class);
        MagicModuleLoader.addModule("server", ServerService.class);

        SQLExecutor magicSQLExecutor = SpringUtil.getBean(SQLExecutor.class);
        log.info("注册模块:{} -> {}", "db", SQLExecutor.class);
        MagicModuleLoader.addModule("db", magicSQLExecutor);
        RedisFunctions redisFunctions = SpringUtil.getBean(RedisFunctions.class);
        log.info("注册模块:{} -> {}", "redis", RedisFunctions.class);
        MagicModuleLoader.addModule("redis", redisFunctions);

    }
}
