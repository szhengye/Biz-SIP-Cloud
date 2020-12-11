package com.bizmda.bizsip.config;

import lombok.Data;

import java.util.*;

@Data
public abstract class AbstractServerAdaptorConfig {
    private String id;
    private String type;
    private String messageType;
    private List<MessageRuleConfig> packRules;
    private List<MessageRuleConfig> unpackRules;
    private AbstractServerAdaptorProtocolConfig protocol;

    public AbstractServerAdaptorConfig(Map map) {
        this.id = (String)map.get("id");
        Map messageMap = (Map)map.get("message");
        this.messageType = (String)messageMap.get("type");
        List<Map> packRules = (List<Map>)messageMap.get("pack-rules");
        if (packRules == null ) {
            packRules = new ArrayList<Map>();
        }
        this.packRules = new ArrayList<MessageRuleConfig>();
        for(Map ruleMap:packRules) {
            MessageRuleConfig messageRuleConfig = new MessageRuleConfig(ruleMap);
            this.packRules.add(messageRuleConfig);
        }
        List<Map> unpackRules = (List<Map>)messageMap.get("unpack-rules");
        if (unpackRules == null) {
            unpackRules = new ArrayList<Map>();
        }
        this.unpackRules = new ArrayList<MessageRuleConfig>();
        for(Map ruleMap:unpackRules) {
            MessageRuleConfig messageRuleConfig = new MessageRuleConfig(ruleMap);
            this.unpackRules.add(messageRuleConfig);
        }
        Map protocolMap = (Map)map.get("protocol");
        String type = (String)protocolMap.get("type");
        if (type.equalsIgnoreCase("java")) {
            this.protocol = new JavaServerAdaptorProtocolConfig(protocolMap);
        }
    }
}
