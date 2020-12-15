package com.bizmda.bizsip.config;

import lombok.Data;

import java.util.Map;

/**
 * @author shizhengye
 */
public class JavaServerAdaptorProtocolConfig extends AbstractServerAdaptorProtocolConfig {
    private String clazzName;

    public String getClazzName() {
        return clazzName;
    }

    public JavaServerAdaptorProtocolConfig(Map map) {
        this.setType("java");
        this.clazzName = (String)map.get("class-name");
    }
}
