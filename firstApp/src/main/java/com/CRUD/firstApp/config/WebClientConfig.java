// WebClientConfig.java
package com.CRUD.firstApp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${rasa.url:http://localhost:5006}")
    private String rasaUrl;

    @Bean
    public WebClient rasaClient(WebClient.Builder builder) {
        return builder
                .baseUrl(rasaUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
