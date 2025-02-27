package com.example.coin_exchange.Redis;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

@EnableCaching
@Configuration
public class RedisConfiguration {
  public KeyGenerator keyGenerator() {
    return new KeyGenerator() {
      @Override
      public Object generate(Object o, Method method, Object... objects) {
        StringBuilder sb = new StringBuilder();
        sb.append(o.getClass().getName());
        sb.append(method.getName());
        for (Object obj : objects) {
          sb.append(obj.toString());
        }
        System.out.println("调用Redis缓存Key : " + sb.toString());
        return sb.toString();
      }
    };
  }
  @Bean
  public CacheManager cacheManager(
      RedisConnectionFactory redisConnectionFactory) {
    return new RedisCacheManager(
        RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory),
        this.getRedisCacheConfigurationWithTtl(5), // 默认策略，未配置的 key 会使用这个
        this.getRedisCacheConfigurationMap() // 指定 key 策略
    );
  }

  // 单独指定key的对应的缓存过期时间
  private Map<String, RedisCacheConfiguration> getRedisCacheConfigurationMap() {
    Map<String, RedisCacheConfiguration> redisCacheConfigurationMap =
        new HashMap<>();
    redisCacheConfigurationMap.put("getArticleList",
        this.getRedisCacheConfigurationWithTtl(10));
    redisCacheConfigurationMap.put("getArticleStarers",
        this.getRedisCacheConfigurationWithTtl(10));

    return redisCacheConfigurationMap;
  }

  private RedisCacheConfiguration getRedisCacheConfigurationWithTtl(
      Integer minutes) {
    Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer =
        new Jackson2JsonRedisSerializer<>(Object.class);
    ObjectMapper om = new ObjectMapper();
    om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    // om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
    // jackson2JsonRedisSerializer.setObjectMapper(om);

    RedisCacheConfiguration redisCacheConfiguration =
        RedisCacheConfiguration.defaultCacheConfig();
    redisCacheConfiguration = redisCacheConfiguration
        .serializeValuesWith(RedisSerializationContext.SerializationPair
            .fromSerializer(jackson2JsonRedisSerializer))
        .entryTtl(Duration.ofMinutes(minutes));

    return redisCacheConfiguration;
  }
}
