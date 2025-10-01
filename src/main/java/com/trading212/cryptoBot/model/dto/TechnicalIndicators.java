package com.trading212.cryptoBot.model.dto;

public class TechnicalIndicators {
    private double rsi;
    private double macd;
    private double macdSignal;
    private double macdHistogram;
    private double sma20;
    private double sma50;
    private double ema12;
    private double ema26;
    private double bollingerUpper;
    private double bollingerLower;
    private double bollingerMiddle;
    private double stochasticK;
    private double stochasticD;
    private double volumeSma;
    private boolean goldenCross;
    private boolean deathCross;
    private String trend;

    public TechnicalIndicators() {}

    public double getRsi() {
        return rsi;
    }

    public void setRsi(double rsi) {
        this.rsi = rsi;
    }

    public double getMacd() {
        return macd;
    }

    public void setMacd(double macd) {
        this.macd = macd;
    }

    public double getMacdSignal() {
        return macdSignal;
    }

    public void setMacdSignal(double macdSignal) {
        this.macdSignal = macdSignal;
    }

    public double getMacdHistogram() {
        return macdHistogram;
    }

    public void setMacdHistogram(double macdHistogram) {
        this.macdHistogram = macdHistogram;
    }

    public double getSma20() {
        return sma20;
    }

    public void setSma20(double sma20) {
        this.sma20 = sma20;
    }

    public double getSma50() {
        return sma50;
    }

    public void setSma50(double sma50) {
        this.sma50 = sma50;
    }

    public double getEma12() {
        return ema12;
    }

    public void setEma12(double ema12) {
        this.ema12 = ema12;
    }

    public double getEma26() {
        return ema26;
    }

    public void setEma26(double ema26) {
        this.ema26 = ema26;
    }

    public double getBollingerUpper() {
        return bollingerUpper;
    }

    public void setBollingerUpper(double bollingerUpper) {
        this.bollingerUpper = bollingerUpper;
    }

    public double getBollingerLower() {
        return bollingerLower;
    }

    public void setBollingerLower(double bollingerLower) {
        this.bollingerLower = bollingerLower;
    }

    public double getBollingerMiddle() {
        return bollingerMiddle;
    }

    public void setBollingerMiddle(double bollingerMiddle) {
        this.bollingerMiddle = bollingerMiddle;
    }

    public double getStochasticK() {
        return stochasticK;
    }

    public void setStochasticK(double stochasticK) {
        this.stochasticK = stochasticK;
    }

    public double getStochasticD() {
        return stochasticD;
    }

    public void setStochasticD(double stochasticD) {
        this.stochasticD = stochasticD;
    }

    public double getVolumeSma() {
        return volumeSma;
    }

    public void setVolumeSma(double volumeSma) {
        this.volumeSma = volumeSma;
    }

    public boolean isGoldenCross() {
        return goldenCross;
    }

    public void setGoldenCross(boolean goldenCross) {
        this.goldenCross = goldenCross;
    }

    public boolean isDeathCross() {
        return deathCross;
    }

    public void setDeathCross(boolean deathCross) {
        this.deathCross = deathCross;
    }

    public String getTrend() {
        return trend;
    }

    public void setTrend(String trend) {
        this.trend = trend;
    }
}
