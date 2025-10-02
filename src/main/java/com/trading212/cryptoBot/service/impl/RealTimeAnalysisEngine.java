package com.trading212.cryptoBot.service.impl;

import com.trading212.cryptoBot.logic.TechnicalIndicatorCalculator;
import com.trading212.cryptoBot.model.dto.CandleData;
import com.trading212.cryptoBot.model.dto.MarketAnalysis;
import com.trading212.cryptoBot.model.dto.MarketDataDTO;
import com.trading212.cryptoBot.model.dto.TechnicalIndicators;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

public class RealTimeAnalysisEngine {
    private final TechnicalIndicatorCalculator calculator;

    public RealTimeAnalysisEngine() {
        this.calculator = new TechnicalIndicatorCalculator();
    }
    public MarketAnalysis analyzeMarket(MarketDataDTO currentData, List<CandleData> historicalData){
        TechnicalIndicators indicators = calculateAllIndicators(historicalData);

        MarketAnalysis analysis = new MarketAnalysis(currentData, indicators);
        generateSignals(analysis);
        generateInsights(analysis);

        return analysis;
    }

    private TechnicalIndicators calculateAllIndicators(List<CandleData> data) {
        TechnicalIndicators indicators = new TechnicalIndicators();

        indicators.setRsi(calculator.calculateRSI(data, 14));

        indicators.setSma20(calculator.calculateSMA(data, 20));
        indicators.setSma50(calculator.calculateSMA(data, 50));
        indicators.setEma12(calculator.calculateEMA(data, 12));
        indicators.setEma26(calculator.calculateEMA(data, 26));

        double[] macd = calculator.calculateMACD(data);
        indicators.setMacd(macd[0]);
        indicators.setMacdSignal(macd[1]);
        indicators.setMacdHistogram(macd[2]);

        double[] bollinger = calculator.calculateBollingerBands(data, 20, 2);
        indicators.setBollingerUpper(bollinger[0]);
        indicators.setBollingerLower(bollinger[1]);
        indicators.setBollingerMiddle(bollinger[2]);

        double[] stochastic = calculator.calculateStochastic(data, 14,3);
        indicators.setStochasticK(stochastic[0]);
        indicators.setStochasticD(stochastic[1]);

        indicators.setGoldenCross(indicators.getEma12() > indicators.getEma26());
        indicators.setDeathCross(indicators.getEma12() < indicators.getEma26());

        indicators.setTrend(determineTrend(indicators));

        return indicators;
    }

    private String determineTrend(TechnicalIndicators indicators) {
        int bullishSignals = 0;
        int bearishSignals = 0;

        if (indicators.getRsi() > 75) bullishSignals++;
        else bearishSignals++;

        if (indicators.getMacd() > indicators.getMacdSignal()) bullishSignals++;
        else bearishSignals++;

        if (indicators.getSma20() > indicators.getSma50()) bullishSignals++;
        else bearishSignals++;

        if (bullishSignals > bearishSignals) return "BULLISH";
        else if (bearishSignals > bullishSignals) return "BEARISH";
        else return "NEUTRAL";
    }

    private void generateSignals(MarketAnalysis analysis) {
        TechnicalIndicators indicators = analysis.getIndicators();
        double currentPrice = analysis.getMarketData().getPrice();

        int buySignals = 0;
        int sellSignals = 0;
        double confidence = 0;

        if (indicators.getRsi() < 30) {
            buySignals++;
            confidence += 0.3;
        } else if (indicators.getRsi() > 70) {
            sellSignals++;
            confidence += 0.3;
        }

        if (indicators.getMacd() > indicators.getMacdSignal() && indicators.getMacdHistogram() > 0) {
            buySignals++;
            confidence += 0.25;
        } else if (indicators.getMacd() < indicators.getMacdSignal() && indicators.getMacdHistogram() < 0) {
            sellSignals++;
            confidence += 0.25;
        }

        if (currentPrice < indicators.getBollingerLower()) {
            buySignals++;
            confidence += 0.2;
        } else if (currentPrice > indicators.getBollingerUpper()) {
            sellSignals++;
            confidence += 0.2;
        }

        if (indicators.isGoldenCross()) {
            buySignals++;
            confidence += 0.25;
        } else if (indicators.isDeathCross()) {
            sellSignals++;
            confidence += 0.25;
        }

        if (buySignals > sellSignals) {
            analysis.setSignal("BUY");
        } else if (sellSignals > buySignals) {
            analysis.setSignal("SELL");
        } else {
            analysis.setSignal("HOLD");
        }

        analysis.setConfidence(Math.min(confidence, 1.0));
    }
    private void generateInsights(MarketAnalysis analysis) {
        TechnicalIndicators indicators = analysis.getIndicators();

        if (indicators.getRsi() < 30) {
            analysis.addInsight("RSI indicates oversold conditions");
        } else if (indicators.getRsi() > 70) {
            analysis.addInsight("RSI indicates overbought conditions");
        }

        if (indicators.getMacdHistogram() > 0) {
            analysis.addInsight("MACD histogram showing bullish momentum");
        } else {
            analysis.addInsight("MACD histogram showing bearish momentum");
        }

        analysis.addInsight("Overall trend: " + indicators.getTrend());

        if (analysis.getMarketData().getTotalVolume() > 0) {
            analysis.addInsight("24h volume: $" + String.format("%,.0f", analysis.getMarketData().getTotalVolume()));
        }
    }

    public void displayAnalysis(MarketAnalysis analysis) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("REAL-TIME CRYPTO ANALYSIS");
        System.out.println("=".repeat(80));

        MarketDataDTO data = analysis.getMarketData();
        TechnicalIndicators indicators = analysis.getIndicators();

        System.out.printf("Price: $%.2f%n", data.getPrice());
        System.out.printf("Signal: %s (Confidence: %.1f%%)%n",
                analysis.getSignal(), analysis.getConfidence() * 100);

        System.out.println("\n--- TECHNICAL INDICATORS ---");
        System.out.printf("RSI (14): %.2f %s%n", indicators.getRsi(),
                getRSILevel(indicators.getRsi()));
        System.out.printf("MACD: %.4f | Signal: %.4f | Histogram: %.4f%n",
                indicators.getMacd(), indicators.getMacdSignal(),
                indicators.getMacdHistogram());
        System.out.printf("SMA 20/50: %.2f / %.2f%n",
                indicators.getSma20(), indicators.getSma50());
        System.out.printf("EMA 12/26: %.2f / %.2f%n",
                indicators.getEma12(), indicators.getEma26());
        System.out.printf("Bollinger Bands: %.2f | %.2f | %.2f%n",
                indicators.getBollingerUpper(), indicators.getBollingerMiddle(),
                indicators.getBollingerLower());
        System.out.printf("Stochastic: K=%.2f, D=%.2f%n",
                indicators.getStochasticK(), indicators.getStochasticD());

        System.out.println("\n--- MARKET INSIGHTS ---");
        for (String insight : analysis.getInsights()) {
            System.out.println("• " + insight);
        }

        System.out.println("=".repeat(80));
    }

    private String getRSILevel(double rsi) {
        if (rsi < 30) return "[OVERSOLD]";
        if (rsi > 70) return "[OVERBOUGHT]";
        if (rsi > 30 && rsi < 50) return "[BEARISH]";
        if (rsi > 50 && rsi < 70) return "[BULLISH]";
        return "[NEUTRAL]";
    }
}
