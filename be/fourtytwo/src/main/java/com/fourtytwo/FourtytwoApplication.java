package com.fourtytwo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FourtytwoApplication {

    public static void main(String[] args) {
        SpringApplication.run(FourtytwoApplication.class, args);
    }

}
