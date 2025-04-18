package com.example.coin_exchange;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.coin_exchange.Redis.RedisCacheHelper;
import com.example.coin_exchange.Redis.config.RedisConfig;
import com.example.coin_exchange.Redis.core.RedisHelper;

@SpringBootTest(classes = {
  RedisHelper.class,
  RedisCacheHelper.class,
  RedisConfig.class
})
@ActiveProfiles("test")
@EnableAutoConfiguration(exclude = {
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class,
    org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration.class
})
public class RedisHelperTest {

  @Autowired
  private RedisHelper redisHelper;

  @Autowired
  private RedisCacheHelper redisCacheHelper;

  private static final String TEST_KEY = "test:key";

  @Test
  public void testCacheHelper_setAndGet() {
    redisCacheHelper.cache(TEST_KEY, "testValue", Duration.ofMinutes(5));
    String result = redisCacheHelper.retrieve(TEST_KEY, String.class);
    assertEquals("testValue", result);
  }

  @Test
  public void testListHelper_lPushAndLRange() {
    redisHelper.del(TEST_KEY);
    List<String> values = Arrays.asList("item1", "item2", "item3");
    for (String val : values) {
      redisHelper.lPush(TEST_KEY, val);
    }
    List<String> result = redisHelper.lRange(TEST_KEY, 0, 10, String.class);
    assertEquals(3, result.size());
    assertTrue(result.contains("item1"));
    assertTrue(result.contains("item2"));
    assertTrue(result.contains("item3"));

  }

  @Test
  public void testSetHelper_sAddAndSMembers() {
    redisHelper.del(TEST_KEY);
    redisHelper.sAdd(TEST_KEY, "unique-item");
    Set<String> result = redisHelper.sMembers(TEST_KEY, String.class);
    assertTrue(result.contains("unique-item"));
  }
}