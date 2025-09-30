package com.trading212.cryptoBot.model.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class CoinPriceDTO {
    private Map<String,Object> coinPrices = new HashMap<>();
    @JsonAnySetter
    void setCoinPrices(String key, Object value){
        coinPrices.put(key,value);
    }

    public Map<String, Object> getCoinPrices() {
        return coinPrices;
    }
}
