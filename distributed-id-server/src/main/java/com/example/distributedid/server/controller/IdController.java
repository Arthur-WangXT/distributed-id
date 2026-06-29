package com.example.distributedid.server.controller;

import com.example.distributedid.common.Result;
import com.example.distributedid.redis.RedisGenerator;
import com.example.distributedid.segment.SegmentGenerator;
import com.example.distributedid.snowflake.SnowflakeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/id")
@RequiredArgsConstructor
public class IdController {

    private final SnowflakeGenerator snowflakeGenerator;
    private final SegmentGenerator segmentGenerator;
    private final RedisGenerator redisGenerator;

    @GetMapping("/snowflake")
    public Result<Long> generateSnowflake() {
        return Result.success(snowflakeGenerator.generate());
    }

    @GetMapping("/snowflake/batch")
    public Result<List<Long>> generateSnowflakeBatch(@RequestParam(defaultValue = "10") int count) {
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ids.add(snowflakeGenerator.generate());
        }
        return Result.success(ids);
    }

    @GetMapping("/segment")
    public Result<Long> generateSegment() {
        return Result.success(segmentGenerator.generate());
    }

    @GetMapping("/segment/batch")
    public Result<List<Long>> generateSegmentBatch(@RequestParam(defaultValue = "10") int count) {
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ids.add(segmentGenerator.generate());
        }
        return Result.success(ids);
    }

    @GetMapping("/redis")
    public Result<Long> generateRedis() {
        return Result.success(redisGenerator.generate());
    }

    @GetMapping("/redis/batch")
    public Result<List<Long>> generateRedisBatch(@RequestParam(defaultValue = "10") int count) {
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ids.add(redisGenerator.generate());
        }
        return Result.success(ids);
    }

    @GetMapping("/uuid")
    public Result<String> generateUuid() {
        return Result.success(UUID.randomUUID().toString());
    }

    @GetMapping("/compare")
    public Result<Map<String, Object>> compareAll() {
        Map<String, Object> result = new HashMap<>();
        result.put("snowflake", snowflakeGenerator.generate());
        result.put("segment", segmentGenerator.generate());
        result.put("redis", redisGenerator.generate());
        result.put("uuid", UUID.randomUUID().toString());
        return Result.success(result);
    }

    @GetMapping("/snowflake/benchmark")
    public Result<Map<String, Object>> snowflakeBenchmark(@RequestParam(defaultValue = "10000") int count) throws InterruptedException {
        long start = System.currentTimeMillis();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            executor.execute(() -> {
                snowflakeGenerator.generate();
                latch.countDown();
            });
        }
        latch.await();
        long end = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("time", end - start);
        result.put("qps", count * 1000.0 / (end - start));
        executor.shutdown();
        return Result.success(result);
    }

}