package com.bizmda.bizsip.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class BizSipConfig {
    @Value("${bizsip.config-path}")
    private String configPath;

    public final static Map<String,String> messageTypeMap = new HashMap<String,String>(){{
        put("simple-json","com.bizmda.bizsip.message.SimpleJsonMessageProcessor");
        put("simple-xml","com.bizmda.bizsip.message.SimpleXmlMessageProcessor");
    }};

    public final static Map<String,String> protocolTypeMap = new HashMap<String,String>() {{
        put("java","com.bizmda.bizsip.serveradaptor.protocol.JavaServerProtocolProcessor");
    }};

    @Bean
    @ConditionalOnProperty(name = "bizsip.config-path", matchIfMissing = false)
    public ScriptServiceMapping scriptServiceMapping() {
        return new ScriptServiceMapping(this.configPath);
    }

    @Bean
    @ConditionalOnProperty(name = "bizsip.config-path", matchIfMissing = false)
    public ServerAdaptorConfigMapping serverAdaptorMapping() {
        return new ServerAdaptorConfigMapping(this.configPath);
    }

}
