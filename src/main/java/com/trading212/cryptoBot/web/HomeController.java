package com.trading212.cryptoBot.web;

import com.trading212.cryptoBot.model.dto.*;
import com.trading212.cryptoBot.model.enums.Action;
import com.trading212.cryptoBot.repository.TradeHistoryRepository;
import com.trading212.cryptoBot.service.impl.CryptoApiService;
import com.trading212.cryptoBot.service.impl.RealTimeAnalysisEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Controller
public class HomeController {
    @Autowired
    public CryptoApiService cryptoApiService;
    @Autowired
    public TradeHistoryRepository tradeHistoryRepository;

    @GetMapping("/")
    public String viewIndex(){
//        TradingStrategy strategy = new SimpleMovingAverageStrategy(10,30);
//        List<MarketDataDTO> topGainers = cryptoApiService.getTop10Gainers();
//        BacktestEngine engine = new BacktestEngine(strategy,10000.0,0.001);
//        for(MarketDataDTO coin : topGainers){
//            List<CandleData> data = cryptoApiService.getHistoricalDataById(coin.getId());
//            BacktestResult res = engine.runBacktest(data);
//            printResults(res);
//        }
        Trade btc = new Trade(Timestamp.from(Instant.now()), Action.BUY,1707,5,"testCoin");
        Trade eth = new Trade(Timestamp.from(Instant.now()), Action.BUY,2707,10,"testCoin2");
        tradeHistoryRepository.addTrade(btc);
        tradeHistoryRepository.addTrade(eth);
        List<Trade> trades = tradeHistoryRepository.getAllTrades();
        for(Trade trade : trades){
            System.out.println(String.format("%s: %s %.2f of %s for %.2f USD",
                    trade.getTimestamp().toString(),
                    trade.getAction().toString(),
                    trade.getQuantity(),
                    trade.getCoinId(),
                    trade.getPrice()));
        }

        return "index";
    }
    //@Scheduled(cron = "*/5 * * * * *")
    private void analyzeInRealTime(){
        List<MarketDataDTO> topGainers = cryptoApiService.getTop10Gainers();
        RealTimeAnalysisEngine engine = new RealTimeAnalysisEngine();
        //
        for(MarketDataDTO data : topGainers){
            List<CandleData> historicalData = cryptoApiService.getHistoricalDataById(data.getId());
            engine.displayAnalysis(engine.analyzeMarket(data,historicalData));
        }
    }
    private static void printResults(BacktestResult result) {
        System.out.println("\n=== BACKTEST RESULTS ===");
        System.out.printf("Strategy: SMA Crossover%n");
        System.out.printf("Initial Capital: $%.2f%n", result.getInitialCapital());
        System.out.printf("Final Capital: $%.2f%n", result.getFinalCapital());
        System.out.printf("Total Return: %.2f%%%n", result.getTotalReturn());
        System.out.printf("Sharpe Ratio: %.2f%n", result.getSharpeRatio());
        System.out.printf("Max Drawdown: %.2f%%%n", result.getMaxDrawdown());
        System.out.printf("Total Trades: %d%n", result.getTotalTrades());
        System.out.printf("Winning Trades: %d%n", result.getWinningTrades());
        System.out.printf("Losing Trades: %d%n", result.getLosingTrades());
        System.out.printf("Win Rate: %.2f%%%n",
                result.getTotalTrades() > 0 ?
                        (double) result.getWinningTrades() / result.getTotalTrades() * 100 : 0);

        // Print recent trades
        System.out.println("\n=== RECENT TRANSACTIONS ===");
        List<Trade> trades = result.getTradeHistory();
        int startIndex = Math.max(0, trades.size() - 10);
        for (int i = startIndex; i < trades.size(); i++) {
            Trade trade = trades.get(i);
            System.out.printf("%s: %s %.6f %s @ $%.2f%n",
                    trade.getTimestamp(),
                    trade.getAction(),
                    trade.getQuantity(),
                    trade.getCoinId(),
                    trade.getPrice());
        }
    }
}