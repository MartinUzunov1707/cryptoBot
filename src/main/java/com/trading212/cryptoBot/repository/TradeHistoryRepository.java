package com.trading212.cryptoBot.repository;

import com.trading212.cryptoBot.config.TradeRowMapper;
import com.trading212.cryptoBot.model.dto.Trade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class TradeHistoryRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Trade> getAllTrades(){
        String sql = "SELECT * FROM trade_history";
        return jdbcTemplate.query(sql, new TradeRowMapper());
    }

    public int addTrade(Trade trade){
        return jdbcTemplate.update("INSERT INTO trade_history(operation,coin_id,date_of_transaction,amount,price,currency) VALUES (?,?,?,?,?,'USD')",
                trade.getAction().toString(),
                trade.getCoinId(),
                trade.getTimestamp(),
                trade.getQuantity(),
                trade.getPrice()
        );
    }
}
