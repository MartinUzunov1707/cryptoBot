package com.trading212.cryptoBot.model.dto;

public class CandleData {
    private long timestamp;
    private double open;
    private double high;
    private double low;
    private double close;
    private String id;

    public CandleData(long timestamp, double open, double high, double low, double close, String id) {
        this.timestamp = timestamp;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.id = id;
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
    public String getId() {return id;}
}
