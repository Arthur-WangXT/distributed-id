package com.example.distributedid.redis;

import com.example.distributedid.common.IdGenerator;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;

public class RedisGenerator implements IdGenerator {

    private final RedissonClient redissonClient;
    private final String key;
    private final long step;

    private final ThreadLocal<Long> currentBatchEnd = new ThreadLocal<>();
    private final ThreadLocal<java.util.concurrent.atomic.AtomicLong> currentId = new ThreadLocal<>();

    public RedisGenerator(RedissonClient redissonClient, String key, long step) {
        this.redissonClient = redissonClient;
        this.key = key;
        this.step = step;
    }

    @Override
    public long generate() {
        if (currentId.get() == null || currentId.get().get() >= currentBatchEnd.get()) {
            fetchBatch();
        }
        return currentId.get().getAndIncrement();
    }

    private void fetchBatch() {
        RAtomicLong atomicLong = redissonClient.getAtomicLong(key);
        long batchEnd = atomicLong.addAndGet(step);
        long batchStart = batchEnd - step + 1;
        currentBatchEnd.set(batchEnd);
        currentId.set(new java.util.concurrent.atomic.AtomicLong(batchStart));
    }

}