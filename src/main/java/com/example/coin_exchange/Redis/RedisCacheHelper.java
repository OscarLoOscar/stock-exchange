package com.example.coin_exchange.Redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.coin_exchange.Redis.core.RedisHelper;

import java.time.Duration;

@Component
public class RedisCacheHelper {

  @Autowired
  private RedisHelper redisHelper;

  public <T> void cache(String key, T value, Duration timeout) {
    redisHelper.set(key, value, timeout);
  }

  public <T> T retrieve(String key, Class<T> clazz) {
    return redisHelper.get(key, clazz);
  }
}