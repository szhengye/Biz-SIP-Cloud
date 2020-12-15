package com.bizmda.bizsip.config;

import cn.hutool.core.io.file.FileReader;
import com.bizmda.bizsip.common.BizUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shizhengye
 */
@Slf4j
public class ScriptServiceMapping {
    private Map<String, String> mappings;

    public ScriptServiceMapping(String configPath) {
        String scriptPath = configPath + "/service";
        List<File> fileList = BizUtils.getFileList(scriptPath,".script");
        mappings = new HashMap<String,String>();
        for (File file:fileList) {
            FileReader fileReader = new FileReader(file);
            String script = fileReader.readString();
            String allPath = file.getPath();
            String serviceId = allPath.substring(scriptPath.length(),allPath.length() - 7);
            log.info("装载聚合服务:{}",serviceId);
            mappings.put(serviceId,script);
        }
    }

    public String getScript(String serviceId) {
        return this.mappings.get(serviceId);
    }
}
