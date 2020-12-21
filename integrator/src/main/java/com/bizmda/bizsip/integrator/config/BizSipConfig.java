package com.bizmda.bizsip.integrator.config;

import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.config.ServerAdaptorConfigMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class BizSipConfig {
    @Value("${bizsip.config-path}")
    private String configPath;

    @Bean
    @ConditionalOnProperty(name = "bizsip.config-path", matchIfMissing = false)
    public IntegratorServiceMapping IntegratorServiceMapping() {
        try {
            return new IntegratorServiceMapping(this.configPath);
        } catch (BizException e) {
            log.error("聚合服务文件装载出错!");
            return null;
        }
    }

    @Bean
    @ConditionalOnProperty(name = "bizsip.config-path", matchIfMissing = false)
    public ServerAdaptorConfigMapping serverAdaptorConfigMapping() {
        return new ServerAdaptorConfigMapping(this.configPath);
    }

}
