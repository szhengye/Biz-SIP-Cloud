package com.bizmda.bizsip.gateway.config;

import com.bizmda.bizsip.gateway.filter.AccessFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator getRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(predicateSpec -> predicateSpec
                        .path("/api/**")
                        .filters(gatewayFilterSpec -> gatewayFilterSpec.filter(new AccessFilter()))
                        .uri("lb://sip-integrator")
                        .id("sip-integrator"))
                .build();
    }
}
