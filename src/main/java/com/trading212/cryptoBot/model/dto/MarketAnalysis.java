package com.trading212.cryptoBot.model.dto;

import java.util.ArrayList;
import java.util.List;

public class MarketAnalysis {
    private MarketDataDTO marketData;
    private TechnicalIndicators indicators;
    private String signal;
    private double confidence;
    private List<String> insights;

    public MarketAnalysis(MarketDataDTO marketData, TechnicalIndicators indicators) {
        this.marketData = marketData;
        this.indicators = indicators;
        this.insights = new ArrayList<>();
    }

    public void setSignal(String signal) {
        this.signal = signal;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public void addInsights(String insight) {
        this.insights.add(insight);
    }

    public MarketDataDTO getMarketData() {
        return marketData;
    }

    public TechnicalIndicators getIndicators() {
        return indicators;
    }

    public String getSignal() {
        return signal;
    }

    public double getConfidence() {
        return confidence;
    }

    public List<String> getInsights() {
        return insights;
    }
}
