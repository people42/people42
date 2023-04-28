package com.fourtytwo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:private.properties") // private.properties 파일 읽기
public class AppleConfig {

    @Value("${apple.team.id}")
    private String appleTeamId;
    @Bean
    public String appleTeamId() {return appleTeamId;}

    @Value("${apple.key.id}")
    private String appleKeyId;
    @Bean
    public String appleKeyId() {return appleKeyId;}

    @Value("${apple.client.id}")
    private String appleClientId;
    @Bean
    public String appleClientId() {return appleClientId;}

    @Value("${apple.key.path}")
    private String appleKeyPath;
    @Bean
    public String appleKeyPath() {return appleKeyPath;}


}
