package com.bizmda.bizsip.integrator.checkrule;

import cn.hutool.core.io.FileUtil;
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
        this.mappings = new HashMap<>();

        if (this.configPath.endsWith("/")) {
            scriptPath = this.configPath + "check-rule";
        }
        else {
            scriptPath = this.configPath + "/check-rule";
        }
        String suffix;

        List<File> files = FileUtil.loopFiles(scriptPath);
        Yaml yaml = new Yaml();
        for (File file : files) {
            suffix = FileUtil.getSuffix(file).toLowerCase();
            if (! "yml".equalsIgnoreCase(suffix)) {
                continue;
            }
            String allPath = file.getPath();
            String serviceId = allPath.substring(scriptPath.length(), allPath.length() - suffix.length() - 1);
            log.info("装载聚合服务:{}", serviceId);
            Map<String,Object> map;
            try {
                map = (Map<String,Object>)yaml.load(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                throw new BizException(BizResultEnum.CHECK_RULE_FILE_NOTFOUND);
            }
            CheckRuleConfig checkRuleConfig = new CheckRuleConfig(map);
            mappings.put(serviceId, checkRuleConfig);
        }
    }

    public CheckRuleConfig getCheckRuleConfig(String serviceId) {
        return this.mappings.get(serviceId);
    }
}
