package com.trading212.cryptoBot.service.impl;

import com.trading212.cryptoBot.model.TradeSignal;
import com.trading212.cryptoBot.model.dto.CandleData;
import com.trading212.cryptoBot.model.dto.Trade;
import com.trading212.cryptoBot.model.enums.Action;
import com.trading212.cryptoBot.service.TradingStrategy;

import java.util.List;

public class SimpleMovingAverageStrategy  implements TradingStrategy {
    private final int shortPeriod;
    private  final int longPeriod;

    public SimpleMovingAverageStrategy(int shortPeriod, int longPeriod) {
        this.shortPeriod = shortPeriod;
        this.longPeriod = longPeriod;
    }

    @Override
    public TradeSignal analyze(List<CandleData> historicalData, int currentIndex){
        if(currentIndex < longPeriod){
            return new TradeSignal("Insufficient data", 0.0 ,Action.HOLD);
        }

        double shortSMA = calculateSMA(historicalData,currentIndex,shortPeriod);
        double longSMA = calculateSMA(historicalData,currentIndex,longPeriod);
        double currentPrice = historicalData.get(currentIndex).getClose();

        if(shortSMA > longSMA && historicalData.get(currentIndex-1).getClose() > longSMA){
            return new TradeSignal(String.format("Golden cross: SMA(%d)=%.2f > SMA(%d)=%.2f",shortPeriod, shortSMA, longPeriod, longSMA),
                    0.7,Action.BUY);
        }
        else if(shortSMA < longSMA && historicalData.get(currentIndex-1).getClose() >= longSMA){
            return new TradeSignal(String.format("Death cross: SMA(%d)=%.2f < SMA(%d)=%.2f",shortPeriod,shortSMA,longPeriod,longSMA),
            0.7, Action.SELL);
        }
        return new TradeSignal("No crossover detected", 0.3,Action.HOLD);
    }

    private double calculateSMA(List<CandleData> historicalData, int currentIndex, int period) {
        double sum = 0;
        for(int i = currentIndex - period + 1;i <= currentIndex; i++){
            sum += historicalData.get(i).getClose();
        }
        return sum / period;
    }
    @Override
    public String getName(){
        return String.format("SMA Strategy(%d/%d)",shortPeriod,longPeriod);
    }
}
