package com.example.coin_exchange.infra;

import lombok.Getter;

@Getter
public enum UriScheme {
  HTTP("http"),//
  HTTPS("https")//
  ;

  private String protocol;

  UriScheme(String protocol){
    this.protocol=protocol;
  }
}
