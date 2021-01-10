package com.bizmda.bizsip.integrator.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 史正烨
 */
@Slf4j
@Component
@Data
@ConfigurationProperties(prefix = "bizsip")
public class ServerAdaptorsConfig {
    private List<Map<String,Object>> serverAdaptors;
    private Map<String,Map<String,Object>> serverAdaptorMap = null;

    public Map<String,Object> getServerAdaptor(String id) {
        if (this.serverAdaptorMap == null) {
            this.setupServerAdaptorMap();
        }
        return this.serverAdaptorMap.get(id);
    }

    private void setupServerAdaptorMap() {
        log.info("serverAdaptor:{}",this.serverAdaptors);
        this.serverAdaptorMap = new HashMap<>();
        for (Map<String,Object> map:this.serverAdaptors) {
            this.serverAdaptorMap.put((String)map.get("id"),map);
        }
    }
}
