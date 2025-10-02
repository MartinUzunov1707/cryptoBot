package com.trading212.cryptoBot.model.dto;

import java.time.LocalDateTime;

public class Position {
    private String id;
    private double entryPrice;
    private double quantity;
    private LocalDateTime entryTime;
    private double stopLoss;
    private double takeProfit;

    public Position(String id, double entryPrice, double quantity,
                    LocalDateTime entryTime, double stopLoss, double takeProfit) {
        this.id = id;
        this.entryPrice = entryPrice;
        this.quantity = quantity;
        this.entryTime = entryTime;
        this.stopLoss = stopLoss;
        this.takeProfit = takeProfit;
    }

    public String getId() { return id; }
    public double getEntryPrice() { return entryPrice; }
    public double getQuantity() { return quantity; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public double getStopLoss() { return stopLoss; }
    public double getTakeProfit() { return takeProfit; }
}
