package com.bizmda.bizsip.sample.serveradaptor.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author 史正烨
 */
@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
public class SampleServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(SampleServerApplication.class, args);
    }
}