package com.bizmda.bizsip.integrator.checkrule;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.common.BizResultEnum;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class CheckRuleConfigMapping {
    private Map<String, CheckRuleConfig> mappings;
    private String configPath;

    public CheckRuleConfigMapping(String configPath) throws BizException {
        this.configPath = configPath;
        this.load();
    }

    public void load() throws BizException {
        String scriptPath;
        this.mappings = new HashMap<String,CheckRuleConfig>();

        if (this.configPath.endsWith("/")) {
            scriptPath = this.configPath + "check-rule";
        }
        else {
            scriptPath = this.configPath + "/check-rule";
        }
        String suffix;
        FieldCheckRule integratorService = null;

        List<File> files = FileUtil.loopFiles(scriptPath);
        Yaml yaml = new Yaml();
        for (File file : files) {
            suffix = FileUtil.getSuffix(file).toLowerCase();
            if (! "yml".equalsIgnoreCase(suffix)) {
                continue;
            }
            FileReader fileReader = new FileReader(file);
            String allPath = file.getPath();
            String serviceId = allPath.substring(scriptPath.length(), allPath.length() - suffix.length() - 1);
            log.info("装载聚合服务:{}", serviceId);
            Map map;
            try {
                map = (Map)yaml.load(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                throw new BizException(BizResultEnum.CHECK_RULE_FILE_NOTFOUND);
            }
            CheckRuleConfig checkRuleConfig = new CheckRuleConfig(map);
            mappings.put(serviceId, checkRuleConfig);
        }
    }

//    public void init() {
//        for(String key:this.mappings.keySet()) {
//            AbstractIntegratorService integratorService = this.mappings.get(key);
//            integratorService.init();
//        }
//    }

    public CheckRuleConfig getCheckRuleConfig(String serviceId) {
        return this.mappings.get(serviceId);
    }
}
