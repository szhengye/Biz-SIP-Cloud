package com.bizmda.bizsip.sample.serveradaptor.server1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@ComponentScan(value = "com.bizmda.bizsip.config")
//@ComponentScan(value = "com.bizmda.bizsip.sample.serveradaptor.server1")
public class Server1Application {
    public static void main(String[] args) {
        SpringApplication.run(Server1Application.class, args);
    }
}