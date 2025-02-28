package com.example.coin_exchange.controller.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.coin_exchange.controller.StockAnalysisControllerImpl;
import com.example.coin_exchange.model.CandleStick;
import com.example.coin_exchange.model.EMAData;
import com.example.coin_exchange.service.impl.StockAnalysisService;
import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
@RequestMapping("/ema")
public class StockAnalysisController implements StockAnalysisControllerImpl {
  @Autowired
  private StockAnalysisService stockAnalysisService;

  @Override
  public List<EMAData> EMA(String symbol, String period)
      throws IllegalAccessException {
    return stockAnalysisService.EMA(symbol, period);
  }

  @Override
  public List<CandleStick> convertToOtherTimeFrameCandleStick(String symbol,
      String period) throws JsonProcessingException {
    return stockAnalysisService.convertToOtherTimeFrameCandleStick(symbol,
        period);
  }
}
