package com.bizmda.bizsip.integrator.service;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.bizmda.bizsip.common.BizMessage;
import lombok.extern.slf4j.Slf4j;
import org.ssssssss.script.MagicScriptContext;

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
