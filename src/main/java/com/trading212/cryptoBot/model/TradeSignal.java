package com.trading212.cryptoBot.model;

import com.trading212.cryptoBot.model.enums.Action;

public class TradeSignal {
    private Action action;
    private double confidence;
    private String reason;

    public TradeSignal(String reason, double confidence, Action action) {
        this.reason = reason;
        this.confidence = confidence;
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    public double getConfidence() {
        return confidence;
    }

    public String getReason() {
        return reason;
    }
}