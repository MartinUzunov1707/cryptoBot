package com.trading212.cryptoBot.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MarketDataDTO {
    private String id;
    @JsonProperty(value = "current_price")
    private double currentPrice;
    @JsonProperty(value = "total_volume")
    private double totalVolume;
    @JsonProperty(value = "high_24h")
    private double high;
    @JsonProperty(value = "low_24h")
    private double low;
    @JsonProperty(value = "price_change_percentage_24h")
    private double priceChangePercentage24h;

    public MarketDataDTO(String id, double price, double volume, double high, double low, double change24h) {
        this.id = id;
        this.currentPrice = price;
        this.totalVolume = volume;
        this.high = high;
        this.low = low;
        this.priceChangePercentage24h = change24h;
    }

    public String getId() {
        return id;
    }

    public double getPrice() {
        return currentPrice;
    }

    public double getTotalVolume() {
        return totalVolume;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public double getPriceChangePercentage24h() {
        return priceChangePercentage24h;
    }
}
