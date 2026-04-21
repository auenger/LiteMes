package com.litemes.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litemes.domain.entity.WorkCenter;
import org.apache.ibatis.annotations.Mapper;

/**
 * MyBatis-Plus Mapper for WorkCenter entity.
 * Provides standard CRUD operations via BaseMapper.
 */
@Mapper
public interface WorkCenterMapper extends BaseMapper<WorkCenter> {
}
