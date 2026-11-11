package com.starkgroup.vms;

import com.starkgroup.vms.config.DotenvEnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VmsBackendApplication {

    public static void main(String[] args) {
        DotenvEnvironmentPostProcessor.loadDotenvIntoSystemProperties();
        SpringApplication.run(VmsBackendApplication.class, args);
    }
}
