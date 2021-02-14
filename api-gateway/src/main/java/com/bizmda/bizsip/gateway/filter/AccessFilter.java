package com.bizmda.bizsip.gateway.filter;

import cn.hutool.core.util.IdUtil;
import com.bizmda.bizsip.common.BizConstant;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class AccessFilter implements GatewayFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        List<String> bizServiceId = exchange.getRequest().getHeaders().get("Biz-Service-Id");
        //链路追踪id
        String traceId = IdUtil.fastSimpleUUID();
        MDC.put(BizConstant.LOG_TRACE_ID, traceId);
        ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate()
                .headers(h -> h.add(BizConstant.TRACE_ID_HEADER, traceId))
                .build();

        ServerWebExchange build = exchange.mutate().request(serverHttpRequest).build();
        log.info("bizServiceId:{}",bizServiceId);
        return chain.filter(build);
//        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
