package com.dhaliwal.notemind.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@Profile("redis-test")
public class RedisTestConfig {

    @Bean
    CommandLineRunner testRedis(StringRedisTemplate redis) {
        return args -> {
            redis.opsForValue().set("notemind:test", "connected");
            System.out.println(
                    redis.opsForValue().get("notemind:test")
            );
        };
    }
}
