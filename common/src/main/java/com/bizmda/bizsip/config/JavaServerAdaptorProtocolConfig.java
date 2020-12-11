package com.bizmda.bizsip.config;

import lombok.Data;

import java.util.Map;

@Data
public class JavaServerAdaptorProtocolConfig extends AbstractServerAdaptorProtocolConfig {
    private String clazzName;

    public JavaServerAdaptorProtocolConfig(Map map) {
        this.setType("java");
        this.clazzName = (String)map.get("class-name");
    }
}
