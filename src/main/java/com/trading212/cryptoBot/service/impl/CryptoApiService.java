package com.trading212.cryptoBot.service.impl;

import com.trading212.cryptoBot.config.CryptoApiConfig;
import com.trading212.cryptoBot.model.dto.CandleData;
import com.trading212.cryptoBot.model.dto.MarketDataDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

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
    public List<MarketDataDTO> getTopGainers(){
        String uri = String.format("%s/coins/markets?vs_currency=USD",apiConfig.getUrl());
        List<MarketDataDTO>  list = cryptoRestClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        list.sort((x1,x2)->Double.compare(x1.getPriceChangePercentage24h(),x2.getPriceChangePercentage24h()));
        Collections.reverse(list);

        return list;
    }

//    public List<CandleData> getHistoricalData(){
//        String url = String.format("%s/coins/")
//        cryptoRestClient.get()
//
//    }
}
