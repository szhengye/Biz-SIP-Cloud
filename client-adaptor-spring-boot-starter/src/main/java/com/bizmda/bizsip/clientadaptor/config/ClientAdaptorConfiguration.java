package com.bizmda.bizsip.clientadaptor.config;

import com.bizmda.bizsip.config.ClientAdaptorConfigMapping;
import com.bizmda.bizsip.config.CommonClientAdaptorConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    @Autowired
    private ClientAdaptorConfigMapping clientAdaptorConfigMapping;

    public CommonClientAdaptorConfig getClientAdaptorConfig(String serverId) {
        return this.clientAdaptorConfigMapping.getClientAdaptorConfig(serverId);
    }

//    public ServerAdaptorProcessor getServerAdaptorProcessor(String serverId) throws BizException {
//        return new ServerAdaptorProcessor(this.serverAdaptorConfigMapping.getServerAdaptorConfig(serverId));
//    }
}
