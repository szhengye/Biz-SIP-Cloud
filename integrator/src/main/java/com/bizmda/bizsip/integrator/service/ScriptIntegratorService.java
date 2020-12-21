package com.bizmda.bizsip.integrator.service;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.bizmda.bizsip.common.BizMessage;
import org.ssssssss.magicapi.script.ScriptManager;
import org.ssssssss.script.MagicScriptContext;

import javax.script.SimpleScriptContext;

/**
 * @author shizhengye
 */
public class ScriptIntegratorService extends AbstractIntegratorService {
    @Override
    public BizMessage doBizService(BizMessage<JSONObject> message) {
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
}
