package com.bizmda.bizsip.integrator.executor;

import cn.hutool.json.JSONObject;
import com.bizmda.bizsip.common.BizMessage;
import lombok.extern.slf4j.Slf4j;
import org.ssssssss.script.MagicScriptContext;

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
        // 没有初始化内容
    }

    @Override
    public BizMessage<JSONObject> doBizService(BizMessage inBizMessage) {

        MagicScriptContext context = new MagicScriptContext();
        context.set("bizmessage", inBizMessage);
        Object result = MagicScriptHelper.executeScript(this.getContent(), context);

        return BizMessage.buildJSONObjectMessage(inBizMessage,result);
    }

}
