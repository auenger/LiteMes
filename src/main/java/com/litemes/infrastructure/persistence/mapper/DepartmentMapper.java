package com.litemes.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litemes.domain.entity.Department;
import org.apache.ibatis.annotations.Mapper;

/**
 * MyBatis-Plus Mapper for Department entity.
 * Provides standard CRUD operations via BaseMapper.
 */
@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {
}
