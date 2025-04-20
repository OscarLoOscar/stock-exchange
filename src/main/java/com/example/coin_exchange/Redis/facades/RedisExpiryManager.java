package com.example.coin_exchange.redis.facades;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RedisExpiryManager {

  @Value("${redis.default-expiry-seconds:0}")
  private long defaultExpirySeconds;

  public Duration resolve(Duration input) {
    return input == null || input.isZero()
        ? Duration.ofSeconds(defaultExpirySeconds)
        : input;
  }

  public Duration getDefaultExpiry() {
    return Duration.ofSeconds(defaultExpirySeconds);
  }

  public Duration resolve(String key, Duration explicitTtl) {
    if (explicitTtl != null && !explicitTtl.isZero())
      return explicitTtl;

    // 範例邏輯（可改從 Map 或 Enum）
    if (key.startsWith("user:"))
      return Duration.ofMinutes(30);
    if (key.startsWith("config:"))
      return Duration.ofHours(1);
    return Duration.ofSeconds(defaultExpirySeconds);
  }

}
