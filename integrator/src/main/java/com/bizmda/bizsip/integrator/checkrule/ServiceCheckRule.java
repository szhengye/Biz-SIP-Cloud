package com.bizmda.bizsip.integrator.checkrule;

import lombok.Data;

import java.util.Map;

@Data
public class ServiceCheckRule {
    private String script;
    private String message;

    public ServiceCheckRule(Map<String,Object> map) {
        this.message = (String)map.get("message");
        String var = (String)map.get("script");
        if (var != null) {
            this.script = var;
            return;
        }
        String file = (String)map.get("file");
        if (file != null) {
            //TODO 载入文件
        }
    }
}
