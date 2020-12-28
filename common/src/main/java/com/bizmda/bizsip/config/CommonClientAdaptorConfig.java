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
    private Map messageMap;
    private List<PredicateRuleConfig> serviceRules;

    public CommonClientAdaptorConfig(Map map) {
        this.id = (String)map.get("id");
        this.messageMap = (Map)map.get("message");

        Map serviceMap = (Map)map.get("service");
        List<Map> rules = (List<Map>)serviceMap.get("service-rules");
        if (rules == null) {
            rules = new ArrayList<Map>();
        }
        this.serviceRules = new ArrayList<PredicateRuleConfig>();
        for(Map ruleMap:rules) {
            PredicateRuleConfig predicateRuleConfig = new PredicateRuleConfig(ruleMap);
            this.serviceRules.add(predicateRuleConfig);
        }
    }
}
