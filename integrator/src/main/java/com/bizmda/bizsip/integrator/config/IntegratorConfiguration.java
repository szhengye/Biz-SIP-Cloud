package com.bizmda.bizsip.integrator.config;

import com.alibaba.nacos.api.exception.NacosException;
import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.config.ServerAdaptorConfigMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author 史正烨
 */
@Slf4j
@Configuration
public class IntegratorConfiguration {
    @Value("${bizsip.config-path}")
    private String configPath;

    @Bean
    public IntegratorServiceMapping integratorServiceMapping() {
        try {
            return new IntegratorServiceMapping(this.configPath);
        } catch (BizException e) {
            log.error("聚合服务文件装载出错!",e);
            return null;
        }
    }

    @Bean
    public ServerAdaptorConfigMapping serverAdaptorConfigMapping() {
        try {
            return new ServerAdaptorConfigMapping(this.configPath);
        } catch (BizException e) {
            log.error("服务端适配器配置文件装载出错!",e);
            return null;
        }
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate()
    {
        return new RestTemplate();
    }
}
