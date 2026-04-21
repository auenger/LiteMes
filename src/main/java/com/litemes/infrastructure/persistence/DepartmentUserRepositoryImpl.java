package com.litemes.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.litemes.domain.entity.DepartmentUser;
import com.litemes.domain.repository.DepartmentUserRepository;
import com.litemes.infrastructure.persistence.mapper.DepartmentUserMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

/**
 * MyBatis-Plus implementation of DepartmentUserRepository.
 */
@ApplicationScoped
public class DepartmentUserRepositoryImpl implements DepartmentUserRepository {

    @Inject
    DepartmentUserMapper departmentUserMapper;

    @Override
    public DepartmentUser save(DepartmentUser entity) {
        departmentUserMapper.insert(entity);
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        departmentUserMapper.deleteById(id);
    }

    @Override
    public void deleteByDepartmentId(Long departmentId) {
        LambdaQueryWrapper<DepartmentUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DepartmentUser::getDepartmentId, departmentId);
        departmentUserMapper.delete(wrapper);
    }

    @Override
    public void deleteByDepartmentIdAndUserId(Long departmentId, Long userId) {
        LambdaQueryWrapper<DepartmentUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DepartmentUser::getDepartmentId, departmentId)
               .eq(DepartmentUser::getUserId, userId);
        departmentUserMapper.delete(wrapper);
    }

    @Override
    public boolean existsByDepartmentIdAndUserId(Long departmentId, Long userId) {
        LambdaQueryWrapper<DepartmentUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DepartmentUser::getDepartmentId, departmentId)
               .eq(DepartmentUser::getUserId, userId);
        return departmentUserMapper.selectCount(wrapper) > 0;
    }

    @Override
    public List<DepartmentUser> findByDepartmentId(Long departmentId) {
        LambdaQueryWrapper<DepartmentUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DepartmentUser::getDepartmentId, departmentId)
               .orderByDesc(DepartmentUser::getCreatedAt);
        return departmentUserMapper.selectList(wrapper);
    }

    @Override
    public List<DepartmentUser> findByUserId(Long userId) {
        LambdaQueryWrapper<DepartmentUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DepartmentUser::getUserId, userId);
        return departmentUserMapper.selectList(wrapper);
    }

    @Override
    public long countByDepartmentId(Long departmentId) {
        LambdaQueryWrapper<DepartmentUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DepartmentUser::getDepartmentId, departmentId);
        return departmentUserMapper.selectCount(wrapper);
    }
}
