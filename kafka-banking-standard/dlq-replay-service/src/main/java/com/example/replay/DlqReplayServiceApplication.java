package com.example.replay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DlqReplayServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DlqReplayServiceApplication.class, args);
    }
}
