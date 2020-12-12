package com.bizmda.bizsip.config;

import lombok.Data;

import java.util.*;

@Data
public abstract class AbstractServerAdaptorConfig {
    private String id;
    private String type;
    private Map messageMap;
    private AbstractServerAdaptorProtocolConfig protocol;

    public AbstractServerAdaptorConfig(Map map) {
        this.id = (String)map.get("id");
        this.messageMap = (Map)map.get("message");
//        this.messageType = (String)messageMap.get("type");
//        List<Map> packRules = (List<Map>)messageMap.get("pack-rules");
//        if (packRules == null ) {
//            packRules = new ArrayList<Map>();
//        }
//        this.packRules = new ArrayList<PredicateRuleConfig>();
//        for(Map ruleMap:packRules) {
//            PredicateRuleConfig predicateRuleConfig = new PredicateRuleConfig(ruleMap);
//            this.packRules.add(predicateRuleConfig);
//        }
//        List<Map> unpackRules = (List<Map>)messageMap.get("unpack-rules");
//        if (unpackRules == null) {
//            unpackRules = new ArrayList<Map>();
//        }
//        this.unpackRules = new ArrayList<PredicateRuleConfig>();
//        for(Map ruleMap:unpackRules) {
//            PredicateRuleConfig predicateRuleConfig = new PredicateRuleConfig(ruleMap);
//            this.unpackRules.add(predicateRuleConfig);
//        }
        Map protocolMap = (Map)map.get("protocol");
        String type = (String)protocolMap.get("type");
        if (type.equalsIgnoreCase("java")) {
            this.protocol = new JavaServerAdaptorProtocolConfig(protocolMap);
        }
    }
}
