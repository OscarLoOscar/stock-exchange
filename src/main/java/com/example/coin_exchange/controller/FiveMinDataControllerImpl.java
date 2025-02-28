package com.example.coin_exchange.controller.impl;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.example.coin_exchange.model.apiResponse.StockQuoteYahooRedis;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface FiveMinDataControllerImpl {

  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/stock/{symbol}")
  @CrossOrigin
  List<StockQuoteYahooRedis.QuoteResult.RedisData> getFiveMinutesData(
      @PathVariable String symbol) throws JsonProcessingException;

}
