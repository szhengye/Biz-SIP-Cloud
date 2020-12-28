package com.bizmda.bizsip.integrator.config;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.common.BizResultEnum;
import com.bizmda.bizsip.common.BizUtils;
import com.bizmda.bizsip.integrator.service.AbstractIntegratorService;
import com.bizmda.bizsip.integrator.service.ScriptIntegratorService;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author shizhengye
 */
@Slf4j
public class IntegratorServiceMapping {
    private Map<String, AbstractIntegratorService> mappings;
    private String configPath;

    public IntegratorServiceMapping(String configPath) throws BizException {
        this.configPath = configPath;
        this.load();
    }

    public void load() throws BizException {
        String scriptPath = this.configPath + "/service";
        List<File> fileList = BizUtils.getFileList(scriptPath,".script");
        this.mappings = new HashMap<String,AbstractIntegratorService>();

        if (this.configPath.endsWith("/")) {
            scriptPath = this.configPath + "service";
        }
        else {
            scriptPath = this.configPath + "/service";
        }
        String suffix;
        AbstractIntegratorService integratorService = null;

        List<File> files = FileUtil.loopFiles(scriptPath);
        for(File file:files) {
            suffix = FileUtil.getSuffix(file).toLowerCase();
            Class integratorClazz = AbstractIntegratorService.SERVICE_SCRIPT_SUFFIX_MAP.get(suffix);
            if (integratorClazz != null) {
                FileReader fileReader = new FileReader(file);
                String fileContent = fileReader.readString();
                String allPath = file.getPath();
                String serviceId = allPath.substring(scriptPath.length(),allPath.length() - suffix.length() - 1);
                log.info("装载聚合服务:{}",serviceId);
                try {
                    Constructor constructor=integratorClazz.getDeclaredConstructor(String.class,String.class,String.class);
                    integratorService = (AbstractIntegratorService) constructor.newInstance(serviceId,suffix,fileContent);
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    throw new BizException(BizResultEnum.INTEGRATOR_SERVICE_CLASS_LOAD_ERROR,e);
                }
                integratorService.init();
                mappings.put(serviceId,integratorService);
            }
        }
    }

    public AbstractIntegratorService getIntegratorService(String serviceId) {
        return this.mappings.get(serviceId);
    }
}
