package com.bizmda.bizsip.integrator.checkrule;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class CheckRuleConfig {
    private CheckMode fieldCheckMode = CheckMode.ONE;
    private List<FieldCheckRule> fieldCheckRuleList;
    private CheckMode serviceCheckMode = CheckMode.ONE;
    private List<ServiceCheckRule> serviceCheckRuleList;

    public CheckRuleConfig(Map checkRuleConfigmap) {
        String mode = (String)checkRuleConfigmap.get("field-check-mode");
        if("all".equalsIgnoreCase(mode)) {
            this.fieldCheckMode = CheckMode.ALL;
        }
        mode = (String)checkRuleConfigmap.get("service-check-mode");
        if("all".equalsIgnoreCase(mode)) {
            this.serviceCheckMode = CheckMode.ALL;
        }

        List<Map> mapList = (List<Map>)checkRuleConfigmap.get("field-check-rules");
        this.fieldCheckRuleList = new ArrayList<FieldCheckRule>();
        for(Map map:mapList) {
            FieldCheckRule fieldCheckRule = new FieldCheckRule(map);
            this.fieldCheckRuleList.add(fieldCheckRule);
        }

        mapList = (List<Map>)checkRuleConfigmap.get("service-check-rules");
        this.serviceCheckRuleList = new ArrayList<ServiceCheckRule>();
        for(Map map:mapList) {
            ServiceCheckRule serviceCheckRule = new ServiceCheckRule(map);
            this.serviceCheckRuleList.add(serviceCheckRule);
        }
    }
}
