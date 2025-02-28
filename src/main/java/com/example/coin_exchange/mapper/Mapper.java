package com.example.coin_exchange.mapper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.coin_exchange.entity.StockQuoteYahoo;
import com.example.coin_exchange.model.CandleStick;
import com.example.coin_exchange.model.apiResponse.StockQuoteYahooRedis;
import com.example.coin_exchange.model.apiResponse.YahooApiResponse;

@Component
public class Mapper {
  @Autowired
  private ModelMapper modelMapper;

  public CandleStick mapToCandleStick(
      StockQuoteYahooRedis.QuoteResult.RedisData redisData) {
    return CandleStick.builder()//
        .open(redisData.getRegularMarketOpen())//
        .high(redisData.getRegularMarketDayHigh())//
        .low(redisData.getRegularMarketDayLow())//
        .close(redisData.getRegularMarketPreviousClose())//
        .time(Instant.ofEpochSecond(redisData.getRegularMarketUnix())//
            .atZone(ZoneId.systemDefault())//
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))//
            .toString())//
        .build();
  }

  public CandleStick mapToCandleStick(StockQuoteYahoo stockQuoteYahoo) {
    return CandleStick.builder().open(stockQuoteYahoo.getOpen())//
        .high(stockQuoteYahoo.getHigh())//
        .low(stockQuoteYahoo.getLow())//
        .close(stockQuoteYahoo.getClose())//
        .time(Instant.ofEpochSecond(stockQuoteYahoo.getTimestamp())//
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            .toString())
        .build();
  }

  public StockQuoteYahoo map(YahooApiResponse yahooApiResponse) {
    return StockQuoteYahoo.builder()//
        .symbol(
            yahooApiResponse.getQuoteResponse().getResult().get(0).getSymbol())//
        .timestamp(yahooApiResponse.getQuoteResponse().getResult().get(0)
            .getRegularMarketTime())//
        .high(yahooApiResponse.getQuoteResponse().getResult().get(0)
            .getRegularMarketDayHigh())//
        .low(yahooApiResponse.getQuoteResponse().getResult().get(0)
            .getRegularMarketDayLow())//
        .open(yahooApiResponse.getQuoteResponse().getResult().get(0)
            .getRegularMarketOpen())//
        .close(yahooApiResponse.getQuoteResponse().getResult().get(0)
            .getRegularMarketPreviousClose())//

        .build();
  }

  public StockQuoteYahooRedis.QuoteResult mapToQuoteResult(
      YahooApiResponse yahooApiResponse) {
    YahooApiResponse.QuoteResponseDetail.Result result =
        yahooApiResponse.getQuoteResponse().getResult().get(0);
    List<StockQuoteYahooRedis.QuoteResult.RedisData> data =
        yahooApiResponse.getQuoteResponse().getResult().stream()//
            .map(res -> StockQuoteYahooRedis.QuoteResult.RedisData.builder()//
                .symbol(res.getSymbol())//
                .regularMarketUnix(res.getRegularMarketTime())//
                .regularMarketPrice(res.getRegularMarketPrice())//
                .regularMarketChange(res.getRegularMarketChange())//
                .bid(res.getBid())//
                .ask(res.getAsk())//
                .bidSize(res.getBidSize())//
                .askSize(res.getAskSize())//
                .regularMarketDayHigh(res.getRegularMarketDayHigh())//
                .regularMarketDayLow(res.getRegularMarketDayLow())//
                .regularMarketOpen(res.getRegularMarketOpen())//
                .regularMarketPreviousClose(res.getRegularMarketPreviousClose())//
                .build())//
            .collect(Collectors.toList());
    return StockQuoteYahooRedis.QuoteResult.builder()//
        .regularMarketTime(String.valueOf(result.getRegularMarketTime()))//
        .data(data)//
        .build();
  }

  public StockQuoteYahooRedis.QuoteResult.RedisData toRedisData(
      StockQuoteYahoo stockQuoteYahoo) {
    if (stockQuoteYahoo == null) {
      return null;
    }

    return StockQuoteYahooRedis.QuoteResult.RedisData.builder()
        .symbol(stockQuoteYahoo.getSymbol())
        .regularMarketUnix(stockQuoteYahoo.getTimestamp())
        .regularMarketOpen(stockQuoteYahoo.getOpen())
        .regularMarketDayHigh(stockQuoteYahoo.getHigh())
        .regularMarketDayLow(stockQuoteYahoo.getLow())
        .regularMarketPreviousClose(stockQuoteYahoo.getClose()).build();
  }

  public StockQuoteYahooRedis.QuoteResult.RedisData map(
      StockQuoteYahoo stockQuoteYahoo) {
    return modelMapper.map(stockQuoteYahoo,
        StockQuoteYahooRedis.QuoteResult.RedisData.class);
  }
}
