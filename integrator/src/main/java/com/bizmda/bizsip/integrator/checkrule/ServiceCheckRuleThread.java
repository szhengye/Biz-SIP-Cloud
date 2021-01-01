package com.bizmda.bizsip.integrator.checkrule;

import cn.hutool.json.JSONObject;
import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.common.BizMessage;
import com.bizmda.bizsip.integrator.service.MagicScriptHelper;
import org.ssssssss.script.MagicScriptContext;

import java.util.concurrent.Callable;

/**
 * @author 史正烨
 */
public class ServiceCheckRuleThread implements Callable<ServiceChcekRuleResult> {
    public static ThreadLocal<BizMessage> currentBizMessage = new ThreadLocal<BizMessage>();

    private ServiceCheckRule serviceCheckRule;
    private JSONObject jsonObject;

    public ServiceCheckRuleThread(JSONObject jsonObject, ServiceCheckRule serviceCheckRule) throws BizException {
        this.serviceCheckRule = serviceCheckRule;
        this.jsonObject = jsonObject;
    }

    @Override
    public ServiceChcekRuleResult call() throws Exception {
        MagicScriptContext context = new MagicScriptContext();;
        context.set("data", this.jsonObject);
        Object result = MagicScriptHelper.executeScript(this.serviceCheckRule.getScript(), context);
        return ServiceChcekRuleResult.builder()
                .result(result)
                .build();
    }
}
