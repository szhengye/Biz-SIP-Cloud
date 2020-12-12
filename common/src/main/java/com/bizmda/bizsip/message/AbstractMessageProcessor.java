package com.bizmda.bizsip.message;

import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.config.AbstractServerAdaptorConfig;
import com.bizmda.bizsip.config.PredicateRuleConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractMessageProcessor {
    private String type;
    private List<PredicateRuleConfig> packRules;
    private List<PredicateRuleConfig> unpackRules;

    public abstract Object pack(Object inMessage) throws BizException;
    public abstract Object unpack(Object inMessage) throws BizException;

    public void init(Map messageMap) throws BizException {
        this.type = (String)messageMap.get("type");
        List<Map> packRules = (List<Map>)messageMap.get("pack-rules");
        if (packRules == null ) {
            packRules = new ArrayList<Map>();
        }
        this.packRules = new ArrayList<PredicateRuleConfig>();
        for(Map ruleMap:packRules) {
            PredicateRuleConfig predicateRuleConfig = new PredicateRuleConfig(ruleMap);
            this.packRules.add(predicateRuleConfig);
        }
        List<Map> unpackRules = (List<Map>)messageMap.get("unpack-rules");
        if (unpackRules == null) {
            unpackRules = new ArrayList<Map>();
        }
        this.unpackRules = new ArrayList<PredicateRuleConfig>();
        for(Map ruleMap:unpackRules) {
            PredicateRuleConfig predicateRuleConfig = new PredicateRuleConfig(ruleMap);
            this.unpackRules.add(predicateRuleConfig);
        }
    }
}
