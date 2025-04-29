package com.example.coin_exchange.redis.core;

import com.example.coin_exchange.redis.RedisSerializationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RedisHelper {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final int MAX_RETRY = 2;

    public <T> void set(String key, T value) {
        set(key, value, Duration.ZERO);
    }

    public <T> void set(String key, T value, Duration timeout) {
        try {
            String json = objectMapper.writeValueAsString(value);
            if (!timeout.isZero()) {
                redisTemplate.opsForValue().set(key, json, timeout);
            } else {
                redisTemplate.opsForValue().set(key, json);
            }
        } catch (JsonProcessingException e) {
            throw new RedisSerializationException("JSON serialize error", e);
        }
    }

    private <T> T getWithRetry(String key, Class<T> clazz, int retryCount) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) return null;
            return objectMapper.readValue(value.toString(), clazz);
        } catch (IOException e) {
            if (retryCount < MAX_RETRY) {
                log.warn("[RedisHelper] Retry due to deserialization error on key={} attempt={}", key, retryCount);
                return getWithRetry(key, clazz, retryCount + 1);
            }
            throw new RedisSerializationException("JSON deserialize error", e);
        }
    }

    public <T> T get(String key, Class<T> clazz) {
        return getWithRetry(key, clazz, 0);
    }

    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            log.error("Expire error: {}", key, e);
            return false;
        }
    }

    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("HasKey error: {}", key, e);
            return false;
        }
    }

    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(Arrays.asList(key));
            }
        }
    }

    public long incr(String key, long delta) {
        if (delta < 0)
            throw new RuntimeException("Delta must be positive");
        return redisTemplate.opsForValue().increment(key, delta);
    }

    public long decr(String key, long delta) {
        if (delta < 0)
            throw new RuntimeException("Delta must be positive");
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    public <HK> void hSet(String key, HK hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    public <HK> void hSet(String key, HK hashKey, Object value, Duration timeout) {
        redisTemplate.opsForHash().put(key, hashKey, value);
        if (!timeout.isZero()) {
            redisTemplate.expire(key, timeout);
        }
    }

    public <T> Map<String, T> hGetAll(String key, Class<T> clazz) {
        try {
            Map<Object, Object> hashEntries = redisTemplate.opsForHash().entries(key);
            Map<String, T> result = new LinkedHashMap<>();
            for (Map.Entry<Object, Object> entry : hashEntries.entrySet()) {
                String json = objectMapper.writeValueAsString(entry.getValue());
                result.put((String) entry.getKey(), objectMapper.readValue(json, clazz));
            }
            return result;
        } catch (Exception e) {
            log.error("HGETALL error: {}", key, e);
            return Collections.emptyMap();
        }
    }

    public boolean sAdd(String key, Object... values) {
        try {
            Object[] jsonValues = Arrays.stream(values)
                    .map(val -> {
                        try {
                            return objectMapper.writeValueAsString(val);
                        } catch (JsonProcessingException e) {
                            throw new RedisSerializationException("JSON serialize error in sAdd", e);
                        }
                    }).toArray();
            return redisTemplate.opsForSet().add(key, jsonValues) > 0;
        } catch (Exception e) {
            log.error("SADD error: {}", key, e);
            return false;
        }
    }

    public boolean sAdd(String key, Duration timeout, Object... values) {
        try {
            Object[] jsonValues = Arrays.stream(values)
                    .map(val -> {
                        try {
                            return objectMapper.writeValueAsString(val);
                        } catch (JsonProcessingException e) {
                            throw new RedisSerializationException("JSON serialize error in sAdd", e);
                        }
                    }).toArray();
            boolean added = redisTemplate.opsForSet().add(key, jsonValues) > 0;
            if (added && !timeout.isZero()) {
                redisTemplate.expire(key, timeout);
            }
            return added;
        } catch (Exception e) {
            log.error("SADD error: {}", key, e);
            return false;
        }
    }

    public <T> Set<T> sMembers(String key, Class<T> clazz) {
        try {
            Set<Object> raw = redisTemplate.opsForSet().members(key);
            Set<T> result = new HashSet<>();
            for (Object item : raw) {
                result.add(objectMapper.readValue(item.toString(), clazz));
            }
            return result;
        } catch (Exception e) {
            log.error("SMEMBERS error: {}", key, e);
            return Collections.emptySet();
        }
    }

    public boolean lPush(String key, Object value) {
        try {
            String json = objectMapper.writeValueAsString(value);
            redisTemplate.opsForList().leftPush(key, json);
            return true;
        } catch (Exception e) {
            log.error("LPUSH error: {}", key, e);
            return false;
        }
    }

    public boolean lPush(String key, Object value, Duration timeout) {
        try {
            String json = objectMapper.writeValueAsString(value);
            redisTemplate.opsForList().leftPush(key, json);
            if (!timeout.isZero()) {
                redisTemplate.expire(key, timeout);
            }
            return true;
        } catch (Exception e) {
            log.error("LPUSH with timeout error: {}", key, e);
            return false;
        }
    }

    public <T> List<T> lRange(String key, long start, long end, Class<T> clazz) {
        try {
            List<Object> range = redisTemplate.opsForList().range(key, start, end);
            return range.stream().map(obj -> {
                try {
                    return objectMapper.readValue(obj.toString(), clazz);
                } catch (Exception e) {
                    log.error("LRANGE deserialization error", e);
                    return null;
                }
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("LRANGE error: {}", key, e);
            return Collections.emptyList();
        }
    }

    public boolean zAdd(String key, Object value, double score) {
        try {
            return redisTemplate.opsForZSet().add(key, value, score);
        } catch (Exception e) {
            log.error("ZADD error: {}", key, e);
            return false;
        }
    }

    public boolean zAdd(String key, Object value, double score, Duration timeout) {
        try {
            boolean added = redisTemplate.opsForZSet().add(key, value, score);
            if (added && !timeout.isZero()) {
                redisTemplate.expire(key, timeout);
            }
            return added;
        } catch (Exception e) {
            log.error("ZADD error: {}", key, e);
            return false;
        }
    }

    public Set<Object> zRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().range(key, start, end);
        } catch (Exception e) {
            log.error("ZRANGE error: {}", key, e);
            return Collections.emptySet();
        }
    }

    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
    
    public Duration getTTL(String key) {
        Long seconds = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        return seconds != null && seconds > 0 ? Duration.ofSeconds(seconds) : Duration.ZERO;
    }

    public <T> List<T> getRecentList(String key, int limit, Class<T> clazz) {
        try {
            List<Object> range = redisTemplate.opsForList().range(key, 0, limit - 1);
            return range.stream().map(obj -> {
                try {
                    return objectMapper.readValue(obj.toString(), clazz);
                } catch (Exception e) {
                    log.error("LRANGE deserialization error", e);
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("getRecentList error: {}", key, e);
            return Collections.emptyList();
        }
    }

    public Set<Object> getZSetByScoreRange(String key, double minScore, double maxScore) {
        try {
            return redisTemplate.opsForZSet().rangeByScore(key, minScore, maxScore);
        } catch (Exception e) {
            log.error("getZSetByScoreRange error: {}", key, e);
            return Collections.emptySet();
        }
    }

    public void alertIfKeyMissing(String key) {
        if (!hasKey(key)) {
            log.warn("[Redis ALERT] Key not found in Redis: {}", key);
        }
    }

    public <T> T getOrSetWithAlert(String key, Supplier<T> supplier, Class<T> clazz, Duration ttl) {
        T cached = get(key, clazz);
        if (cached != null) return cached;

        alertIfKeyMissing(key);
        T fresh = supplier.get();
        if (fresh != null) {
            set(key, fresh, ttl);
        }
        return fresh;
    }
}
