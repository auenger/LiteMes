package com.litemes.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litemes.domain.entity.Supplier;
import org.apache.ibatis.annotations.Mapper;

/**
 * MyBatis-Plus Mapper for Supplier entity.
 * Provides standard CRUD operations via BaseMapper.
 */
@Mapper
public interface SupplierMapper extends BaseMapper<Supplier> {
}
