package com.example.coin_exchange.exception;

import com.example.coin_exchange.exception.exceptionEnum.Syscode;
import lombok.Getter;

@Getter
public class BussinessException extends Exception{
  private Syscode syscode;

  public BussinessException(Syscode syscode){
    super(syscode.getMessage());
    this.syscode=syscode;
  }
}
