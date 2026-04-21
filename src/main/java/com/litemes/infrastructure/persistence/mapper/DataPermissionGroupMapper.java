package com.litemes.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litemes.domain.entity.DataPermissionGroup;
import org.apache.ibatis.annotations.Mapper;

/**
 * MyBatis-Plus Mapper for DataPermissionGroup entity.
 * Provides standard CRUD operations via BaseMapper.
 */
@Mapper
public interface DataPermissionGroupMapper extends BaseMapper<DataPermissionGroup> {
}
