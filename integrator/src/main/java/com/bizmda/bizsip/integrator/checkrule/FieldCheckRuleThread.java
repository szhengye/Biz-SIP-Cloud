package com.bizmda.bizsip.integrator.checkrule;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.json.JSONObject;
import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.common.BizResultEnum;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author 史正烨
 */
public class FieldCheckRuleThread implements Callable<FieldChcekRuleResult> {
    private FieldCheckRule fieldCheckRule;
    private JSONObject jsonObject;

    public FieldCheckRuleThread(JSONObject jsonObject, FieldCheckRule fieldCheckRule) throws BizException {
        this.fieldCheckRule = fieldCheckRule;
        this.jsonObject = jsonObject;
    }

    @Override
    public FieldChcekRuleResult call() throws Exception {
        Method method;
        try {
            method = FieldCheckRuleFunction.class.getMethod(fieldCheckRule.getRule(), Object.class, List.class);
        } catch (NoSuchMethodException e) {
            throw new BizException(BizResultEnum.FIELD_CHECK_FUNCTION_NOTFOUND);
        }

        Object value = this.jsonObject.getByPath(this.fieldCheckRule.getField());
        boolean result = (boolean)method.invoke(null,value,this.fieldCheckRule.getArgs());
        if (result) {
            return null;
        }
        return FieldChcekRuleResult.builder()
                .field(this.fieldCheckRule.getField())
                .value(value)
                .rule(this.fieldCheckRule.getRule())
                .args(this.fieldCheckRule.getArgs())
                .message(StrFormatter.format(this.fieldCheckRule.getMessage(),value))
                .build();
    }
}
