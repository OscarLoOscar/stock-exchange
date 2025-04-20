package com.example.coin_exchange.redis.facades;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.coin_exchange.redis.core.RedisHelper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RedisCacheHelper {

    @Autowired
    private RedisHelper redisHelper;

    @Autowired
    private RedisExpiryManager expiryManager;

    public <T> void cache(String key, T value, Duration ttl) {
        log.debug("[CACHE] Set key: {} with TTL: {}", key, ttl);
        redisHelper.set(key, value, ttl);
    }

    public <HK> void cacheHash(String key, HK hashKey, Object value, Duration ttl) {
        log.debug("[CACHE:HASH] key={}, hashKey={}, TTL={} ", key, hashKey, ttl);
        redisHelper.hSet(key, hashKey, value, ttl);
    }

    public boolean cacheList(String key, Object value, Duration ttl) {
        log.debug("[CACHE:LIST] key={}, TTL={} ", key, ttl);
        return redisHelper.lPush(key, value, ttl);
    }

    public boolean cacheSet(String key, Duration ttl, Object... values) {
        log.debug("[CACHE:SET] key={}, TTL={}, values={} ", key, ttl, values);
        return redisHelper.sAdd(key, ttl, values);
    }

    public boolean cacheZSet(String key, Object value, double score, Duration ttl) {
        log.debug("[CACHE:ZSET] key={}, score={}, TTL={} ", key, score, ttl);
        return redisHelper.zAdd(key, value, score, ttl);
    }

    public <T> T retrieve(String key, Class<T> clazz) {
        log.debug("[GET] key={} ", key);
        return redisHelper.get(key, clazz);
    }

    public <T> Map<String, T> retrieveHashAll(String key, Class<T> clazz) {
        return redisHelper.hGetAll(key, clazz);
    }

    public <T> List<T> retrieveList(String key, long start, long end, Class<T> clazz) {
        return redisHelper.lRange(key, start, end, clazz);
    }

    public <T> Set<T> retrieveSet(String key, Class<T> clazz) {
        return redisHelper.sMembers(key, clazz);
    }

    public Set<Object> retrieveZSet(String key, long start, long end) {
        return redisHelper.zRange(key, start, end);
    }

    public void delete(String... keys) {
        log.debug("[DEL] keys={}", (Object) keys);
        redisHelper.del(keys);
    }

    public boolean exists(String key) {
        return redisHelper.hasKey(key);
    }

    public <T> T getOrSet(String key, Supplier<T> supplier, Class<T> clazz, Duration ttl) {
        T cached = redisHelper.get(key, clazz);
        if (cached != null)
            return cached;

        T fresh = supplier.get();
        if (fresh != null) {
            redisHelper.set(key, fresh, expiryManager.resolve(ttl));
        } else {
            log.warn("Redis getOrSet fallback supplier returned null for key={}", key);
        }
        return fresh;
    }
}
