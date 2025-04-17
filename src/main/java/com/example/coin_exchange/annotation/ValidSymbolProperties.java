package com.example.coin_exchange.annotation;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "yahoo.finance.api")
public class ValidSymbolProperties {
  private List<String> list;
}
