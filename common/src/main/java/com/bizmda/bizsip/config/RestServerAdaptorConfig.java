package com.bizmda.bizsip.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Map;

/**
 * @author 史正烨
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
