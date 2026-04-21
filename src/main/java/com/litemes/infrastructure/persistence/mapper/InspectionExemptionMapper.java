package com.litemes.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litemes.domain.entity.InspectionExemption;
import org.apache.ibatis.annotations.Mapper;

/**
 * MyBatis-Plus Mapper for InspectionExemption entity.
 * Provides standard CRUD operations via BaseMapper.
 */
@Mapper
public interface InspectionExemptionMapper extends BaseMapper<InspectionExemption> {
}
