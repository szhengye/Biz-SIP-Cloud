package com.bizmda.bizsip.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class BizSipConfig {
    @Value("${bizsip.config-path}")
    private String configPath;

//    @Autowired
//    private ServerAdaptorsConfig serverAdaptorsConfig;

    @Bean
    @ConditionalOnProperty(name = "bizsip.config-path", matchIfMissing = false)
    public ScriptServiceMapping scriptServiceMapping() {
//        this.setupMagicModules();
        return new ScriptServiceMapping(this.configPath);
    }

    @Bean
    @ConditionalOnProperty(name = "bizsip.config-path", matchIfMissing = false)
    public ServerAdaptorMapping serverAdaptorMapping() {
//        this.setupMagicModules();
        return new ServerAdaptorMapping(this.configPath);
    }

}
