package com.bizmda.bizsip.common.fieldrule;

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
public class FieldRuleThread implements Callable<FieldRule> {
    private FieldRuleConfig fieldRuleConfig;
    private JSONObject jsonObject;

    public FieldRuleThread(JSONObject jsonObject, FieldRuleConfig fieldRuleConfig) throws BizException {
        this.fieldRuleConfig = fieldRuleConfig;
        this.jsonObject = jsonObject;
    }

    @Override
    public FieldRule call() throws Exception {
        Method method;
        try {
            method = FieldRuleFunction.class.getMethod(fieldRuleConfig.getRule(), Object.class, List.class);
        } catch (NoSuchMethodException e) {
            throw new BizException(BizResultEnum.FIELD_VALIDATE_FUNCTION_NOTFOUND);
        }

        Object value = this.jsonObject.getByPath(this.fieldRuleConfig.getField());
        boolean result = (boolean)method.invoke(null,value,this.fieldRuleConfig.getArgs());
        if (result) {
            return null;
        }
        return FieldRule.builder()
                .field(this.fieldRuleConfig.getField())
                .value(value)
                .rule(this.fieldRuleConfig.getRule())
                .args(this.fieldRuleConfig.getArgs())
                .desc(StrFormatter.format(this.fieldRuleConfig.getDesc(),value))
                .build();
    }
}
