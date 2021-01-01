package com.bizmda.bizsip.integrator.checkrule;

import cn.hutool.core.lang.Validator;

import java.util.List;

public class FieldCheckRuleFunction {
    public static boolean isCitizenId(Object value, List args) {
        return Validator.isCitizenId((String)value);
    }

    public static boolean isEmail(Object value,List args) {
        return Validator.isEmail((String)value);
    }

    public static boolean notEmpty(Object value,List args) {
        return !Validator.isEmpty(value);
    }

    public static boolean isMatchRegex(Object value,List args) {
        return Validator.isMatchRegex((String)args.get(0), (String)value);
    }

}
