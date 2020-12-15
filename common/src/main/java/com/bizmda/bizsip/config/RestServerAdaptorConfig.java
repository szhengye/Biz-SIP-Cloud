package com.bizmda.bizsip.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Map;

/**
 * @author shizhengye
 */
public class RestServerAdaptorConfig extends AbstractServerAdaptorConfig {
    private String url;

    public String getUrl() {
        return url;
    }

    public RestServerAdaptorConfig(Map map) {
        super(map);
        this.setType("rest");
        this.url = (String)map.get("url");
    }
}
