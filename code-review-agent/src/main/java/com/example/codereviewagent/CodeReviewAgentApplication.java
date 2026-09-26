package com.example.codereviewagent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This is the entry point of the whole application.
 * Run this file (main method) to start the server.
 * Spring Boot will start a web server on http://localhost:8080
 */
@SpringBootApplication
public class CodeReviewAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeReviewAgentApplication.class, args);
        System.out.println("Code Review Agent started! Open http://localhost:8080 in your browser.");
    }
}
