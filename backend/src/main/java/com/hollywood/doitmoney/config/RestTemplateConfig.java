package com.hollywood.doitmoney.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder // timeout 등 옵션을 주고 싶으면 builder 로 설정
                // .setConnectTimeout(Duration.ofSeconds(3))
                // .setReadTimeout(Duration.ofSeconds(3))
                .build();
    }
}