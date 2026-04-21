package com.litemes.domain.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litemes.domain.entity.Department;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for Department entity.
 * Infrastructure layer provides the MyBatis-Plus implementation.
 */
public interface DepartmentRepository {

    Department save(Department entity);

    Optional<Department> findById(Long id);

    Optional<Department> findByDepartmentCode(String departmentCode);

    IPage<Department> findPage(IPage<Department> page, String departmentCode, String name, Long factoryId, Integer status);

    Department update(Department entity);

    void deleteById(Long id);

    boolean existsByDepartmentCode(String departmentCode);

    boolean hasChildren(Long parentId);

    boolean existsByFactoryId(Long factoryId);

    long countActiveByFactoryId(Long factoryId);

    List<Department> findByFactoryId(Long factoryId);

    List<Department> findAllActive();
}
