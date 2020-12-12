package com.bizmda.bizsip.config;

import lombok.Data;

import java.util.Map;

@Data
public class PredicateRuleConfig {
    private String predicate;
    private String rule;

    public PredicateRuleConfig(Map ruleMap) {
        this.predicate = (String)ruleMap.get("predicate");
        this.rule = (String)ruleMap.get("rule");
    }
}
