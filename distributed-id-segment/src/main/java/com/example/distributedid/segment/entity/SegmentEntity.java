package com.example.distributedid.segment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("segment_id_generator")
public class SegmentEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String bizType;

    private Long maxId;

    private Integer step;

    private String updateTime;

}