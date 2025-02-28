package com.example.coin_exchange.controller.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.coin_exchange.controller.FiveMinDataControllerImpl;
import com.example.coin_exchange.model.apiResponse.StockQuoteYahooRedis;
import com.example.coin_exchange.service.FiveMinDataServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
@RequestMapping("/5min")
public class FiveMinDataController implements FiveMinDataControllerImpl {

  @Autowired
  private FiveMinDataServiceImpl fiveMinDataService;

  @Override
  public List<StockQuoteYahooRedis.QuoteResult.RedisData> getFiveMinutesData(
      String symbol) throws JsonProcessingException {
    
    return fiveMinDataService.getFiveMinutesData(symbol.toUpperCase());
  }


}