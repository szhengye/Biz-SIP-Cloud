package com.bizmda.log.trace;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StringUtils;

import java.io.IOException;

public class RestTemplateTraceInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        String traceId = MDCTraceUtils.getTraceId();
        if (!StringUtils.isEmpty(traceId)) {
            HttpHeaders headers = httpRequest.getHeaders();
            headers.add(MDCTraceUtils.TRACE_ID_HEADER,traceId);
        }
        return clientHttpRequestExecution.execute(httpRequest, bytes);
    }
}
