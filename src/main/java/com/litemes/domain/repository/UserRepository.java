package com.litemes.domain.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litemes.domain.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for User entity.
 * Infrastructure layer provides the MyBatis-Plus implementation.
 */
public interface UserRepository {

    Optional<User> findById(Long id);

    IPage<User> findPage(IPage<User> page, String username, String realName);

    List<User> findByIds(List<Long> ids);
}
