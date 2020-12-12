package com.bizmda.bizsip.config;

import cn.hutool.json.JSONObject;
import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.common.BizUtils;
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

    public String getMatchRule(JSONObject data) throws BizException {
        if (this.predicate == null || this.predicate.isEmpty()) {
            return BizUtils.getELStringResult(this.rule,data);
        }
        Boolean predicateFlag = BizUtils.getELBooleanResult(this.predicate,data);
        if (predicateFlag) {
            return BizUtils.getELStringResult(this.rule,data);
        }
        return null;
    }
}
