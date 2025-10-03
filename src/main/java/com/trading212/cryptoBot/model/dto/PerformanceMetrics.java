package com.trading212.cryptoBot.model.dto;

public class PerformanceMetrics {
    private int totalTrades;
    private int activeTrades;
    private double winRate;
    private double hypotheticalPortfolioVolume;
    private double actualPortfolioVolume;
    private double allHoldings;
    private double pnl;

    public PerformanceMetrics(int totalTrades, double winRate, int activeTrades, double actualPortfolioVolume) {
        this.winRate = winRate;
        this.totalTrades = totalTrades;
        this.activeTrades = activeTrades;
        this.actualPortfolioVolume = actualPortfolioVolume;
        allHoldings = 0;
        this.hypotheticalPortfolioVolume = actualPortfolioVolume;
        this.pnl = this.actualPortfolioVolume - 10000;
    }

    public double getPnl() {
        return pnl;
    }

    public double getHypotheticalPortfolioVolume() {
        return hypotheticalPortfolioVolume;
    }

    public void setHypotheticalPortfolioVolume(double hypotheticalPortfolioVolume) {
        this.hypotheticalPortfolioVolume = hypotheticalPortfolioVolume;
    }

    public double getActualPortfolioVolume() {
        return actualPortfolioVolume;
    }

    public void setActualPortfolioVolume(double actualPortfolioVolume) {
        this.actualPortfolioVolume = actualPortfolioVolume;
    }

    public void setPnl(double pnl) {
        this.pnl = pnl;
    }

    public double getAllHoldings() {
        return allHoldings;
    }

    public void setAllHoldings(double allHoldings) {
        this.allHoldings = allHoldings;
    }

    public double getWinRate() {
        return winRate;
    }

    public void setWinRate(double winRate) {
        this.winRate = winRate;
    }

    public int getTotalTrades() {
        return totalTrades;
    }

    public void setTotalTrades(int totalTrades) {
        this.totalTrades = totalTrades;
    }


    public int getActiveTrades() {
        return activeTrades;
    }

    public void setActiveTrades(int activeTrades) {
        this.activeTrades = activeTrades;
    }
}
