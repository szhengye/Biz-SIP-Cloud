package com.bizmda.bizsip.integrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author shizhengye
 */
@SpringBootApplication
//@ComponentScan(value = "com.bizmda.bizsip.config")
//@ComponentScan(value = "com.bizmda.bizsip.integrator")
public class IntegratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(com.bizmda.bizsip.integrator.IntegratorApplication.class, args);
    }

}