package com.trading212.cryptoBot.logic;

import com.trading212.cryptoBot.model.dto.MarketDataDTO;

import java.util.ArrayList;
import java.util.List;

public class TechnicalIndicatorCalculator {
    public double calculateRSI(List<MarketDataDTO> data, int period){
        if(data.size() < period + 1){
            return 50.0;
        }
        double gains = 0;
        double losses = 0;
        for(int i = data.size() - period; i < data.size(); i++){
            double priceChange = data.get(i).getPrice() - data.get(i-1).getPrice();
            if(priceChange > 0){
                gains += priceChange;
            }else{
                losses -= priceChange;
            }
        }
        double avgGain = gains / period;
        double avgLoss = losses/period;
        if(avgLoss == 0){
            return 100.0;
        }
        double result = avgGain / avgLoss;
        return 100 - (100 / (1 + result));
    }

    public double calculateSMA(List<MarketDataDTO> data, int period){
        if(data.size() < period){
            return data.get(data.size() - 1).getPrice();
        }

        double sum = 0;
        for(int i = data.size() - period; i <  data.size(); i++){
            sum += data.get(i).getPrice();
        }
        return sum / period;
    }

    public double calculateEMA(List<MarketDataDTO> data, int period){
        if(data.size() < period){
            return data.get(data.size() - 1).getPrice();
        }

        double multiplier = 2.0 / (period + 1);
        double ema = calculateSMA(data.subList(0,period), period);

        for(int i = period; i<data.size(); i++){
            ema = (data.get(i).getPrice() - ema) * multiplier + ema;
        }
        return ema;
    }

    public double[] calculateMACD(List<MarketDataDTO> data){
        double ema12 = calculateEMA(data,12);
        double ema26 = calculateEMA(data,26);
        double macd = ema12 - ema26;
        double signal = calculateEmaForMACD(data,9,macd);
        double histogram = macd - signal;
        return new double[]{macd,signal,histogram};
    }

    private double calculateEmaForMACD(List<MarketDataDTO> data, int period, double currentMACD) {
        List<MarketDataDTO> macdData = data.subList(Math.max(0, data.size() - 50), data.size());
        if (macdData.size() < period) {
            return currentMACD;
        }

        double multiplier = 2.0 / (period + 1);
        double ema = macdData.get(0).getPrice(); // Using price as placeholder

        for (int i = 1; i < macdData.size(); i++) {
            ema = (macdData.get(i).getPrice() - ema) * multiplier + ema;
        }

        return ema;
    }

    private double[] calculateBollingerBands(List<MarketDataDTO> data, int period, double stdDevMultiplier){
        double sma = calculateSMA(data,period);
        if(data.size() < period){
            return new double[]{sma,sma,sma};
        }
        double sumSquaredDiff = 0;
        for(int i = data.size() - period; i < data.size();i++){
            double diff = data.get(i).getPrice() - sma;
            sumSquaredDiff += diff * diff;
        }
        double stdDev = Math.sqrt(sumSquaredDiff / period);
        double upperBand = sma + (stdDev * stdDevMultiplier);
        double lowerBand = sma - (stdDev * stdDevMultiplier);

        return new double[]{upperBand, lowerBand, sma};
    }
    public double[] calculateStochastic(List<MarketDataDTO> data, int periodK, int periodD) {
        if (data.size() < periodK) {
            return new double[]{50.0, 50.0};
        }

        double stochasticK = calculateStochasticK(data, periodK);

        double stochasticD = calculateSMAForStochastic(data, periodD, stochasticK);

        return new double[]{stochasticK, stochasticD};
    }

    private double calculateStochasticK(List<MarketDataDTO> data, int period) {
        int startIndex = Math.max(0, data.size() - period);
        List<MarketDataDTO> periodData = data.subList(startIndex, data.size());

        double lowestLow = Double.MAX_VALUE;
        double highestHigh = Double.MIN_VALUE;
        double currentClose = data.get(data.size() - 1).getPrice();
        for (MarketDataDTO candle : periodData) {
            double price = candle.getPrice();
            lowestLow = Math.min(lowestLow, price);
            highestHigh = Math.max(highestHigh, price);
        }

        if (highestHigh == lowestLow) {
            return 50.0; // Neutral position
        }

        return ((currentClose - lowestLow) / (highestHigh - lowestLow)) * 100;
    }

    public double calculateSMAForStochastic(List<MarketDataDTO> data, int periodD, double currentK) {

        if (data.size() < periodD) {
            return currentK;
        }

        List<Double> kValues = new ArrayList<>();

        for (int i = data.size() - periodD; i < data.size(); i++) {
            double kValue = calculateHistoricalStochasticK(data, i, periodD);
            kValues.add(kValue);
        }

        kValues.add(currentK);

        if (kValues.size() > periodD) {
            kValues = kValues.subList(kValues.size() - periodD, kValues.size());
        }

        double sum = 0;
        for (double k : kValues) {
            sum += k;
        }

        return sum / kValues.size();
    }

    private double calculateHistoricalStochasticK(List<MarketDataDTO> data, int endIndex, int period) {
        int startIndex = Math.max(0, endIndex - period + 1);
        List<MarketDataDTO> periodData = data.subList(startIndex, endIndex + 1);

        double lowestLow = Double.MAX_VALUE;
        double highestHigh = Double.MIN_VALUE;
        double currentClose = data.get(endIndex).getPrice();

        for (MarketDataDTO candle : periodData) {
            double price = candle.getPrice();
            lowestLow = Math.min(lowestLow, price);
            highestHigh = Math.max(highestHigh, price);
        }

        if (highestHigh == lowestLow) {
            return 50.0;
        }

        return ((currentClose - lowestLow) / (highestHigh - lowestLow)) * 100;
    }
}
