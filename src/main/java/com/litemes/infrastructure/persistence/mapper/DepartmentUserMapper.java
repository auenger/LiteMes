package com.litemes.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litemes.domain.entity.DepartmentUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * MyBatis-Plus Mapper for DepartmentUser entity.
 * Provides standard CRUD operations via BaseMapper.
 */
@Mapper
public interface DepartmentUserMapper extends BaseMapper<DepartmentUser> {
}
