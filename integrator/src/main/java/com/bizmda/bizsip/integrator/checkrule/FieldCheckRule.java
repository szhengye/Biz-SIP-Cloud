package com.bizmda.bizsip.integrator.checkrule;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class FieldCheckRule {
    private String field;
    private String rule;
    private String message;
    private List<Object> args;

    public FieldCheckRule(Map<String,Object> map) {
        this.field = (String)map.get("field");
        this.rule = (String)map.get("rule");
        this.message = (String)map.get("message");
        this.args = (List<Object>)map.get("args");
    }
}
