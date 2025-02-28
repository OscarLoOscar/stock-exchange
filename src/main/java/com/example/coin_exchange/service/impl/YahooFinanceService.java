package com.example.coin_exchange.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.example.coin_exchange.config.YahooFinanceConfig;
import com.example.coin_exchange.cookie.FinanceService;
import com.example.coin_exchange.entity.StockQuoteYahoo;
import com.example.coin_exchange.model.apiResponse.YahooApiResponse;
import com.example.coin_exchange.repository.StockQuoteYahooRepo;
import com.example.coin_exchange.service.YahooFinanceServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class YahooFinanceService implements YahooFinanceServiceImpl {

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private YahooFinanceConfig config;

  @Autowired
  private StockQuoteYahooRepo stockQuoteYahooRepo;

  @Autowired
  private FinanceService financeService;


  @Override
  public StockQuoteYahoo saveInDB(StockQuoteYahoo stockQuoteYahoo) {
    return stockQuoteYahooRepo.save(stockQuoteYahoo);
  }

  @Override
  public List<YahooApiResponse> getLatest5MinData(String symbol) {
    return stockQuoteYahooRepo.findTop5BySymbolOrderByTimestamp(symbol);
  }

  @Override
  public YahooApiResponse callYahooFinanceAPI(String symbol)
      throws JsonProcessingException {
    String url = config.yahooFinanceUriBuilder(symbol).toUriString();
    HttpHeaders headers = new HttpHeaders();
    String cookie = financeService.getCookie();
    headers.set("Cookie", cookie);

    HttpEntity<YahooApiResponse> entity = new HttpEntity<>(headers);
    ResponseEntity<YahooApiResponse> response = restTemplate.exchange(url,
        HttpMethod.GET, entity, YahooApiResponse.class);
    return response.getBody();
  }

}
