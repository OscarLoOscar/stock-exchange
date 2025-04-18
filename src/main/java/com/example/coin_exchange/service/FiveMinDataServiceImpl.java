package com.example.coin_exchange.service;

import java.util.List;
import com.example.coin_exchange.model.apiResponse.StockQuoteYahooRedis;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface FiveMinDataServiceImpl {
   List<StockQuoteYahooRedis.QuoteResult.RedisData> getFiveMinutesData(
      String symbol) throws JsonProcessingException;
}
