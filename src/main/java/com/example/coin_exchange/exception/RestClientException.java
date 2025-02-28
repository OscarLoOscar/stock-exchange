package com.example.coin_exchange.exception;

import com.example.coin_exchange.exception.exceptionEnum.Syscode;
import lombok.Getter;

@Getter
public class RestClientException extends BussinessException {
  private Syscode syscode;

  public RestClientException(Syscode syscode) {
    super(syscode);
    this.syscode = syscode;
  }
}