package com.fourtytwo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:private.properties") // private.properties 파일 읽기
public class GoogleMapConfig {

    @Value("${google.map.key}")
    private String googleMapKey;

    @Bean
    public String googleMapKey() {
        return googleMapKey;
    }

}
