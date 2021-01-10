package com.bizmda.bizsip.config;

import lombok.Data;

import java.util.*;

/**
 * @author 史正烨
 */
@Data
public class AbstractServerAdaptorConfig {
    private String id;
    private String type;
    private Map<String,Object> messageMap;
    private Map<String,Object> protocolMap;

    public AbstractServerAdaptorConfig(Map<String,Object> map) {
        this.id = (String)map.get("id");
        this.messageMap = (Map<String, Object>)map.get("message");
        this.protocolMap = (Map<String, Object>)map.get("protocol");
    }
}
