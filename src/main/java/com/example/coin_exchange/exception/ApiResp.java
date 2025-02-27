package com.example.coin_exchange.exception;

import com.example.coin_exchange.exception.exceptionEnum.Syscode;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResp<T> {
  private String syscode;
  private String message;
  private T data;

  public static <T> ApiResponseBuilder<T> builder(){
    return new ApiResponseBuilder<>();
  }

  @Getter
  public static class ApiResponseBuilder<T> {
    private String syscode;
    private String message;
    private T data;

    public ApiResponseBuilder<T>status(Syscode syscode){
      this.syscode=syscode.getSyscode();
      this.message=syscode.getMessage();
      return this;
    }
  }

}
