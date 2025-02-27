package com.example.coin_exchange.exception.exceptionEnum;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.core.JsonProcessingException;

@RestControllerAdvice
public class GlobalException{
  
  @ExceptionHandler(value = JsonProcessingException.class)
  @ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
  public ApiResp<Void> bootcampExceptionHandler(JsonProcessingException e){
    return ApiResp.<Void>builder()//
    .syscode(Syscode.REDIS_ERROR.getSyscode())//
    .message(e.getMessage())//
    .build();
  }
}