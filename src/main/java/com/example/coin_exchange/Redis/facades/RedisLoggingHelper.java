package com.example.coin_exchange.redis.facades;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisLoggingHelper {

  public void logInfo(String action, String key) {
    log.info("Redis action: {}, key={}", action, key);
  }

  public void logDebug(String message) {
    log.debug(message);
  }

  public void logError(String action, String key, Exception e) {
    log.error("Redis error during {}: key={}, cause={}", action, key, e.toString());
  }

  public void logTrace(String message) {
    log.trace(message);
  }
}
