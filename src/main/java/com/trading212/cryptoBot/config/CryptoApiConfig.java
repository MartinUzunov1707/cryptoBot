package com.trading212.cryptoBot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cryptoMarketCap.api")
public class CryptoApiConfig {
    private String baseUrl;
    private String key;

    public String getKey() {return key;}
    public String getBaseUrl() {
        return baseUrl;
    }
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
