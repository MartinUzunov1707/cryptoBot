package com.trading212.cryptoBot.config;

import com.trading212.cryptoBot.model.dto.PortfolioDTO;
import com.trading212.cryptoBot.model.dto.Trade;
import com.trading212.cryptoBot.model.enums.Action;
import org.springframework.jdbc.core.RowMapper;

import javax.sound.sampled.Port;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PortfolioDTORowMapper implements RowMapper<PortfolioDTO> {
    @Override
    public PortfolioDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new PortfolioDTO(
                rs.getString("coin_id"),
                rs.getDouble("amount")
        );
    }
}
