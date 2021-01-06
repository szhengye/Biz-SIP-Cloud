package com.bizmda.bizsip.tmservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TmServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TmServiceApplication.class, args);
    }
}
