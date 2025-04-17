package com.example.coin_exchange.annotation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class SymbolValidator implements ConstraintValidator<SymbolCheck, String> {
  private final ValidSymbolProperties symbolProperties;

  @Autowired
  public SymbolValidator(ValidSymbolProperties symbolProperties) {
    this.symbolProperties = symbolProperties;
  }

  @Override
  public boolean isValid(String symbol, ConstraintValidatorContext context) {
    if (symbol == null || symbol.trim().isEmpty())
      return false;

    List<String> validSymbols = symbolProperties.getList();
    return validSymbols.contains(symbol.trim().toUpperCase());
  }
}
