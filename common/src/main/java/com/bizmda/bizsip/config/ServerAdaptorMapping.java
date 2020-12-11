package com.bizmda.bizsip.config;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerAdaptorMapping {
    private Map<String, AbstractServerAdaptorConfig> serverAdaptorMap;

    public ServerAdaptorMapping(String configPath) {
        Yaml yaml = new Yaml();
        List<Map> serverAdaptorList = null;
        try {
            serverAdaptorList = (List<Map>)yaml.load(new FileInputStream(new File(configPath+"/server-adaptor.yml")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        AbstractServerAdaptorConfig serverAdaptor = null;
        this.serverAdaptorMap = new HashMap<String, AbstractServerAdaptorConfig>();
        for (Map serverAdaptorMap:serverAdaptorList) {
            String type = (String)serverAdaptorMap.get("type");
            if (type.equalsIgnoreCase("rest")) {
                serverAdaptor = new RestServerAdaptorConfig(serverAdaptorMap);
            }
            else {
                continue;
            }
            this.serverAdaptorMap.put(serverAdaptor.getId(),serverAdaptor);
        }
    }

    public AbstractServerAdaptorConfig getServerAdaptor(String id) {
        return this.serverAdaptorMap.get(id);
    }
}
