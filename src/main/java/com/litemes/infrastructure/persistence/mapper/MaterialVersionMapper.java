package com.litemes.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litemes.domain.entity.MaterialVersion;
import org.apache.ibatis.annotations.Mapper;

/**
 * MyBatis-Plus Mapper for MaterialVersion entity.
 * Provides standard CRUD operations via BaseMapper.
 */
@Mapper
public interface MaterialVersionMapper extends BaseMapper<MaterialVersion> {
}
