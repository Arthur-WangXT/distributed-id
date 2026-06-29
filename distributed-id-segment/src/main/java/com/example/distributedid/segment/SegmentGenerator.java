package com.example.distributedid.segment;

import com.example.distributedid.common.IdGenerator;
import com.example.distributedid.segment.entity.SegmentEntity;
import com.example.distributedid.segment.mapper.SegmentMapper;

import java.util.concurrent.atomic.AtomicLong;

public class SegmentGenerator implements IdGenerator {

    private final SegmentMapper segmentMapper;
    private final String bizType;
    private int step;

    private volatile AtomicLong currentId;
    private volatile long maxId;
    private volatile boolean isReady;

    private volatile AtomicLong currentIdBuffer;
    private volatile long maxIdBuffer;
    private volatile boolean isBufferReady;

    public SegmentGenerator(SegmentMapper segmentMapper, String bizType, int step) {
        this.segmentMapper = segmentMapper;
        this.bizType = bizType;
        this.step = step;
        this.currentId = new AtomicLong(0);
        this.maxId = 0;
        this.isReady = false;
        this.currentIdBuffer = new AtomicLong(0);
        this.maxIdBuffer = 0;
        this.isBufferReady = false;
        initSegment();
    }

    private synchronized void initSegment() {
        SegmentEntity entity = segmentMapper.selectById(bizType);
        if (entity == null) {
            entity = new SegmentEntity();
            entity.setBizType(bizType);
            entity.setMaxId(0L);
            entity.setStep(step);
            segmentMapper.insert(entity);
        }
        this.step = entity.getStep();
        updateSegment();
    }

    private synchronized void updateSegment() {
        segmentMapper.updateMaxId(bizType, step);
        SegmentEntity entity = segmentMapper.selectById(bizType);
        if (isReady) {
            this.currentIdBuffer = new AtomicLong(entity.getMaxId() - step + 1);
            this.maxIdBuffer = entity.getMaxId();
            this.isBufferReady = true;
        } else {
            this.currentId = new AtomicLong(entity.getMaxId() - step + 1);
            this.maxId = entity.getMaxId();
            this.isReady = true;
        }
    }

    @Override
    public long generate() {
        if (!isReady) {
            throw new RuntimeException("Segment not initialized for bizType: " + bizType);
        }
        long id = currentId.incrementAndGet();
        if (id <= maxId) {
            if (id > maxId - step / 2 && !isBufferReady) {
                new Thread(() -> updateSegment()).start();
            }
            return id;
        }
        if (isBufferReady) {
            currentId = currentIdBuffer;
            maxId = maxIdBuffer;
            isReady = true;
            isBufferReady = false;
            currentIdBuffer = new AtomicLong(0);
            maxIdBuffer = 0;
            new Thread(() -> updateSegment()).start();
            return currentId.incrementAndGet();
        }
        synchronized (this) {
            if (currentId.get() > maxId) {
                updateSegment();
                currentId = new AtomicLong(maxId - step + 1);
            }
            return currentId.incrementAndGet();
        }
    }

}