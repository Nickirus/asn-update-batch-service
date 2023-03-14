package com.nikitov.asn.update.service.batch.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {

    @Value("${rest-template.connection-timout-millis}")
    private int connectionTimout;
    @Value("${rest-template.read-timout-millis}")
    private int readTimout;
    @Value("${spring.application.name}")
    private String appName;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofMillis(connectionTimout))
                .setReadTimeout(Duration.ofMillis(readTimout))
                .defaultHeader(HttpHeaders.USER_AGENT, appName)
                .build();
    }
}
