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
    private Map messageMap;
    private Map protocolMap;

    public AbstractServerAdaptorConfig(Map map) {
        this.id = (String)map.get("id");
        this.messageMap = (Map)map.get("message");

        this.protocolMap = (Map)map.get("protocol");
//        String type = (String)protocolMap.get("type");
//        if (type.equalsIgnoreCase("java")) {
//            this.protocol = new JavaServerAdaptorProtocolConfig(protocolMap);
//        }
    }
}
