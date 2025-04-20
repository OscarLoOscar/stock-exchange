package com.example.coin_exchange.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.coin_exchange.redis.core.RedisHelper;
import com.example.coin_exchange.repository.StockQuoteYahooRepo;
import com.example.coin_exchange.service.SystemDateServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SystemDateService implements SystemDateServiceImpl {
  private static final int EXPERATION_HOURS = 4;

  @Autowired
  private RedisHelper redisHelper;

  @Autowired
  private StockQuoteYahooRepo stockQuoteYahooRepo;

  @Override
  public String getSystemDate(String symbol) {
    String key = "SYSDATE_" + symbol;
    this.setSysDate(key, LocalDateTime.now().toString());
    String date = this.getSysData(key);
    if (date == null) {
      date = stockQuoteYahooRepo.findMaxRegularMarketTime(symbol)
          .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
    return date;
  }

  private String getSysData(String key) {
    return redisHelper.get(key,String.class);
  }

  private void setSysDate(String key, String date) {
     redisHelper.set(key, date, Duration.ofHours(EXPERATION_HOURS));
}

}
