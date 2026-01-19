package com.apm.repository;

import com.apm.model.Trade;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Trade entity database operations.
 */
@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {

    /**
     * Find all trades for a specific user.
     *
     * @param userId the user's UUID
     * @return list of trades belonging to the user
     */
    List<Trade> findByUserId(UUID userId);

    /**
     * Find all trades for a specific ticker symbol.
     *
     * @param ticker the stock ticker symbol
     * @return list of trades for the ticker
     */
    List<Trade> findByTicker(String ticker);

    /**
     * Get distinct ticker symbols from all trades.
     *
     * @return list of unique ticker symbols
     */
    @Query("SELECT DISTINCT t.ticker FROM Trade t")
    List<String> findDistinctTickers();

    /**
     * Find all trades for a user and specific ticker.
     *
     * @param userId the user's UUID
     * @param ticker the stock ticker symbol
     * @return list of matching trades
     */
    List<Trade> findByUserIdAndTicker(UUID userId, String ticker);
}
