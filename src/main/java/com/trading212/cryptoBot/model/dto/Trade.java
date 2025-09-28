package com.trading212.cryptoBot.model.dto;

import java.time.LocalDateTime;

public class Trade {
    public enum Action { BUY, SELL }

    private LocalDateTime timestamp;
    private Action action;
    private double price;
    private double quantity;
    private String symbol;

    public Trade(LocalDateTime timestamp, Action action, double price, double quantity, String symbol) {
        this.timestamp = timestamp;
        this.action = action;
        this.price = price;
        this.quantity = quantity;
        this.symbol = symbol;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public Action getAction() { return action; }
    public double getPrice() { return price; }
    public double getQuantity() { return quantity; }
    public String getSymbol() { return symbol; }

}
