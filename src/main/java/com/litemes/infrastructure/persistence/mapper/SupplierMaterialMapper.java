package com.litemes.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litemes.domain.entity.SupplierMaterial;
import org.apache.ibatis.annotations.Mapper;

/**
 * MyBatis-Plus Mapper for SupplierMaterial entity.
 * Provides standard CRUD operations via BaseMapper.
 */
@Mapper
public interface SupplierMaterialMapper extends BaseMapper<SupplierMaterial> {
}
