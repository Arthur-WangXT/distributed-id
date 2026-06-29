package com.example.distributedid.segment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.distributedid.segment.entity.SegmentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SegmentMapper extends BaseMapper<SegmentEntity> {

    @Update("UPDATE segment_id_generator SET max_id = max_id + #{step} WHERE biz_type = #{bizType}")
    int updateMaxId(@Param("bizType") String bizType, @Param("step") Integer step);

}