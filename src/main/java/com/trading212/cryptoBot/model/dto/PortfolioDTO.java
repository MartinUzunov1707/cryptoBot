package com.trading212.cryptoBot.model.dto;

public class PortfolioDTO {
    private String coinId;
    private double amount;

    public PortfolioDTO(String coinId, double amount) {
        this.coinId = coinId;
        this.amount = amount;
    }

    public String getCoinId() {
        return coinId;
    }

    public void setCoinId(String coinId) {
        this.coinId = coinId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
