package com.trading212.cryptoBot.model.dto;

public class CandleData {
    private long timestamp;
    private double open;
    private double high;
    private double low;
    private double close;

    public CandleData(long timestamp, double open, double high, double low, double close) {
        this.timestamp = timestamp;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public double getOpen() {
        return open;
    }
    public double getHigh() {
        return high;
    }
    public double getLow() {
        return low;
    }
    public double getClose() {
        return close;
    }
}
