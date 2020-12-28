package com.bizmda.bizsip.serveradaptor.config;

import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.config.ServerAdaptorConfigMapping;
import com.bizmda.bizsip.serveradaptor.ServerAdaptor;
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
public class ServerAdaptorConfiguration {
    @Value("${bizsip.config-path}")
    private String configPath;

    @Bean
    @ConditionalOnProperty(name = "bizsip.config-path", matchIfMissing = false)
    public ServerAdaptorConfigMapping serverAdaptorConfigMapping() {
        try {
            return new ServerAdaptorConfigMapping(this.configPath);
        } catch (BizException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Bean
    @Scope("prototype")
    public ServerAdaptor serverAdaptor() {
        return new ServerAdaptor();
    }
}
