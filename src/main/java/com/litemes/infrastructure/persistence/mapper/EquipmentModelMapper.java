package com.litemes.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litemes.domain.entity.EquipmentModel;
import org.apache.ibatis.annotations.Mapper;

/**
 * MyBatis-Plus Mapper for EquipmentModel entity.
 * Provides standard CRUD operations via BaseMapper.
 */
@Mapper
public interface EquipmentModelMapper extends BaseMapper<EquipmentModel> {
}
