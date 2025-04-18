package com.example.coin_exchange.service.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.coin_exchange.Redis.core.RedisHelper;
import com.example.coin_exchange.entity.StockQuoteYahoo;
import com.example.coin_exchange.mapper.Mapper;
import com.example.coin_exchange.model.apiResponse.StockQuoteYahooRedis;
import com.example.coin_exchange.model.apiResponse.StockQuoteYahooRedis.QuoteResult.RedisData;
import com.example.coin_exchange.repository.StockQuoteYahooRepo;
import com.example.coin_exchange.service.FiveMinDataServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FiveMinDataService implements FiveMinDataServiceImpl {
  private static final int EXPERATION_HOURS = 12;

  @Autowired
  private RedisHelper redisHelper;

  @Autowired
  private StockQuoteYahooRepo stockQuoteYahooRepo;

  @Autowired
  private Mapper mapper;

  @Override
  public List<RedisData> getFiveMinutesData(String symbol)
      throws JsonProcessingException {
    long sysDate =
        LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
    String key = "5MIN-" + symbol;
    StockQuoteYahooRedis data = this.getFiveMinDataFromRedis(key);
    List<StockQuoteYahoo> sysData =
        stockQuoteYahooRepo.findFiveMinDataBySymbolAndDate(symbol, sysDate);
    if (sysData.isEmpty()) {
      log.info("dbData.isEmpty()");
      return new ArrayList<>();
    }
    if (data.getQuoteResult().getData().size() < sysData.size()) {
      return sysData.stream()//
          .map(e -> mapper.toRedisData(e))//
          .toList();
    }
    return data.getQuoteResult().getData();
  }

  private StockQuoteYahooRedis getFiveMinDataFromRedis(String key)
      throws JsonProcessingException {
    return redisHelper.get(key, StockQuoteYahooRedis.class);
  }
}
