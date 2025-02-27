package com.example.coin_exchange.exception.exceptionEnum;

import lombok.Getter;

@Getter
public enum Syscode {
  OK("000000", "OK"), //
  INVALID_INPUT("9", "Invalid Input"), //
  INVALID_OPERATION("10", "Invalid operation"), //
  API_ERROR("100", "Api Error"), //
  YAHOO_SERVICE_UNAVAILABLE("90000", "Yahoo Service unvaliable"), //
  NULL_POINTER("11", "Null Pointer Exception"), //
  REDIS_ERROR("12", "Redis Error"), //
  REDIS_CONVERT_DATA_ERROR("12", "Redis Mapper Error"), //
  UNAUTHORIZED("14", "Unauthorized"),//
  ;

  private String syscode;
  private String message;

  private Syscode(String syscode, String message) {
    this.syscode = syscode;
    this.message = message;
  }

  public static Syscode fromCode(Syscode syscode) {
    for (Syscode c : Syscode.values()) {
      if (c.getSyscode().equals(syscode.getSyscode()))
        return c;
    }
    return null;
  }
}
