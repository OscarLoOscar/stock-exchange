package com.example.coin_exchange.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import com.example.coin_exchange.Redis.RedisHelper;
import com.example.coin_exchange.controller.YahooControllerImpl;
import com.example.coin_exchange.model.apiResponse.YahooApiResponse;
import com.example.coin_exchange.service.impl.SystemDateService;
import com.example.coin_exchange.service.impl.YahooFinanceService;
import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
public class YahooController implements YahooControllerImpl {
  @Autowired
  private YahooFinanceService yahooFinanceService;

  @Autowired
  private SystemDateService systemDateService;

  @Autowired
  private RedisHelper redisHelper;

  public YahooApiResponse getStockData(String symbol)
      throws JsonProcessingException {
    return yahooFinanceService.callYahooFinanceAPI(symbol);

  }

  @Override
  public YahooApiResponse getLatest5MinData(String symbol)
      throws JsonProcessingException {
    if (redisHelper.get(symbol) instanceof YahooApiResponse) {
      return (YahooApiResponse) redisHelper.get(symbol);
    }
    return yahooFinanceService.callYahooFinanceAPI(symbol);
  }

  @Override
  public String getSystemDate(String symbol) {
    return systemDateService.getSystemDate(symbol);
  }

}
