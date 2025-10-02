package com.trading212.cryptoBot.repository;

import com.trading212.cryptoBot.config.PortfolioDTORowMapper;
import com.trading212.cryptoBot.config.TradeRowMapper;
import com.trading212.cryptoBot.model.dto.PortfolioDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

import javax.sound.sampled.Port;

@Repository
public class PortfolioRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<PortfolioDTO> getPortfolio(){
        String sql = "SELECT * FROM portfolio";
        return jdbcTemplate.query(sql, new PortfolioDTORowMapper());
    }

    public int addCoinToPortfolio(PortfolioDTO coin){
        return jdbcTemplate.update("INSERT INTO portfolio(coin_id,amount) VALUES(?,?);",
                coin.getCoinId(),
                coin.getAmount()
        );
    }

    public int deleteCoinById(PortfolioDTO coin){
            return jdbcTemplate.update("DELETE FROM portfolio WHERE coin_id = ?;",
                coin.getCoinId()
        );
    }

    public PortfolioDTO getCoinById(String id){
        String sql = String.format("SELECT FROM portfolio WHERE coin_id = ?;",id);
        return jdbcTemplate.query(sql,new PortfolioDTORowMapper()).get(0);
    }

    public int setAmountByCoinId(PortfolioDTO coin){
        return jdbcTemplate.update("UPDATE portfolio SET amount = ? WHERE coin_id = ?;",
                coin.getAmount(),coin.getCoinId()
        );
    }
}
