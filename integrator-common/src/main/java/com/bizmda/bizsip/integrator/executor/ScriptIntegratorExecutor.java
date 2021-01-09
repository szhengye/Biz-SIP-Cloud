package com.bizmda.bizsip.integrator.executor;

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
public class ScriptIntegratorExecutor extends AbstractIntegratorExecutor {


    public ScriptIntegratorExecutor(String serviceId, String type, String fileContent) {
        super(serviceId, type, fileContent);
    }

    @Override
    public void init() {

    }

    @Override
    public BizMessage doBizService(BizMessage inBizMessage) {

        MagicScriptContext context = new MagicScriptContext();;
        context.set("bizmessage", inBizMessage);
        Object result = MagicScriptHelper.executeScript(this.getContent(), context);

        BizMessage outBizMessage;
        if (result instanceof List) {
            JSONArray jsonArray = JSONUtil.parseArray(result);
            outBizMessage = BizMessage.buildSuccessMessage(inBizMessage,jsonArray);
        }
        else {
            JSONObject jsonObject = JSONUtil.parseObj(result);
            outBizMessage = BizMessage.buildSuccessMessage(inBizMessage,jsonObject);
        }
        return outBizMessage;
    }

}
