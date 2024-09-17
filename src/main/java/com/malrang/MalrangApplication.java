package com.malrang;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing
@RequiredArgsConstructor
public class MalrangApplication {
    private static final Logger logger = LoggerFactory.getLogger(MalrangApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(MalrangApplication.class, args);
        logger.info("Application has started.");
    }
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}