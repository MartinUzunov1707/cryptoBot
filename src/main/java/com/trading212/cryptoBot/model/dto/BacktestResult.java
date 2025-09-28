package com.trading212.cryptoBot.model.dto;

import java.util.List;

public class BacktestResult {
    private double initialCapital;
    private double finalCapital;
    private double totalReturn;
    private double sharpeRatio;
    private double maxDrawdown;
    private int totalTrades;
    private int winningTrades;
    private int losingTrades;
    private List<Trade> tradeHistory;

    public BacktestResult(double initialCapital, double finalCapital, double totalReturn,
                          double sharpeRatio, double maxDrawdown, int totalTrades,
                          int winningTrades, int losingTrades, List<Trade> tradeHistory) {
        this.initialCapital = initialCapital;
        this.finalCapital = finalCapital;
        this.totalReturn = totalReturn;
        this.sharpeRatio = sharpeRatio;
        this.maxDrawdown = maxDrawdown;
        this.totalTrades = totalTrades;
        this.winningTrades = winningTrades;
        this.losingTrades = losingTrades;
        this.tradeHistory = tradeHistory;
    }
    public double getInitialCapital(){
        return initialCapital;
    }
    public double getFinalCapital(){
        return finalCapital;
    }
    public double getTotalReturn(){
        return totalReturn;
    }
    public double getSharpeRatio(){
        return sharpeRatio;
    }
    public double getMaxDrawdown(){
        return maxDrawdown;
    }
    public int getTotalTrades(){
        return totalTrades;
    }
    public int getWinningTrades(){
        return winningTrades;
    }
    public int getLosingTrades(){
        return losingTrades;
    }
    public List<Trade> getTradeHistory(){
        return tradeHistory;
    }
}
