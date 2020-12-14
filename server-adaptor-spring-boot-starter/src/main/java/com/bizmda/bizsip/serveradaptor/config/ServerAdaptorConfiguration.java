package com.bizmda.bizsip.serveradaptor.config;

import com.bizmda.bizsip.config.ServerAdaptorConfigMapping;
import com.bizmda.bizsip.serveradaptor.ServerAdaptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ServerAdaptorConfiguration {
    @Value("${bizsip.config-path}")
    private String configPath;

    @Bean
    @ConditionalOnProperty(name = "bizsip.config-path", matchIfMissing = false)
    public ServerAdaptorConfigMapping serverAdaptorConfigMapping() {
        return new ServerAdaptorConfigMapping(this.configPath);
    }

    @Bean
    public ServerAdaptor serverAdaptor() {
        return new ServerAdaptor();
    }
}
