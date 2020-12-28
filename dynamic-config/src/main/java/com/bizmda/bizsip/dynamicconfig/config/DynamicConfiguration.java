package com.bizmda.bizsip.dynamicconfig.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author shizhengye
 */
@Slf4j
@Configuration
public class DynamicConfiguration {
    @Value("${bizsip.config-path}")
    private String configPath;
    @Value("${spring.cloud.nacos.discovery.server-addr}")
    private String serverAddr;

    @Bean
    public ConfigWatchMonitor configWatchMonitor() {
        return new ConfigWatchMonitor(this.serverAddr,this.configPath);
    }
}
