package com.trading212.cryptoBot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "crypto.api")
public class CryptoApiConfig {

    @Value("${spring.crypto.api.url}")
    private String url;
    private String key;

    public String getKey() {return key;}
    public String getUrl() {
        return url;
    }
    public void setKey(String key){this.key = key;}
    public void setUrl(String url) {
        this.url = url;
    }
}
