package com.bizmda.bizsip.integrator.config;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.annotation.NacosConfigListener;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.alibaba.nacos.api.exception.NacosException;
import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.common.NacosConstants;
import com.bizmda.bizsip.config.ServerAdaptorConfigMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Properties;

/**
 * @author 史正烨
 */
@Slf4j
@Service
public class IntegratorRefreshConfig {
    @Value("${spring.cloud.nacos.discovery.server-addr}")
    private String serverAddr;
//    @Value("${bizsip.config-path}")
//    private String configPath;
//    @Autowired
//    IntegratorServiceMapping integratorServiceMapping;
//    @Autowired
//    ServerAdaptorConfigMapping serverAdaptorConfigMapping;

    private ConfigService configService;

    @PostConstruct
    public void init() throws NacosException {
        Properties properties = new Properties();
        properties.put("serverAddr", this.serverAddr);
        this.configService = NacosFactory.createConfigService(properties);

        this.configService.addListener(NacosConstants.REFRESH_SERVER_ADAPTOR_DATA_ID, NacosConstants.NACOS_GROUP, new AbstractListener() {
            @Override
            public void receiveConfigInfo(String config) {
                log.info("刷新服务端适配器配置");
                ServerAdaptorConfigMapping serverAdaptorConfigMapping = SpringUtil.getBean("serverAdaptorConfigMapping");
                try {
                    serverAdaptorConfigMapping.load();
                } catch (BizException e) {
                    log.error("服务端适配器配置装载失败!",e);
                }
            }
        });
//        this.configService.addListener(NacosConstants.REFRESH_CLIENT_ADAPTOR_DATA_ID, NacosConstants.NACOS_GROUP, new AbstractListener() {
//            @Override
//            public void receiveConfigInfo(String config) {
//                log.info("刷新客户端适配器配置");
//            }
//        });
        this.configService.addListener(NacosConstants.REFRESH_SERVICE_DATA_ID, NacosConstants.NACOS_GROUP, new AbstractListener() {
            @Override
            public void receiveConfigInfo(String config) {
                log.info("刷新服务配置");
                IntegratorServiceMapping integratorServiceMapping = SpringUtil.getBean("integratorServiceMapping");
                try {
                    integratorServiceMapping.load();
                } catch (BizException e) {
                    log.error("服务配置装载失败!",e);
                }
            }
        });
//        this.configService.addListener(NacosConstants.REFRESH_MESSAGE_DATA_ID, NacosConstants.NACOS_GROUP, new AbstractListener() {
//            @Override
//            public void receiveConfigInfo(String config) {
//                log.info("刷新消息配置");
//            }
//        });
//        log.info("配置刷新监控初始化成功!");

    }
}
