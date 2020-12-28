package com.bizmda.bizsip.dynamicconfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class DynamicConfigApplication {
    public static void main(String[] args) {
        SpringApplication.run(com.bizmda.bizsip.dynamicconfig.DynamicConfigApplication.class, args);
    }
}
