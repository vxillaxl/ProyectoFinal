package com.smarttraffic.processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DensityProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(DensityProcessorApplication.class, args);
    }
}
