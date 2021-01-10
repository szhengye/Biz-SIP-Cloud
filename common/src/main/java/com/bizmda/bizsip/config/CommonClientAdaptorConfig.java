package com.bizmda.bizsip.config;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 史正烨
 */
@Data
public class CommonClientAdaptorConfig {
    private String id;
    private Map<String,Object> messageMap;
    private List<PredicateRuleConfig> serviceRules;

    public CommonClientAdaptorConfig(Map<String,Object> map) {
        this.id = (String)map.get("id");
        this.messageMap = (Map)map.get("message");

        Map<String,Object> serviceMap = (Map<String,Object>)map.get("service");
        List<Map<String,Object>> rules = (List<Map<String,Object>>)serviceMap.get("service-rules");
        if (rules == null) {
            rules = new ArrayList<>();
        }
        this.serviceRules = new ArrayList<>();
        for(Map<String,Object> ruleMap:rules) {
            PredicateRuleConfig predicateRuleConfig = new PredicateRuleConfig(ruleMap);
            this.serviceRules.add(predicateRuleConfig);
        }
    }
}
