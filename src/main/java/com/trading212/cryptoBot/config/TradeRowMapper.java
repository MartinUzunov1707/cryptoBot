package com.trading212.cryptoBot.config;

import com.trading212.cryptoBot.model.dto.Trade;
import com.trading212.cryptoBot.model.enums.Action;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TradeRowMapper implements RowMapper<Trade> {
    @Override
    public Trade mapRow(ResultSet rs, int rowNum) throws SQLException{
       return new Trade(
                rs.getTimestamp("date_of_transaction"),
                Action.valueOf(rs.getString("operation")),
                rs.getDouble("price"),
                rs.getDouble("amount"),
                rs.getString("coin_id")
                );
    }
}
