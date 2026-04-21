package com.litemes.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litemes.domain.entity.User;
import com.litemes.domain.entity.UserDataPermission;
import com.litemes.domain.entity.UserDataPermissionFactory;
import com.litemes.domain.entity.UserDataPermissionProcess;
import com.litemes.domain.entity.UserDataPermissionWorkCenter;
import com.litemes.domain.repository.UserDataPermissionRepository;
import com.litemes.infrastructure.persistence.mapper.UserDataPermissionFactoryMapper;
import com.litemes.infrastructure.persistence.mapper.UserDataPermissionMapper;
import com.litemes.infrastructure.persistence.mapper.UserDataPermissionProcessMapper;
import com.litemes.infrastructure.persistence.mapper.UserDataPermissionWorkCenterMapper;
import com.litemes.infrastructure.persistence.mapper.UserMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

/**
 * MyBatis-Plus implementation of UserDataPermissionRepository.
 */
@ApplicationScoped
public class UserDataPermissionRepositoryImpl implements UserDataPermissionRepository {

    @Inject
    UserDataPermissionMapper permissionMapper;

    @Inject
    UserDataPermissionFactoryMapper factoryMapper;

    @Inject
    UserDataPermissionWorkCenterMapper workCenterMapper;

    @Inject
    UserDataPermissionProcessMapper processMapper;

    @Inject
    UserMapper userMapper;

    @Override
    public UserDataPermission save(UserDataPermission entity) {
        permissionMapper.insert(entity);
        return entity;
    }

    @Override
    public Optional<UserDataPermission> findById(Long id) {
        return Optional.ofNullable(permissionMapper.selectById(id));
    }

    @Override
    public Optional<UserDataPermission> findByUserId(Long userId) {
        LambdaQueryWrapper<UserDataPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDataPermission::getUserId, userId);
        return Optional.ofNullable(permissionMapper.selectOne(wrapper));
    }

    @Override
    public IPage<User> findUserPermissionPage(IPage<User> page, String username, String realName) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (username != null && !username.isBlank()) {
            wrapper.like(User::getUsername, username);
        }
        if (realName != null && !realName.isBlank()) {
            wrapper.like(User::getRealName, realName);
        }
        wrapper.orderByDesc(User::getCreatedAt);
        return userMapper.selectPage(page, wrapper);
    }

    @Override
    public UserDataPermission update(UserDataPermission entity) {
        permissionMapper.updateById(entity);
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        permissionMapper.deleteById(id);
    }

    // Factory association operations

    @Override
    public List<UserDataPermissionFactory> findFactoriesByUserPermissionId(Long userPermissionId) {
        LambdaQueryWrapper<UserDataPermissionFactory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDataPermissionFactory::getUserPermissionId, userPermissionId);
        return factoryMapper.selectList(wrapper);
    }

    @Override
    public List<UserDataPermissionFactory> findFactoriesByUserPermissionIdAndSource(Long userPermissionId, String source) {
        LambdaQueryWrapper<UserDataPermissionFactory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDataPermissionFactory::getUserPermissionId, userPermissionId);
        wrapper.eq(UserDataPermissionFactory::getSource, source);
        return factoryMapper.selectList(wrapper);
    }

    @Override
    public void saveFactoryAssociationsBatch(List<UserDataPermissionFactory> associations) {
        for (UserDataPermissionFactory association : associations) {
            factoryMapper.insert(association);
        }
    }

    @Override
    public void deleteFactoryAssociationsByUserPermissionId(Long userPermissionId) {
        LambdaQueryWrapper<UserDataPermissionFactory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDataPermissionFactory::getUserPermissionId, userPermissionId);
        factoryMapper.delete(wrapper);
    }

    @Override
    public void deleteFactoryAssociationsByUserPermissionIdAndSource(Long userPermissionId, String source) {
        LambdaQueryWrapper<UserDataPermissionFactory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDataPermissionFactory::getUserPermissionId, userPermissionId);
        wrapper.eq(UserDataPermissionFactory::getSource, source);
        factoryMapper.delete(wrapper);
    }

    // Work Center association operations

    @Override
    public List<UserDataPermissionWorkCenter> findWorkCentersByUserPermissionId(Long userPermissionId) {
        LambdaQueryWrapper<UserDataPermissionWorkCenter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDataPermissionWorkCenter::getUserPermissionId, userPermissionId);
        return workCenterMapper.selectList(wrapper);
    }

    @Override
    public List<UserDataPermissionWorkCenter> findWorkCentersByUserPermissionIdAndSource(Long userPermissionId, String source) {
        LambdaQueryWrapper<UserDataPermissionWorkCenter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDataPermissionWorkCenter::getUserPermissionId, userPermissionId);
        wrapper.eq(UserDataPermissionWorkCenter::getSource, source);
        return workCenterMapper.selectList(wrapper);
    }

    @Override
    public void saveWorkCenterAssociationsBatch(List<UserDataPermissionWorkCenter> associations) {
        for (UserDataPermissionWorkCenter association : associations) {
            workCenterMapper.insert(association);
        }
    }

    @Override
    public void deleteWorkCenterAssociationsByUserPermissionId(Long userPermissionId) {
        LambdaQueryWrapper<UserDataPermissionWorkCenter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDataPermissionWorkCenter::getUserPermissionId, userPermissionId);
        workCenterMapper.delete(wrapper);
    }

    @Override
    public void deleteWorkCenterAssociationsByUserPermissionIdAndSource(Long userPermissionId, String source) {
        LambdaQueryWrapper<UserDataPermissionWorkCenter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDataPermissionWorkCenter::getUserPermissionId, userPermissionId);
        wrapper.eq(UserDataPermissionWorkCenter::getSource, source);
        workCenterMapper.delete(wrapper);
    }

    // Process association operations

    @Override
    public List<UserDataPermissionProcess> findProcessesByUserPermissionId(Long userPermissionId) {
        LambdaQueryWrapper<UserDataPermissionProcess> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDataPermissionProcess::getUserPermissionId, userPermissionId);
        return processMapper.selectList(wrapper);
    }

    @Override
    public List<UserDataPermissionProcess> findProcessesByUserPermissionIdAndSource(Long userPermissionId, String source) {
        LambdaQueryWrapper<UserDataPermissionProcess> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDataPermissionProcess::getUserPermissionId, userPermissionId);
        wrapper.eq(UserDataPermissionProcess::getSource, source);
        return processMapper.selectList(wrapper);
    }

    @Override
    public void saveProcessAssociationsBatch(List<UserDataPermissionProcess> associations) {
        for (UserDataPermissionProcess association : associations) {
            processMapper.insert(association);
        }
    }

    @Override
    public void deleteProcessAssociationsByUserPermissionId(Long userPermissionId) {
        LambdaQueryWrapper<UserDataPermissionProcess> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDataPermissionProcess::getUserPermissionId, userPermissionId);
        processMapper.delete(wrapper);
    }

    @Override
    public void deleteProcessAssociationsByUserPermissionIdAndSource(Long userPermissionId, String source) {
        LambdaQueryWrapper<UserDataPermissionProcess> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDataPermissionProcess::getUserPermissionId, userPermissionId);
        wrapper.eq(UserDataPermissionProcess::getSource, source);
        processMapper.delete(wrapper);
    }
}
