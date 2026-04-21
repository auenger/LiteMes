package com.litemes.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litemes.domain.entity.ExampleEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * MyBatis-Plus Mapper for ExampleEntity.
 * Provides standard CRUD operations via BaseMapper.
 */
@Mapper
public interface ExampleMapper extends BaseMapper<ExampleEntity> {
}
