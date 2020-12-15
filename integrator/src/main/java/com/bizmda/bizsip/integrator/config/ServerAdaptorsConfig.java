package com.bizmda.bizsip.integrator.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shizhengye
 */
@Slf4j
@Component
@Data
@ConfigurationProperties(prefix = "bizsip")
public class ServerAdaptorsConfig {
    private List<Map> serverAdaptors;
    private Map<String,Map> serverAdaptorMap = null;

    public Map getServerAdaptor(String id) {
        if (this.serverAdaptorMap == null) {
            this.setupServerAdaptorMap();
        }
        return this.serverAdaptorMap.get(id);
    }

    private void setupServerAdaptorMap() {
        log.info("serverAdaptor:{}",this.serverAdaptors);
        this.serverAdaptorMap = new HashMap<>();
        for (Map map:this.serverAdaptors) {
            this.serverAdaptorMap.put((String)map.get("id"),map);
        }
    }
}
