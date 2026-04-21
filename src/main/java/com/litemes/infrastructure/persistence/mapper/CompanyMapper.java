package com.litemes.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litemes.domain.entity.Company;
import org.apache.ibatis.annotations.Mapper;

/**
 * MyBatis-Plus Mapper for Company entity.
 * Provides standard CRUD operations via BaseMapper.
 */
@Mapper
public interface CompanyMapper extends BaseMapper<Company> {
}
