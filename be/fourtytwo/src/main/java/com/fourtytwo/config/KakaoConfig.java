package com.fourtytwo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:private.properties") // private.properties 파일 읽기
public class KakaoConfig {

    @Value("${kakao.restapi.key}")
    private String kakaoRestApiKey;

    @Bean
    public String kakaoRestApiKey() {
        return kakaoRestApiKey;
    }
}
