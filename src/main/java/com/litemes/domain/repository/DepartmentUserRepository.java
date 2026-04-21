package com.litemes.domain.repository;

import com.litemes.domain.entity.DepartmentUser;

import java.util.List;

/**
 * Domain repository interface for DepartmentUser entity.
 * Infrastructure layer provides the MyBatis-Plus implementation.
 */
public interface DepartmentUserRepository {

    DepartmentUser save(DepartmentUser entity);

    void deleteById(Long id);

    void deleteByDepartmentId(Long departmentId);

    void deleteByDepartmentIdAndUserId(Long departmentId, Long userId);

    boolean existsByDepartmentIdAndUserId(Long departmentId, Long userId);

    List<DepartmentUser> findByDepartmentId(Long departmentId);

    List<DepartmentUser> findByUserId(Long userId);

    long countByDepartmentId(Long departmentId);
}
