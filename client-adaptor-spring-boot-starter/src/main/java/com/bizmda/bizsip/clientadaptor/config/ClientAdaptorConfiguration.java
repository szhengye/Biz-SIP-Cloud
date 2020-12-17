package com.bizmda.bizsip.clientadaptor.config;

import com.bizmda.bizsip.config.ClientAdaptorConfigMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author shizhengye
 */
@Slf4j
@Configuration
public class ClientAdaptorConfiguration {
    @Value("${bizsip.config-path}")
    private String configPath;

    @Bean
    @ConditionalOnProperty(name = "bizsip.config-path", matchIfMissing = false)
    public ClientAdaptorConfigMapping clientAdaptorConfigMapping() {
        return new ClientAdaptorConfigMapping(this.configPath);
    }

    @Bean
    @Scope("prototype")
    public ClientAdaptor clientAdaptor() {
        return new ClientAdaptor();
    }
}
