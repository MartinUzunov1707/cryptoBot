package com.trading212.cryptoBot.service.impl;

import com.trading212.cryptoBot.model.dto.MarketAnalysis;
import com.trading212.cryptoBot.model.dto.Position;
import com.trading212.cryptoBot.model.dto.TechnicalIndicators;
import com.trading212.cryptoBot.model.dto.Trade;
import com.trading212.cryptoBot.model.enums.Action;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class TradeExecutionEngine {
    @Autowired
    private final CryptoApiService apiService;
    private final RealTimeAnalysisEngine analysisEngine;
    private double availableCapital;
    private final double riskPerTrade;
    private final Map<String, Position> activePositions;
    private final List<Trade> tradeHistory;

    public TradeExecutionEngine(CryptoApiService apiService, double initialCapital, double riskPerTrade) {
        this.apiService = apiService;
        this.analysisEngine = new RealTimeAnalysisEngine();
        this.availableCapital = initialCapital;
        this.riskPerTrade = riskPerTrade;
        this.activePositions = new HashMap<>();
        this.tradeHistory = new ArrayList<>();
    }

    public void analyzeAndExecute(MarketAnalysis analysis){
        if(analysis == null){
            System.out.println("Failed to get analysis.");
            return;
        }
        String coinId = analysis.getMarketData().getId();
        String signal = analysis.getSignal();
        double confidence = analysis.getConfidence();
        double currentPrice = analysis.getMarketData().getPrice();

        System.out.println("\n=== TRADE DECISION ===");
        System.out.printf("Symbol: %s | Signal: %s | Confidence: %.1f%% | Price: $%.2f%n",
                coinId, signal, confidence * 100, currentPrice);

        boolean hasPosition = activePositions.containsKey(coinId);

        switch (signal) {
            case "BUY":
                if (!hasPosition && shouldEnterTrade(analysis)) {
                    executeBuy(coinId, currentPrice, analysis);
                } else if (hasPosition) {
                    manageExistingPosition(coinId, currentPrice, analysis);
                }
                break;

            case "SELL":
                if (hasPosition) {
                    executeSell(coinId, currentPrice, "Signal-based exit");
                }
                break;
            case "HOLD":
                if (hasPosition) {
                    manageExistingPosition(coinId, currentPrice, analysis);
                }
                break;
        }
        checkRiskManagement(coinId, currentPrice);
    }

    private boolean shouldEnterTrade(MarketAnalysis analysis) {
        TechnicalIndicators indicators = analysis.getIndicators();
        double confidence = analysis.getConfidence();

        if (confidence < 0.6) {
            return false;
        }

        int confirmations = 0;

        if (indicators.getRsi() < 65 && indicators.getRsi() > 30) {
            confirmations++;
        }

        if (indicators.getMacd() > indicators.getMacdSignal()) {
            confirmations++;
        }

        if ("BULLISH".equals(indicators.getTrend())) {
            confirmations++;
        }

        if (analysis.getMarketData().getTotalVolume() > 1000000) {
            confirmations++;
        }

        return confirmations >= 3;
    }

    private void executeBuy(String symbol, double price, MarketAnalysis analysis) {
        double positionSize = calculatePositionSize(price);

        if (positionSize * price > availableCapital) {
            System.out.println("Insufficient capital for trade");
            return;
        }

        double stopLoss = calculateStopLoss(price, "BUY", analysis);
        double takeProfit = calculateTakeProfit(price, "BUY", analysis);

        Position position = new Position(
                symbol, price, positionSize, LocalDateTime.now(), stopLoss, takeProfit
        );

        activePositions.put(symbol, position);
        availableCapital -= positionSize * price;

        Trade trade = new Trade(
                Timestamp.from(Instant.now()), Action.BUY, price, positionSize, symbol);
        tradeHistory.add(trade);

        System.out.printf("EXECUTED BUY: %.6f %s @ $%.2f | Risk: $%.2f | Reward: $%.2f%n",
                positionSize, symbol, price,
                (price - stopLoss) * positionSize,
                (takeProfit - price) * positionSize);
    }
    private void executeSell(String symbol, double price, String reason) {
        Position position = activePositions.get(symbol);
        if (position == null) return;

        double profitLoss = (price - position.getEntryPrice()) * position.getQuantity();
        double profitLossPercent = ((price - position.getEntryPrice()) / position.getEntryPrice()) * 100;

        availableCapital += position.getQuantity() * price;

        Trade trade = new Trade(Timestamp.from(Instant.now()), Action.SELL, price, position.getQuantity(),symbol);
        tradeHistory.add(trade);

        activePositions.remove(symbol);

        System.out.printf("EXECUTED SELL: %.6f %s @ $%.2f | P/L: $%.2f (%.2f%%)%n",
                position.getQuantity(), symbol, price, profitLoss, profitLossPercent);
    }

    private double calculatePositionSize(double entryPrice) {
        double riskAmount = availableCapital * riskPerTrade;
        double stopLossDistance = entryPrice * 0.02;
        double positionSize = riskAmount / stopLossDistance;

        double maxPositionValue = availableCapital * 0.1;
        double maxPositionSize = maxPositionValue / entryPrice;

        return Math.min(positionSize, maxPositionSize);
    }

    private double calculateStopLoss(double entryPrice, String direction, MarketAnalysis analysis) {
        TechnicalIndicators indicators = analysis.getIndicators();

        if ("BUY".equals(direction)) {
            double bollingerStop = indicators.getBollingerLower();
            double atrStop = entryPrice * 0.98; // 2% stop loss
            return Math.max(bollingerStop, atrStop);
        } else {
            double bollingerStop = indicators.getBollingerUpper();
            double atrStop = entryPrice * 1.02; // 2% stop loss
            return Math.min(bollingerStop, atrStop);
        }
    }

    private double calculateTakeProfit(double entryPrice, String direction, MarketAnalysis analysis) {
        TechnicalIndicators indicators = analysis.getIndicators();

        if ("BUY".equals(direction)) {
            double bollingerTarget = indicators.getBollingerUpper();
            double rrTarget = entryPrice + (2 * (entryPrice - calculateStopLoss(entryPrice, direction, analysis)));
            return Math.min(bollingerTarget, rrTarget);
        } else {
            double bollingerTarget = indicators.getBollingerLower();
            double rrTarget = entryPrice - (2 * (calculateStopLoss(entryPrice, direction, analysis) - entryPrice));
            return Math.max(bollingerTarget, rrTarget);
        }
    }

    private void manageExistingPosition(String symbol, double currentPrice, MarketAnalysis analysis) {
        Position position = activePositions.get(symbol);
        if (position == null) return;

        double unrealizedPnl = (currentPrice - position.getEntryPrice()) * position.getQuantity();
        double unrealizedPercent = ((currentPrice - position.getEntryPrice()) / position.getEntryPrice()) * 100;

        if (analysis.getSignal().equals("SELL") && analysis.getConfidence() > 0.7) {
            executeSell(symbol, currentPrice, "Early exit due to SELL signal");
        }

        applyTrailingStop(symbol, currentPrice, position);

        System.out.printf("Position Update: %s | Current: $%.2f | Unrealized P/L: $%.2f (%.2f%%)%n",
                symbol, currentPrice, unrealizedPnl, unrealizedPercent);
    }

    private void applyTrailingStop(String symbol, double currentPrice, Position position) {
        double newStopLoss = currentPrice * 0.95; // 5% trailing stop
        if (newStopLoss > position.getStopLoss()) {
            position = new Position(
                    symbol, position.getEntryPrice(), position.getQuantity(),
                    position.getEntryTime(), newStopLoss, position.getTakeProfit()
            );
            activePositions.put(symbol, position);
            System.out.printf("Trailing stop updated: $%.2f%n", newStopLoss);
        }
    }

    private void checkRiskManagement(String symbol, double currentPrice) {
        Position position = activePositions.get(symbol);
        if (position == null) return;

        if (currentPrice <= position.getStopLoss()) {
            executeSell(symbol, currentPrice, "Stop loss triggered");
            return;
        }

        if (currentPrice >= position.getTakeProfit()) {
            executeSell(symbol, currentPrice, "Take profit triggered");
        }
    }

    public void printPortfolioStatus() {
        System.out.println("\n=== PORTFOLIO STATUS ===");
        System.out.printf("Available Capital: $%.2f%n", availableCapital);

        double totalPortfolioValue = availableCapital;
        System.out.println("\nActive Positions:");

        for (Position position : activePositions.values()) {
            List<String> ids = new ArrayList<>();
            ids.add(position.getId());
            double currentPrice = apiService.getPricesById(ids).get(0).getPrice();
            double positionValue = currentPrice * position.getQuantity();
            double pnl = (currentPrice - position.getEntryPrice()) * position.getQuantity();
            double pnlPercent = ((currentPrice - position.getEntryPrice()) / position.getEntryPrice()) * 100;

            totalPortfolioValue += positionValue;

            System.out.printf("  %s: %.6f @ $%.2f | Value: $%.2f | P/L: $%.2f (%.2f%%)%n",
                    position.getId(), position.getQuantity(), position.getEntryPrice(),
                    positionValue, pnl, pnlPercent);
        }

        System.out.printf("Total Portfolio Value: $%.2f%n", totalPortfolioValue);
    }

    public void printTradeHistory() {
        System.out.println("\n=== TRADE HISTORY ===");
        for (Trade trade : tradeHistory) {
            System.out.printf("%s | %s %.6f %s @ $%.2f | %s%n",
                    trade.getTimestamp(), trade.getAction(),
                    trade.getQuantity(), trade.getCoinId(),
                    trade.getPrice());
        }
    }
}
