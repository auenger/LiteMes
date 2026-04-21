package com.litemes.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litemes.domain.entity.Uom;
import org.apache.ibatis.annotations.Mapper;

/**
 * MyBatis-Plus Mapper for Uom entity.
 * Provides standard CRUD operations via BaseMapper.
 */
@Mapper
public interface UomMapper extends BaseMapper<Uom> {
}
