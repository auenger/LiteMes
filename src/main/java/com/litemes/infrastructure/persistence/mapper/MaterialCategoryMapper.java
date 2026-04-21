package com.litemes.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litemes.domain.entity.MaterialCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * MyBatis-Plus Mapper for MaterialCategory entity.
 * Provides standard CRUD operations via BaseMapper.
 */
@Mapper
public interface MaterialCategoryMapper extends BaseMapper<MaterialCategory> {
}
