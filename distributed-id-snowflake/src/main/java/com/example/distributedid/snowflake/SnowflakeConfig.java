package com.example.distributedid.snowflake;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "snowflake")
public class SnowflakeConfig {

    private long workerId = 0;

    public long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(long workerId) {
        this.workerId = workerId;
    }

    @Bean
    public SnowflakeGenerator snowflakeGenerator() {
        return new SnowflakeGenerator(workerId);
    }

}