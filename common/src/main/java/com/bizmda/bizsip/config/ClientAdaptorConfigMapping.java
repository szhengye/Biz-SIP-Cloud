package com.bizmda.bizsip.config;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientAdaptorConfigMapping {
    private Map<String, CommonClientAdaptorConfig> clientAdaptorConfigMap;

    public ClientAdaptorConfigMapping(String configPath) {
        Yaml yaml = new Yaml();
        List<Map> clientAdaptorList = null;
        try {
            clientAdaptorList = (List<Map>)yaml.load(new FileInputStream(new File(configPath+"/client-adaptor.yml")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        CommonClientAdaptorConfig clientAdaptor = null;
        this.clientAdaptorConfigMap = new HashMap<String, CommonClientAdaptorConfig>();
        for (Map clientAdaptorMap:clientAdaptorList) {
            clientAdaptor = new CommonClientAdaptorConfig(clientAdaptorMap);
            this.clientAdaptorConfigMap.put(clientAdaptor.getId(),clientAdaptor);
        }
    }

    public CommonClientAdaptorConfig getClientAdaptorConfig(String id) {
        return this.clientAdaptorConfigMap.get(id);
    }
}
