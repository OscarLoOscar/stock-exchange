package com.example.coin_exchange.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResp<T> {
  private String syscode;
  private String message;
  private T data;

  public static <T> ApiResponseBuilder<T> builder(){
    return new ApiResponseBuilder<>();
  }

}
