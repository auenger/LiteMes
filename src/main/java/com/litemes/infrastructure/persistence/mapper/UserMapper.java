package com.litemes.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litemes.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * MyBatis-Plus Mapper for User entity.
 * Provides standard CRUD operations via BaseMapper.
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
