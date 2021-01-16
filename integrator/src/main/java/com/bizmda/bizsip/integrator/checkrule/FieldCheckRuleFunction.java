package com.bizmda.bizsip.integrator.checkrule;

import cn.hutool.core.lang.Validator;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class FieldCheckRuleFunction {
    private FieldCheckRuleFunction() {

    }
    public static boolean isCitizenId(Object value, List<Object> args) {
        log.debug("isCitizenId({},{})",value,args);
        return Validator.isCitizenId((String)value);
    }

    public static boolean isEmail(Object value,List<Object> args) {
        log.debug("isEmail({},{})",value,args);
        return Validator.isEmail((String)value);
    }

    public static boolean notEmpty(Object value,List<Object> args) {
        log.debug("notEmpty({},{})",value,args);
        return !Validator.isEmpty(value);
    }

    public static boolean isMatchRegex(Object value,List<Object> args) {
        log.debug("isMatchRegex({},{})",value,args);
        return Validator.isMatchRegex((String)args.get(0), (String)value);
    }

}
