package com.example.coin_exchange.annotation;


import java.util.Objects;
import java.util.List;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SymbolValidator implements ConstraintValidator<SymbolCheck,StockSymbol>{
  
  
  @Override
  public boolean isValid(StockSymbol value, ConstraintValidatorContext context) {
    if (Objects.isNull(value) || Objects.isNull(value.getStockIds())) {
      return false; 
    }

    List<String> stockIds = value.getStockIds();
    return stockIds.contains(value.getSymbol()); 
  }
}
