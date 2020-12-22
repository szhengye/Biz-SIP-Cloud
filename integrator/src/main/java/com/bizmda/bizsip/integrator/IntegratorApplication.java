package com.bizmda.bizsip.integrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * @author shizhengye
 */
@SpringBootApplication
@EnableDiscoveryClient
//@ComponentScan(value = "com.bizmda.bizsip.config")
@ComponentScan(value = "cn.hutool.extra.spring")
@ComponentScan(value = "com.bizmda.bizsip.integrator")
@Import(cn.hutool.extra.spring.SpringUtil.class)
public class IntegratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(com.bizmda.bizsip.integrator.IntegratorApplication.class, args);
    }

}