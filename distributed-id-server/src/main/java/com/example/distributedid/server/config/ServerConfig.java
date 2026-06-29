package com.example.distributedid.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.example.distributedid.redis.RedisConfig;
import com.example.distributedid.segment.SegmentConfig;
import com.example.distributedid.snowflake.SnowflakeConfig;

@Configuration
@Import({SnowflakeConfig.class, SegmentConfig.class, RedisConfig.class})
public class ServerConfig {

}