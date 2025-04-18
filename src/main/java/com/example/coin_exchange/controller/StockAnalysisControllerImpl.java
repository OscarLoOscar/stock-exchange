package com.example.coin_exchange.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.example.coin_exchange.model.CandleStick;
import com.example.coin_exchange.model.EMAData;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface StockAnalysisControllerImpl {
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  @CrossOrigin
  List<EMAData> EMA(@RequestParam String symbol, @RequestParam String period)
      throws IllegalAccessException;

  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/timeframe")
  @CrossOrigin("http://localhost:5173")
  List<CandleStick> convertToOtherTimeFrameCandleStick(
      @RequestParam String symbol, @RequestParam String period)
      throws JsonProcessingException;
}
