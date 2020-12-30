package com.bizmda.bizsip.common.fieldrule;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class FieldRuleConfig {
    private String field;
    private String rule;
    private String desc;
    private List args;

    public FieldRuleConfig(Map map) {
        this.field = (String)map.get("field");
        this.rule = (String)map.get("rule");
        this.desc = (String)map.get("desc");
        this.args = (List)map.get("args");
    }
}
