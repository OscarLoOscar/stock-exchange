package com.example.coin_exchange.Redis;

import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class CustomJackson2JsonRedisSerializer <T>  implements RedisSerializer<T>{
  
  private final Jackson2JsonRedisSerializer<T> delegate;

  public CustomJackson2JsonRedisSerializer(Class<T> type) {
      delegate = new Jackson2JsonRedisSerializer<>(type);
  }

  @Override
  public byte[] serialize(T t) throws SerializationException {
      return delegate.serialize(t);
  }

  @Override
  public T deserialize(byte[] bytes) throws SerializationException {
      return delegate.deserialize(bytes);
  }
}
