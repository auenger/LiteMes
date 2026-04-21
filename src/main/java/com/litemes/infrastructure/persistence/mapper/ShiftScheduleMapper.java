package com.litemes.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litemes.domain.entity.ShiftSchedule;
import org.apache.ibatis.annotations.Mapper;

/**
 * MyBatis-Plus Mapper for ShiftSchedule.
 * Provides standard CRUD operations via BaseMapper.
 */
@Mapper
public interface ShiftScheduleMapper extends BaseMapper<ShiftSchedule> {
}
