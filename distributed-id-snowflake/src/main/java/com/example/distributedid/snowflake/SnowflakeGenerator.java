package com.example.distributedid.snowflake;

import com.example.distributedid.common.IdGenerator;

public class SnowflakeGenerator implements IdGenerator {

    private static final long START_TIMESTAMP = 1704067200000L;
    private static final long WORKER_ID_BITS = 10L;
    private static final long SEQUENCE_BITS = 12L;
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

    private final long workerId;
    private volatile long lastTimestamp = -1L;
    private volatile long sequence = 0L;

    public SnowflakeGenerator(long workerId) {
        if (workerId < 0 || workerId > MAX_WORKER_ID) {
            throw new IllegalArgumentException("workerId must be between 0 and " + MAX_WORKER_ID);
        }
        this.workerId = workerId;
    }

    @Override
    public synchronized long generate() {
        long currentTimestamp = getCurrentTimestamp();
        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id for " + (lastTimestamp - currentTimestamp) + " milliseconds");
        }
        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                currentTimestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = currentTimestamp;
        return ((currentTimestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT) | (workerId << WORKER_ID_SHIFT) | sequence;
    }

    private long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    private long waitNextMillis(long lastTimestamp) {
        long timestamp = getCurrentTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = getCurrentTimestamp();
        }
        return timestamp;
    }

}