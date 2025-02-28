package com.example.coin_exchange.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.coin_exchange.entity.StockQuoteYahoo;
import com.example.coin_exchange.model.apiResponse.YahooApiResponse;

@Repository
public interface StockQuoteYahooRepo
    extends JpaRepository<StockQuoteYahoo, Integer> {
  @Query(
      value = "SELECT * , AVG(close) OVER (PARTITION By symbol ORDER By timestamp ROWS BETWEEN :period - 1 PRECEDING ANd CURRENT ROW)AS sma FROM tstock_quote_yahoo WHERE symbol=:symbol AND MOD(EXTRACT(EPOCH FROM TO_TIMESTAMP(timestamp)),300)=0",
      nativeQuery = true)
  List<StockQuoteYahoo> findBySymbolAndPeriod(@Param("sumbol") String symbol,
      @Param("period") int period);

      List<YahooApiResponse> findTop5BySymbolOrderByTimestamp(String symbol);

  @Query(
      value = "SELECT MAX(t.timestamp) FROM TSTOCK_QUOTE_YAHOO t WHERE t.symbol = :symbol",
      nativeQuery = true)
  LocalDateTime findMaxRegularMarketTime(@Param("symbol") String symbol);

  @Query(
      value = "SELECT MAX(t.timestamp) FROM TSTOCK_QUOTE_YAHOO t WHERE t.symbol = :symbol AND DATE(TO_TIMESTAMP(t.timestamp)) < DATE(TO_TIMESTAMP(:sysDate))",
      nativeQuery = true)
  LocalDateTime findMaxRegularMarketBeforeDate(@Param("symbol") String symbol,
      @Param("sysDate") Long sysDate);

  @Query(
      value = "SELECT * FROM TSTOCK_QUOTE_YAHOO t WHERE t.symbol = :symbol AND DATE(TO_TIMESTAMP(t.timestamp)) = DATE(TO_TIMESTAMP(:sysDate))",
      nativeQuery = true)
  List<StockQuoteYahoo> findFiveMinDataBySymbolAndDate(
      @Param("symbol") String symbol, @Param("sysDate") Long sysDate);

  @Query(
      value = "SELECT * FROM TSTOCK_QUOTE_YAHOO t WHERE t.symbol = :symbol "
          + "AND t.timestamp BETWEEN :startTime AND :currentTime",
      nativeQuery = true)
  List<StockQuoteYahoo> findBySymbolAndPeriod(@Param("symbol") String symbol,
      @Param("startTime") long startTime,
      @Param("currentTime") long currentTime);
}
