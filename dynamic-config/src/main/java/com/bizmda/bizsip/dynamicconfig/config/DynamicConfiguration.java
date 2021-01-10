package com.bizmda.bizsip.dynamicconfig.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 史正烨
 */
@Slf4j
@Configuration
@Getter
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
