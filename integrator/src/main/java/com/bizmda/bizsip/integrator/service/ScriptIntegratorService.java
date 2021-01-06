package com.bizmda.bizsip.integrator.service;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONArray;
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
import java.util.List;

/**
 * @author 史正烨
 */
@Slf4j
public class ScriptIntegratorService extends AbstractIntegratorService {


    public ScriptIntegratorService(String serviceId,String type,String fileContent) {
        super(serviceId, type, fileContent);
    }

    @Override
    public void init() {

    }

    @Override
    public BizMessage doBizService(BizMessage message) {

        MagicScriptContext context = new MagicScriptContext();;
        context.set("bizmessage", message);
        Object result = MagicScriptHelper.executeScript(this.getFileContent(), context);
//        BizMessage outMessage = (BizMessage)context.get("bizmessage");
//        log.info("outMessage***:{}",outMessage);
        if (result instanceof List) {
            JSONArray jsonArray = JSONUtil.parseArray(result);
            message.success(jsonArray);
        }
        else {
            JSONObject jsonObject = JSONUtil.parseObj(result);
            message.success(jsonObject);
        }
        return message;
    }

}
