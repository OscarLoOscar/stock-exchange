package com.example.coin_exchange.controller.impl;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.example.coin_exchange.model.apiResponse.YahooApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface YahooControllerImpl {
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/stock/{symbol}")
  public YahooApiResponse getStockData(@PathVariable String symbol)
      throws JsonProcessingException;

  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/stock/{symbol}/latest")
  YahooApiResponse getLatest5MinData(@PathVariable String symbol)
      throws JsonProcessingException;

  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/api/v1/system-date")
  public String getSystemDate(@RequestParam String symbol);
}
