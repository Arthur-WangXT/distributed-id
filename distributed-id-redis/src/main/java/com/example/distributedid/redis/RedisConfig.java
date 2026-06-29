package com.example.distributedid.redis;

import org.redisson.api.RedissonClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "redis.id")
public class RedisConfig {

    private String key = "distributed:id:generator";
    private long step = 1000;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getStep() {
        return step;
    }

    public void setStep(long step) {
        this.step = step;
    }

    @Bean
    public RedisGenerator redisGenerator(RedissonClient redissonClient) {
        return new RedisGenerator(redissonClient, key, step);
    }

}