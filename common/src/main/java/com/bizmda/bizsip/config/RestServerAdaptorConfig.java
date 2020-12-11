package com.bizmda.bizsip.config;

import lombok.Data;

import java.util.Map;

@Data
public class RestServerAdaptorConfig extends AbstractServerAdaptorConfig {
    private String url;
    public RestServerAdaptorConfig(Map map) {
        super(map);
        this.setType("rest");
        this.url = (String)map.get("url");
    }
}
