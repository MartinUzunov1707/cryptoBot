package com.trading212.cryptoBot.service.impl;

import com.trading212.cryptoBot.model.TradeSignal;
import com.trading212.cryptoBot.model.dto.BacktestResult;
import com.trading212.cryptoBot.model.dto.CandleData;
import com.trading212.cryptoBot.model.dto.Trade;
import com.trading212.cryptoBot.model.enums.Action;
import com.trading212.cryptoBot.service.TradingStrategy;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BacktestEngine {
    private final TradingStrategy strategy;
    private final double initialCapital;
    private final double transactionFee;

    public BacktestEngine(TradingStrategy strategy, double initialCapital, double transactionFee) {
        this.strategy = strategy;
        this.initialCapital = initialCapital;
        this.transactionFee = transactionFee;
    }

    public BacktestResult runBacktest(List<CandleData> historicalData){
        double capital = initialCapital;
        double holdings = 0;
        List<Trade> trades = new ArrayList<>();
        List<Double> portfolioValues = new ArrayList<>();
        double peakPortfolioValue = initialCapital;
        double maxDrawdown = 0;


        for(int i = 0; i<historicalData.size(); i++){
            CandleData currentCandle = historicalData.get(i);
            TradeSignal signal = strategy.analyze(historicalData,i);

            if(signal.getAction() == Action.BUY && capital > 0){
                double quantity = (capital * (1 - transactionFee)) / currentCandle.getClose();
                holdings += quantity;
                double tradeValue = quantity * currentCandle.getClose();
                trades.add(new Trade(
                        LocalDateTime.ofInstant(Instant.ofEpochMilli(currentCandle.getTimestamp()),java.time.ZoneId.systemDefault()),
                        Action.BUY,
                        currentCandle.getClose(),
                        quantity,
                        currentCandle.getId()
                ));
                capital = 0;
            } else if(signal.getAction() == Action.SELL && holdings > 0){
                double tradeValue = holdings * currentCandle.getClose();
                capital += tradeValue * (1-transactionFee);

                trades.add(new Trade(
                        LocalDateTime.ofInstant(Instant.ofEpochMilli(currentCandle.getTimestamp()),java.time.ZoneId.systemDefault()),
                        Action.SELL,
                        currentCandle.getClose(),
                        holdings,
                        currentCandle.getId()
                ));
                holdings = 0;
            }
            double currentPortfolioValue = capital + (holdings * currentCandle.getClose());
            portfolioValues.add(currentPortfolioValue);

            if(currentPortfolioValue > peakPortfolioValue){
                peakPortfolioValue = currentPortfolioValue;
            }
            double drawdown = (peakPortfolioValue - currentPortfolioValue) / peakPortfolioValue;

            if(drawdown > maxDrawdown) {
                maxDrawdown = drawdown;
            }
        }
        double finalPortfolioValue = capital + (holdings * historicalData.get(historicalData.size() - 1).getClose());
        double totalReturn = (finalPortfolioValue - initialCapital) / initialCapital * 100;

        int winningTrades = 0, losingTrades = 0;

        for(int i = 1; i < trades.size(); i+=2){
            Trade buyTrade = trades.get(i-1);
            Trade sellTrade = trades.get(i);
            double profit = (sellTrade.getPrice() - buyTrade.getPrice()) / buyTrade.getPrice() * 100;
            if(profit > 0) winningTrades ++;
            else losingTrades++;
        }

        double sharpeRatio = calculateSharpeRatio(portfolioValues);
        return new BacktestResult(
                initialCapital,
                finalPortfolioValue,
                totalReturn,
                sharpeRatio,
                maxDrawdown * 100,
                trades.size() / 2,
                winningTrades,
                losingTrades,
                trades
        );
    }

    private double calculateSharpeRatio(List<Double> portfolioValues) {
        if(portfolioValues.size() < 2) return 0;

        List<Double> returns = new ArrayList<>();
        for(int i = 1; i < portfolioValues.size(); i++){
            double dailyReturn = (portfolioValues.get(i) - portfolioValues.get(i-1)) / portfolioValues.get(i-1);
            returns.add(dailyReturn);
        }
        double meanReturn = returns.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double standartDeviation = Math.sqrt(returns.stream().mapToDouble(r->Math.pow(r-meanReturn,2)).average().orElse(0));
        return (standartDeviation == 0) ? 0 : (meanReturn/standartDeviation) * Math.sqrt(365); //Annual
    }
}
