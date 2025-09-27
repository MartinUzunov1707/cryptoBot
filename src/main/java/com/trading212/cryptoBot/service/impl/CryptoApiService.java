package com.trading212.cryptoBot.service.impl;

import com.trading212.cryptoBot.config.CryptoApiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class CryptoApiService {
    @Autowired
    private RestClient cryptoRestClient;
    @Autowired
    private CryptoApiConfig apiConfig;

    public CryptoApiService(RestClient cryptoRestClient, CryptoApiConfig apiConfig){
        this.apiConfig = apiConfig;
        this.cryptoRestClient = cryptoRestClient;
    }
}
