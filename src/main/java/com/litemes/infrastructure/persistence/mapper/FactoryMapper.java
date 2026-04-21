package com.litemes.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litemes.domain.entity.Factory;
import org.apache.ibatis.annotations.Mapper;

/**
 * MyBatis-Plus Mapper for Factory entity.
 * Provides standard CRUD operations via BaseMapper.
 */
@Mapper
public interface FactoryMapper extends BaseMapper<Factory> {
}
