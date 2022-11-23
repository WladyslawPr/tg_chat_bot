package com.dev.vlpr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.dev.vlpr.service")
public class DispatcherApp {
    public static void main(String[] args) {
        SpringApplication.run(DispatcherApp.class);

    }
}
