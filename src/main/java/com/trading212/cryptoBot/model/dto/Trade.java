package com.trading212.cryptoBot.model.dto;

import com.trading212.cryptoBot.model.enums.Action;

import java.sql.Timestamp;

public class Trade {

    private Timestamp timestamp;
    private Action action;
    private double price;
    private double quantity;
    private String coinId;

    public Trade(Timestamp timestamp, Action action, double price, double quantity, String coinId) {
        this.timestamp = timestamp;
        this.action = action;
        this.price = price;
        this.quantity = quantity;
        this.coinId = coinId;
    }

    public Timestamp getTimestamp() { return timestamp; }
    public Action getAction() { return action; }
    public double getPrice() { return price; }
    public double getQuantity() { return quantity; }
    public String getCoinId() { return coinId; }

}
