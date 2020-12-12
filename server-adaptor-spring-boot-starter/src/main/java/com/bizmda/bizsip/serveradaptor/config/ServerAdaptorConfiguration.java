package com.bizmda.bizsip.serveradaptor.config;

import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.config.AbstractServerAdaptorConfig;
import com.bizmda.bizsip.config.ServerAdaptorConfigMapping;
import com.bizmda.bizsip.serveradaptor.ServerAdaptorProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

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
    public ServerAdaptorProcessor serverAdaptorProcessor() {
        return new ServerAdaptorProcessor();
    }
}
