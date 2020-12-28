package com.bizmda.bizsip.config;

import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.common.BizResultEnum;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 史正烨
 */
public class ServerAdaptorConfigMapping {
    private String configPath;
    private Map<String, AbstractServerAdaptorConfig> serverAdaptorConfigMap;

    public ServerAdaptorConfigMapping(String configPath) throws BizException {
        this.configPath = configPath;
        this.load();
    }

    public void load() throws BizException {
        Yaml yaml = new Yaml();
        List<Map> serverAdaptorList = null;
        try {
            serverAdaptorList = (List<Map>)yaml.load(new FileInputStream(new File(this.configPath+"/server-adaptor.yml")));
        } catch (FileNotFoundException e) {
            throw new BizException(BizResultEnum.SERVER_ADATPOR_FILE_NOTFOUND);
        }
        AbstractServerAdaptorConfig serverAdaptor = null;
        this.serverAdaptorConfigMap = new HashMap<String, AbstractServerAdaptorConfig>();
        for (Map serverAdaptorMap:serverAdaptorList) {
            String type = (String)serverAdaptorMap.get("type");
            if ("rest".equalsIgnoreCase(type)) {
                serverAdaptor = new RestServerAdaptorConfig(serverAdaptorMap);
            }
            else {
                continue;
            }
            this.serverAdaptorConfigMap.put(serverAdaptor.getId(),serverAdaptor);
        }
    }

    public AbstractServerAdaptorConfig getServerAdaptorConfig(String id) {
        return this.serverAdaptorConfigMap.get(id);
    }
}
