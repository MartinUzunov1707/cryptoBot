package com.trading212.cryptoBot.service;

import com.trading212.cryptoBot.model.TradeSignal;
import com.trading212.cryptoBot.model.dto.CandleData;

import java.util.List;

public interface TradingStrategy {
    TradeSignal analyze(List<CandleData> historicalData, int currentIndex);

    String getName();
}
