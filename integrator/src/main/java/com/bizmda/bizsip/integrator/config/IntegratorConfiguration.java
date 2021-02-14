package com.bizmda.bizsip.integrator.config;

import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.integrator.checkrule.CheckRuleConfigMapping;
import com.bizmda.bizsip.config.ServerAdaptorConfigMapping;
import com.bizmda.log.trace.RestTemplateTraceInterceptor;
import com.bizmda.log.trace.WebTraceFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * @author 史正烨
 */
@Slf4j
@Configuration
public class IntegratorConfiguration {
    @Value("${bizsip.config-path}")
    private String configPath;

    @Bean
    public IntegratorServiceMapping integratorServiceMapping() {
        try {
            return new IntegratorServiceMapping(this.configPath);
        } catch (BizException e) {
            log.error("聚合服务文件装载出错!",e);
            return null;
        }
    }

    @Bean
    public ServerAdaptorConfigMapping serverAdaptorConfigMapping() {
        try {
            return new ServerAdaptorConfigMapping(this.configPath);
        } catch (BizException e) {
            log.error("服务端适配器配置文件装载出错!",e);
            return null;
        }
    }

    @Bean
    public CheckRuleConfigMapping checkRuleConfigMapping() {
        try {
            return new CheckRuleConfigMapping(this.configPath);
        } catch (BizException e) {
            log.error("服务消息域校验文件装载出错!",e);
            return null;
        }
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate()
    {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(new RestTemplateTraceInterceptor()));
        return restTemplate;
    }

//    @Bean
//    public WebTraceFilter webTraceFilter() {
//        return new WebTraceFilter();
//    }
}
