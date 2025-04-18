package com.example.coin_exchange.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.coin_exchange.Redis.RedisCacheHelper;
import com.example.coin_exchange.Redis.core.RedisHelper;
import com.example.coin_exchange.entity.StockQuoteYahoo;
import com.example.coin_exchange.mapper.Mapper;
import com.example.coin_exchange.model.CandleStick;
import com.example.coin_exchange.model.EMAData;
import com.example.coin_exchange.model.apiResponse.StockQuoteYahooRedis;
import com.example.coin_exchange.repository.StockQuoteYahooRepo;
import com.example.coin_exchange.service.StockAnalysisServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StockAnalysisService implements StockAnalysisServiceImpl {
  @Autowired
  private StockQuoteYahooRepo stockQuoteYahooRepo;

  @Autowired
  private Mapper mapper;

  @Autowired
  private RedisHelper redisHelper;

  @Override
  public List<EMAData> EMA(String symbol, String period)
      throws IllegalAccessException {
    List<StockQuoteYahoo> stockQuotes = stockQuoteYahooRepo.findAll().stream()//
        .filter(e -> e.getSymbol().equals(symbol)).collect(Collectors.toList());
    List<EMAData> emaDataList = new ArrayList<>();
    int periodInt = Integer.parseInt(period);
    for (int i = periodInt - 1; i < stockQuotes.size(); i++) {
      BigDecimal sum = BigDecimal.ZERO;

      for (int j = i - periodInt + 1; j <= i; j++) {
        sum = sum.add(BigDecimal.valueOf(stockQuotes.get(j).getClose()));
      }
      BigDecimal periodBD = BigDecimal.valueOf(periodInt);
      BigDecimal ema = sum.divide(periodBD, RoundingMode.HALF_UP);

      String dateTime = Instant.ofEpochSecond(stockQuotes.get(i).getTimestamp())//
          .atZone(ZoneId.systemDefault())//
          .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))//
          .toString();

      EMAData emaData = new EMAData(dateTime, ema.doubleValue());
      emaDataList.add(emaData);
    }
    return emaDataList;

  }

  @Override
  public List<CandleStick> convertToOtherTimeFrameCandleStick(String symbol,
      String period) throws JsonProcessingException {
    if ("5m".equals(period))
      return stockQuoteYahooRepo.findAll().stream()//
          .filter(e -> e.getSymbol().equals(symbol))//
          .map(mapper::mapToCandleStick)//
          .collect(Collectors.toList());

    List<StockQuoteYahoo> stockQuotes = stockQuoteYahooRepo.findAll().stream()//
        .filter(e -> e.getSymbol().equals(symbol))//
        .collect(Collectors.toList());
    String keyprefix = "5MIN-";
    StockQuoteYahooRedis existEntry =
        redisHelper.get(keyprefix + symbol, StockQuoteYahooRedis.class);

    long periodInSeconds = this.convertPeriodToSeconds(period);

    List<CandleStick> candleSticks = new ArrayList<>();
    List<StockQuoteYahoo> groupedStockQuotes = new ArrayList<>();
    long currentGroupStartTime = -1;
    for (StockQuoteYahoo stockQuote : stockQuotes) {
      if (currentGroupStartTime == -1) {
        currentGroupStartTime =
            stockQuote.getTimestamp() / periodInSeconds * periodInSeconds;
      }

      if (stockQuote.getTimestamp() >= currentGroupStartTime
          + periodInSeconds) {
        CandleStick candleStick = processGroup(groupedStockQuotes);
        candleSticks.add(candleStick);
        groupedStockQuotes.clear();
        currentGroupStartTime =
            stockQuote.getTimestamp() / periodInSeconds * periodInSeconds;
      }
      groupedStockQuotes.add(stockQuote);
    }
    if (!groupedStockQuotes.isEmpty()) {
      CandleStick candleStick = processGroup(groupedStockQuotes);
      candleSticks.add(candleStick);
    }
    return candleSticks;
  }

  private long convertPeriodToSeconds(String period) {
    switch (period.toLowerCase()) {
      case "15m":
        return 900;
      case "30m":
        return 1800;
      case "1h":
        return 3600;
      case "4h":
        return 3600 * 4;
      case "d":
        return 3600 * 24;
      case "w":
        return 3600 * 24 * 5;
      default:
        throw new IllegalArgumentException("Invalid period : " + period);
    }
  }

  private CandleStick processGroup(List<StockQuoteYahoo> groupStockQuotes) {
    double open = groupStockQuotes.get(0).getOpen();
    double close = groupStockQuotes.get(groupStockQuotes.size() - 1).getClose();
    double high = groupStockQuotes.stream()
        .mapToDouble(StockQuoteYahoo::getHigh).max().getAsDouble();
    double low = groupStockQuotes.stream().mapToDouble(StockQuoteYahoo::getLow)
        .min().getAsDouble();

    String time = LocalDateTime.ofInstant(
        Instant.ofEpochSecond(groupStockQuotes.get(0).getTimestamp()),
        ZoneId.systemDefault()).format(DateTimeFormatter.ISO_DATE_TIME);

    return CandleStick.builder()//
        .open(open)//
        .close(close)//
        .high(high)//
        .low(low)//
        .time(time)//
        .build();
  }
}
