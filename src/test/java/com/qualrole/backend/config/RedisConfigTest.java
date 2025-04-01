package com.qualrole.backend.config;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

@SpringBootTest
class RedisConfigTest {

    @Autowired
    private RedisConfig redisConfig;

    @Test
    void testRedisTemplate() {
        RedisConnectionFactory redisConnectionFactory = mock(RedisConnectionFactory.class);

        RedisTemplate<String, String> redisTemplate = redisConfig.redisTemplate(redisConnectionFactory);

        assertNotNull(redisTemplate);
        assertNotNull(redisTemplate.getConnectionFactory());
        Mockito.verify(redisConnectionFactory, Mockito.times(1));
    }
}