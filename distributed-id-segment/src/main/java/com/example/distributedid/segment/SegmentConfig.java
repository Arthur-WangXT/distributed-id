package com.example.distributedid.segment;

import com.example.distributedid.segment.mapper.SegmentMapper;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "segment")
public class SegmentConfig {

    private String bizType = "default";
    private int step = 1000;

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    @Bean
    public SegmentGenerator segmentGenerator(SegmentMapper segmentMapper) {
        return new SegmentGenerator(segmentMapper, bizType, step);
    }

}