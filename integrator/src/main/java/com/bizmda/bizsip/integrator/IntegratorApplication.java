package com.bizmda.bizsip.integrator;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * @author 史正烨
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.bizmda.bizsip.db.mapper")
@ComponentScan(basePackages={"com.bizmda.bizsip.integrator","cn.hutool.extra.spring","com.bizmda.bizsip.db"})
@Import(cn.hutool.extra.spring.SpringUtil.class)
public class IntegratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(com.bizmda.bizsip.integrator.IntegratorApplication.class, args);
    }

}