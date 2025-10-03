package com.trading212.cryptoBot.web;

import com.trading212.cryptoBot.model.dto.*;
import com.trading212.cryptoBot.model.enums.Action;
import com.trading212.cryptoBot.repository.PortfolioRepository;
import com.trading212.cryptoBot.repository.TradeHistoryRepository;
import com.trading212.cryptoBot.service.impl.*;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.sound.sampled.Port;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Controller
public class HomeController {
    @Autowired
    public CryptoApiService cryptoApiService;
    @Autowired
    public TradeHistoryRepository tradeHistoryRepository;
    @Autowired
    public PortfolioRepository portfolioRepository;
    public PerformanceMetrics globalPerformanceMetrics = new PerformanceMetrics(0,0,0,10000);
    public ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    Boolean isTrading = false;
    Boolean isRunning = false;
    public BacktestEngine backtestEngine = new BacktestEngine(new SimpleMovingAverageStrategy(10,30),10000,0.001);
    public TradeExecutionEngine tradeExecutionEngine = new TradeExecutionEngine(cryptoApiService,tradeHistoryRepository,portfolioRepository,10000,0.02);
    public ScheduledFuture task;

    @GetMapping("/")
    public String viewIndex(Model model){

        List<Trade> trades = tradeHistoryRepository.getAllTrades();
        model.addAttribute("allTrades", trades);
        model.addAttribute("toggleForm",new ToggleFormDTO(isTrading));
        model.addAttribute("performanceMetrics",globalPerformanceMetrics);
        return "index";
    }
    @PostMapping("/switch-mode")
    public String switchMode(@ModelAttribute ToggleFormDTO data, Model model){
        isTrading = data.getChecked();
        return "redirect:/";
    }
    @PostMapping("/start")
    public String startBot(){
        System.out.println("started");
        if(isRunning!= null) {
            List<MarketDataDTO> watchlist = cryptoApiService.getTopGainers();
            RealTimeAnalysisEngine engine = new RealTimeAnalysisEngine();
            List<PortfolioDTO> portfolio = portfolioRepository.getPortfolio();
            if (!portfolio.isEmpty()) {
                List<String> ids = portfolio.stream().map(x -> x.getCoinId()).collect(Collectors.toList());
                cryptoApiService.getMarketDataByIds(ids).forEach(x -> watchlist.add(x));
            }
            task = scheduler.scheduleAtFixedRate(() -> {
                try {
                    if(!isRunning){
                        isRunning = true;
                        for (MarketDataDTO data : watchlist) {
                            List<CandleData> historicalData = cryptoApiService.getHistoricalDataById(data.getId());
                            PerformanceMetrics metrics;
                            if (isTrading) {
                                metrics = tradeExecutionEngine.analyzeAndExecute(engine.analyzeMarket(data, historicalData));
                            } else {
                                BacktestResult res = backtestEngine.runBacktest(historicalData);
                                printResults(res);
                                metrics = new PerformanceMetrics(res.getTotalTrades(),
                                        ((double) res.getWinningTrades() / res.getTotalTrades()) * 100,
                                        0,
                                        res.getFinalCapital());
                                metrics.setHypotheticalPortfolioVolume(res.getFinalCapital());
                            }
                            globalPerformanceMetrics.setActualPortfolioVolume(metrics.getActualPortfolioVolume());
                            globalPerformanceMetrics.setHypotheticalPortfolioVolume(metrics.getHypotheticalPortfolioVolume());
                            globalPerformanceMetrics.setActiveTrades(globalPerformanceMetrics.getActiveTrades() + metrics.getActiveTrades());
                            globalPerformanceMetrics.setTotalTrades(globalPerformanceMetrics.getTotalTrades() + metrics.getTotalTrades());
                            globalPerformanceMetrics.setAllHoldings(calculateAllHoldins(portfolio));
                            Thread.sleep(25000);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error in trading cycle: " + e.getMessage());
                }
            }, 0, 45, TimeUnit.MINUTES);
        }
        return "redirect:/";
    }
    @PostMapping("/pause")
    public String pauseBot(){
       isRunning = false;
        System.out.println("paused");
        return "redirect:/";
    }
    @PostMapping("/reset")
    public String resetBot(){
        isRunning = false;
        task.cancel(true);
//        scheduler.shutdown();
//        try {
//            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
//                scheduler.shutdownNow();
//            }
//        } catch (InterruptedException e) {
//            scheduler.shutdownNow();
//            Thread.currentThread().interrupt();
//        }

        return "redirect:/";
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
    private double calculateAllHoldins( List<PortfolioDTO> portfolio ){
        double sum = 0;
        for(PortfolioDTO data : portfolio){
            sum += data.getAmount();
        }
        return sum;
    }
}