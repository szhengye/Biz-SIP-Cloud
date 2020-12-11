package com.bizmda.bizsip.config;

import lombok.Data;

import java.util.Map;

@Data
public class MessageRuleConfig {
    private String predicate;
    private String rule;

    public MessageRuleConfig(Map ruleMap) {
        this.predicate = (String)ruleMap.get("predicate");
        this.rule = (String)ruleMap.get("rule");
    }
}
