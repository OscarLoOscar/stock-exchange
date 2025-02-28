package com.example.coin_exchange.service.impl;

import java.util.List;
import com.example.coin_exchange.model.CandleStick;
import com.example.coin_exchange.model.EMAData;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface StockAnalysisServiceImpl {
  List<EMAData> EMA(String symbol, String period) throws IllegalAccessException;

  public List<CandleStick> convertToOtherTimeFrameCandleStick(String symbol,
      String period) throws JsonProcessingException;
}
