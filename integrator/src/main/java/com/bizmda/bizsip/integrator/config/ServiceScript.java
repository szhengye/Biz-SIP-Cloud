package com.bizmda.bizsip.integrator.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServiceScript {
    private String serviceId;
    private String type;
    private String script;
}
