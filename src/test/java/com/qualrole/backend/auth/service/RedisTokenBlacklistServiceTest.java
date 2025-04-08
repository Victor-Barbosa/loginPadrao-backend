package com.qualrole.backend.auth.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringJUnitConfig
class RedisTokenBlacklistServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @InjectMocks
    private RedisTokenBlacklistService redisTokenBlacklistServiceMock;

    @Test
    void isTokenBlacklisted_ShouldReturnTrue_WhenTokenExistsInRedis() {
        String token = "blacklistedToken";
        when(redisTemplate.hasKey(token)).thenReturn(true);

        boolean result = redisTokenBlacklistServiceMock.isTokenBlacklisted(token);

        assertThat(result).isTrue();
        Mockito.verify(redisTemplate).hasKey(token);
    }

    @Test
    void isTokenBlacklisted_ShouldReturnFalse_WhenTokenDoesNotExistInRedis() {
        String token = "nonExistentToken";
        when(redisTemplate.hasKey(token)).thenReturn(false);

        boolean result = redisTokenBlacklistServiceMock.isTokenBlacklisted(token);

        assertThat(result).isFalse();
        Mockito.verify(redisTemplate).hasKey(token);
    }
}