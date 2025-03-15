package com.example.demo;

import org.springframework.boot.SpringApplication;

public class TestCaseApplication {

    public static void main(String[] args) {
        SpringApplication.from(CaseApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }
}
