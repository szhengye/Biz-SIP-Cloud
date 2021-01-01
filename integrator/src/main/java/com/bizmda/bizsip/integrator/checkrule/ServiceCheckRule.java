package com.bizmda.bizsip.integrator.checkrule;

import lombok.Data;

import java.util.Map;

@Data
public class ServiceCheckRule {
    private String script;
    private String message;

    public ServiceCheckRule(Map map) {
        this.message = (String)map.get("message");
        String script = (String)map.get("script");
        if (script != null) {
            this.script = script;
            return;
        }
        String file = (String)map.get("file");
        if (file != null) {
            //TODO 载入文件
        }
    }
}
