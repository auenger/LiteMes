package com.litemes.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litemes.domain.entity.Department;
import com.litemes.domain.repository.DepartmentRepository;
import com.litemes.infrastructure.persistence.mapper.DepartmentMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

/**
 * MyBatis-Plus implementation of DepartmentRepository.
 */
@ApplicationScoped
public class DepartmentRepositoryImpl implements DepartmentRepository {

    @Inject
    DepartmentMapper departmentMapper;

    @Override
    public Department save(Department entity) {
        departmentMapper.insert(entity);
        return entity;
    }

    @Override
    public Optional<Department> findById(Long id) {
        return Optional.ofNullable(departmentMapper.selectById(id));
    }

    @Override
    public Optional<Department> findByDepartmentCode(String departmentCode) {
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Department::getDepartmentCode, departmentCode);
        return Optional.ofNullable(departmentMapper.selectOne(wrapper));
    }

    @Override
    public IPage<Department> findPage(IPage<Department> page, String departmentCode, String name, Long factoryId, Integer status) {
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        if (departmentCode != null && !departmentCode.isBlank()) {
            wrapper.like(Department::getDepartmentCode, departmentCode);
        }
        if (name != null && !name.isBlank()) {
            wrapper.like(Department::getName, name);
        }
        if (factoryId != null) {
            wrapper.eq(Department::getFactoryId, factoryId);
        }
        if (status != null) {
            wrapper.eq(Department::getStatus, status);
        }
        wrapper.orderByAsc(Department::getSortOrder);
        wrapper.orderByDesc(Department::getCreatedAt);
        return departmentMapper.selectPage(page, wrapper);
    }

    @Override
    public Department update(Department entity) {
        departmentMapper.updateById(entity);
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        departmentMapper.deleteById(id);
    }

    @Override
    public boolean existsByDepartmentCode(String departmentCode) {
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Department::getDepartmentCode, departmentCode);
        return departmentMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean hasChildren(Long parentId) {
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Department::getParentId, parentId);
        return departmentMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsByFactoryId(Long factoryId) {
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Department::getFactoryId, factoryId);
        return departmentMapper.selectCount(wrapper) > 0;
    }

    @Override
    public long countActiveByFactoryId(Long factoryId) {
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Department::getFactoryId, factoryId)
               .eq(Department::getStatus, 1);
        return departmentMapper.selectCount(wrapper);
    }

    @Override
    public List<Department> findByFactoryId(Long factoryId) {
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Department::getFactoryId, factoryId)
               .orderByAsc(Department::getSortOrder);
        return departmentMapper.selectList(wrapper);
    }

    @Override
    public List<Department> findAllActive() {
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Department::getStatus, 1)
               .orderByAsc(Department::getSortOrder);
        return departmentMapper.selectList(wrapper);
    }
}
