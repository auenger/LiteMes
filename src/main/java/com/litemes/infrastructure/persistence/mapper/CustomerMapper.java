package com.litemes.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litemes.domain.entity.Customer;
import org.apache.ibatis.annotations.Mapper;

/**
 * MyBatis-Plus Mapper for Customer entity.
 * Provides standard CRUD operations via BaseMapper.
 */
@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {
}
