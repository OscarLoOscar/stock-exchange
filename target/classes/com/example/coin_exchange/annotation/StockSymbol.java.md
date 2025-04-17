package com.example.coin_exchange.annotation;

import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StockSymbol {

  private String symbol;
  private String stockSymbols; // Comma-separated list of valid stock symbols

  public List<String> getStockIds() {
    return Arrays.asList(stockSymbols.split(",")); // Converts to a list
  }
}