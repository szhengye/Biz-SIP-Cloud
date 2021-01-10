package com.bizmda.bizsip.integrator.config;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.extra.spring.SpringUtil;
import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.common.BizResultEnum;
import com.bizmda.bizsip.integrator.executor.AbstractIntegratorExecutor;
import com.bizmda.bizsip.integrator.executor.ScriptIntegratorExecutor;
import com.bizmda.bizsip.integrator.service.JavaIntegratorServiceInterface;
import com.bizmda.bizsip.integrator.executor.JavaIntegratorExecutor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author 史正烨
 */
@Slf4j
public class IntegratorServiceMapping {
    private Map<String, AbstractIntegratorExecutor> integratorExecutorMap;
    private String configPath;
    private Map<String, JavaIntegratorServiceInterface> javaIntegratorServiceMap = null;

    private static final Map<String,Object> SERVICE_SCRIPT_SUFFIX_MAP = new HashMap<>();
    static {
        SERVICE_SCRIPT_SUFFIX_MAP.put("script", ScriptIntegratorExecutor.class);
    }

    public IntegratorServiceMapping(String configPath) throws BizException {
        this.configPath = configPath;
        this.load();
    }

    public void load() throws BizException {
        String scriptPath;
        this.integratorExecutorMap = new HashMap<>();

        if (this.configPath.endsWith("/")) {
            scriptPath = this.configPath + "service";
        }
        else {
            scriptPath = this.configPath + "/service";
        }
        String suffix;
        AbstractIntegratorExecutor integratorService = null;

        List<File> files = FileUtil.loopFiles(scriptPath);
        for(File file:files) {
            suffix = FileUtil.getSuffix(file).toLowerCase();
            Class<AbstractIntegratorExecutor> integratorClazz = (Class<AbstractIntegratorExecutor>)SERVICE_SCRIPT_SUFFIX_MAP.get(suffix);
            if (integratorClazz != null) {
                FileReader fileReader = new FileReader(file);
                String fileContent = fileReader.readString();
                String allPath = file.getPath();
                String serviceId = allPath.substring(scriptPath.length(),allPath.length() - suffix.length() - 1);
                log.info("装载聚合服务:{}",serviceId);
                try {
                    Constructor<AbstractIntegratorExecutor> constructor=integratorClazz.getDeclaredConstructor(String.class,String.class,String.class);
                    integratorService = constructor.newInstance(serviceId,suffix,fileContent);
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    throw new BizException(BizResultEnum.INTEGRATOR_SERVICE_CLASS_LOAD_ERROR,e);
                }
                integratorService.init();
                integratorExecutorMap.put(serviceId,integratorService);
            }
        }
    }

    private void loadJavaService() {
        this.javaIntegratorServiceMap = SpringUtil.getBeansOfType(JavaIntegratorServiceInterface.class);
        for(String key:this.javaIntegratorServiceMap.keySet()) {
            JavaIntegratorExecutor javaIntegratorExecutor = new JavaIntegratorExecutor(key,"java",null);
            this.integratorExecutorMap.put(key,javaIntegratorExecutor);
        }

    }

    public AbstractIntegratorExecutor getIntegratorService(String serviceId) {
        if (this.javaIntegratorServiceMap == null) {
            this.loadJavaService();
        }
        return this.integratorExecutorMap.get(serviceId);
    }
}
