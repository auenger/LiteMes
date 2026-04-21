package com.litemes.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litemes.domain.entity.EquipmentType;
import org.apache.ibatis.annotations.Mapper;

/**
 * MyBatis-Plus Mapper for EquipmentType entity.
 * Provides standard CRUD operations via BaseMapper.
 */
@Mapper
public interface EquipmentTypeMapper extends BaseMapper<EquipmentType> {
}
