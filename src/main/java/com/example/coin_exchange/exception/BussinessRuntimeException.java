package com.example.coin_exchange.exception;

import com.example.coin_exchange.exception.exceptionEnum.Syscode;
import lombok.Getter;

@Getter
public class BussinessRuntimeException extends RuntimeException{
  private Syscode syscode;
  public BussinessRuntimeException(Syscode syscode){
    super(syscode.getMessage());
    this.syscode=syscode;
  }
}
