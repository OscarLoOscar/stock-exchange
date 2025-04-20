package com.example.coin_exchange.redis;

public class RedisSerializationException extends RuntimeException {
    public RedisSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
