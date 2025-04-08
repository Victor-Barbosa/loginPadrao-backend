package com.qualrole.backend.auth.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisTokenBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisTokenBlacklistService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void addTokenToBlacklist(String token, long expirationInMillis) {
        long expirationInSeconds = expirationInMillis / 1000;
        redisTemplate.opsForValue().set(token, "revoked", expirationInSeconds, TimeUnit.SECONDS);
    }

    public boolean isTokenBlacklisted(String token) {
        return redisTemplate.hasKey(token);
    }
}