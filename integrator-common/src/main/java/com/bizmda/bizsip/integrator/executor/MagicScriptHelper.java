package com.bizmda.bizsip.integrator.executor;

import cn.hutool.extra.spring.SpringUtil;
import com.bizmda.bizsip.integrator.executor.script.SipFunctions;
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

@Slf4j
public class MagicScriptHelper {
    private static boolean isSetupMagicModules = false;

    public static Object executeScript(String script, MagicScriptContext context) {
        if (!isSetupMagicModules) {
            setupMagicModules();
            isSetupMagicModules = true;
        }
        SimpleScriptContext simpleScriptContext = new SimpleScriptContext();
        simpleScriptContext.setAttribute("ROOT", context, 100);
        Object result = ScriptManager.compile("MagicScript", script).eval(simpleScriptContext);
        return result;
    }

    private static void setupMagicModules() {
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
        log.info("注册模块:{} -> {}", "sip", SipFunctions.class);
        MagicModuleLoader.addModule("sip", SipFunctions.class);

        SQLExecutor magicSQLExecutor = SpringUtil.getBean(SQLExecutor.class);
        log.info("注册模块:{} -> {}", "db", SQLExecutor.class);
        MagicModuleLoader.addModule("db", magicSQLExecutor);
        RedisFunctions redisFunctions = SpringUtil.getBean(RedisFunctions.class);
        log.info("注册模块:{} -> {}", "redis", RedisFunctions.class);
        MagicModuleLoader.addModule("redis", redisFunctions);

    }
}
