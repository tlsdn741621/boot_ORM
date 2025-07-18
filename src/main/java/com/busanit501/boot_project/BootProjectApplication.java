package com.busanit501.boot_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BootProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootProjectApplication.class, args);
    }

}
