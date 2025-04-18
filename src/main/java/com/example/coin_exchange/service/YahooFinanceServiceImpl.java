package com.example.coin_exchange.service;

import java.util.List;
import com.example.coin_exchange.entity.StockQuoteYahoo;
import com.example.coin_exchange.model.apiResponse.YahooApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface YahooFinanceServiceImpl {
  public YahooApiResponse callYahooFinanceAPI(String symbol)
      throws JsonProcessingException;

  public StockQuoteYahoo saveInDB(StockQuoteYahoo stockQuoteYahoo);

  public List<YahooApiResponse> getLatest5MinData(String symbol);
}
