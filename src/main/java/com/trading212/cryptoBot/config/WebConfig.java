package com.trading212.cryptoBot.config;

import com.trading212.cryptoBot.service.impl.CryptoApiService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
public class WebConfig {
    @Bean
    public RestClient cryptoRestClient(CryptoApiConfig apiConfig){
        return RestClient.builder()
                .baseUrl(apiConfig.getUrl())
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("x_cg_demo_api_key", apiConfig.getKey())
                .build();
    }
}
