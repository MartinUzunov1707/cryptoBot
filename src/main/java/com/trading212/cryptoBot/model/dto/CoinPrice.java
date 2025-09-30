package com.trading212.cryptoBot.model.dto;

public class CoinPrice {

    private String coinId;
    private double price;

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCoinId() {
        return coinId;
    }

    public void setCoinId(String coinId) {
        this.coinId = coinId;
    }

    public CoinPrice(String coinId, double price) {
        this.coinId = coinId;
        this.price = price;
    }
}
