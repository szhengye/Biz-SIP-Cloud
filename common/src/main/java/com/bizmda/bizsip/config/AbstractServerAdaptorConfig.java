package com.bizmda.bizsip.config;

import lombok.Data;

import java.util.*;

/**
 * @author shizhengye
 */
@Data
public abstract class AbstractServerAdaptorConfig {
    private String id;
    private String type;
    private Map messageMap;
    private AbstractServerAdaptorProtocolConfig protocol;

    public AbstractServerAdaptorConfig(Map map) {
        this.id = (String)map.get("id");
        this.messageMap = (Map)map.get("message");

        Map protocolMap = (Map)map.get("protocol");
        String type = (String)protocolMap.get("type");
        if (type.equalsIgnoreCase("java")) {
            this.protocol = new JavaServerAdaptorProtocolConfig(protocolMap);
        }
    }
}
