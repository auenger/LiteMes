package com.litemes.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litemes.domain.entity.Shift;
import org.apache.ibatis.annotations.Mapper;

/**
 * MyBatis-Plus Mapper for Shift.
 * Provides standard CRUD operations via BaseMapper.
 */
@Mapper
public interface ShiftMapper extends BaseMapper<Shift> {
}
