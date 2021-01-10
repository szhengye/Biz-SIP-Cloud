package com.bizmda.bizsip.dynamicconfig.config;

import cn.hutool.core.io.watch.Watcher;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.bizmda.bizsip.common.BizConstant;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.Properties;

/**
 * @author 史正烨
 */
@Slf4j
public class ConfigWatcher implements Watcher {
    private static final int CREATE_FLAG = 1;
    private static final int MODIFY_FLAG = 2;
    private static final int DELETE_FLAG = 3;
    private static final int OVERFLOW_FLAG = 4;

    private String configPath;
    private ConfigService configService;
    public ConfigWatcher(String serverAddr,String configPath) {
        if (configPath.endsWith("/")) {
            this.configPath = configPath;
        }
        else {
            this.configPath = configPath+"/";
        }
        Properties properties = new Properties();
        properties.put("serverAddr", serverAddr);
        try {
            configService = NacosFactory.createConfigService(properties);
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onCreate(WatchEvent<?> event, Path currentPath) {
        Object obj = event.context();
        this.doConfigRefresh(CREATE_FLAG,currentPath.toString()+"/"+obj.toString());
    }

    @Override
    public void onModify(WatchEvent<?> event, Path currentPath) {
        Object obj = event.context();
        this.doConfigRefresh(MODIFY_FLAG,currentPath.toString()+"/"+obj.toString());
    }

    @Override
    public void onDelete(WatchEvent<?> event, Path currentPath) {
        Object obj = event.context();
        this.doConfigRefresh(DELETE_FLAG,currentPath.toString()+"/"+obj.toString());
    }

    @Override
    public void onOverflow(WatchEvent<?> event, Path currentPath) {
        Object obj = event.context();
        this.doConfigRefresh(OVERFLOW_FLAG,currentPath.toString()+"/"+obj.toString());
    }

    private void doConfigRefresh(int flag,String file) {
        file = file.substring(this.configPath.length());
        log.info("配置文件变更:{}-{}",flag,file);
        if("server-adaptor.yml".equalsIgnoreCase(file)) {
            try {
                configService.publishConfig(BizConstant.REFRESH_SERVER_ADAPTOR_DATA_ID, BizConstant.NACOS_GROUP, Long.toString(System.currentTimeMillis()));
            } catch (NacosException e) {
                e.printStackTrace();
            }
        }
        if("client-adaptor.yml".equalsIgnoreCase(file)) {
            try {
                configService.publishConfig(BizConstant.REFRESH_CLIENT_ADAPTOR_DATA_ID, BizConstant.NACOS_GROUP, Long.toString(System.currentTimeMillis()));
            } catch (NacosException e) {
                e.printStackTrace();
            }
        }
        if(file.startsWith("service")) {
            try {
                configService.publishConfig(BizConstant.REFRESH_SERVICE_DATA_ID, BizConstant.NACOS_GROUP, Long.toString(System.currentTimeMillis()));
            } catch (NacosException e) {
                e.printStackTrace();
            }
        }
        if(file.startsWith("message")) {
            try {
                configService.publishConfig(BizConstant.REFRESH_MESSAGE_DATA_ID, BizConstant.NACOS_GROUP, Long.toString(System.currentTimeMillis()));
            } catch (NacosException e) {
                e.printStackTrace();
            }
        }
    }
}
