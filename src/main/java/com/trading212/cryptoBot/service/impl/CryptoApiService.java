package com.trading212.cryptoBot.service.impl;

import com.trading212.cryptoBot.config.CryptoApiConfig;
import com.trading212.cryptoBot.model.dto.CandleData;
import com.trading212.cryptoBot.model.dto.CoinPrice;
import com.trading212.cryptoBot.model.dto.CoinPriceDTO;
import com.trading212.cryptoBot.model.dto.MarketDataDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;

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
    //Only top 10 because of api request per minute limit
    public List<MarketDataDTO> getTop10Gainers(){
        String uri = String.format("%s/coins/markets?per_page=10&vs_currency=USD",apiConfig.getUrl());
        List<MarketDataDTO>  list = cryptoRestClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        list.sort((x1,x2)->Double.compare(x1.getPriceChangePercentage24h(),x2.getPriceChangePercentage24h()));
        Collections.reverse(list);

        return list;
    }

    public List<CandleData> getHistoricalDataById(String id){
       String uri = String.format("%s/coins/%s/ohlc?vs_currency=USD&days=7",apiConfig.getUrl(),id);
        List<CandleData> data = new ArrayList<>();
        double[][] result = cryptoRestClient
                .get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        for(double[] candle : result){
            data.add(new CandleData((long)candle[0],candle[1],candle[2],candle[3],candle[4],id));
        }
        return data;
    }

    public List<CoinPrice> getPricesById(List<String> ids){
        List<CoinPrice> result = new ArrayList<>();
        String uri = String.format("%s/simple/price?vs_currencies=USD&ids=%s",apiConfig.getUrl(),String.join(",",ids));
        CoinPriceDTO data = cryptoRestClient
                .get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(CoinPriceDTO.class);
        for(Map.Entry<String,Object> pair : data.getCoinPrices().entrySet()){
            LinkedHashMap<String,Number> value = (LinkedHashMap<String, Number>) pair.getValue();
            Double price =  value.get("usd").doubleValue();
            result.add(new CoinPrice(pair.getKey(),price));
        }
        return result;
    }



}
