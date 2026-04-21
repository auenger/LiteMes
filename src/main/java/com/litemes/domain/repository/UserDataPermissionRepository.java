package com.litemes.domain.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litemes.domain.entity.User;
import com.litemes.domain.entity.UserDataPermission;
import com.litemes.domain.entity.UserDataPermissionFactory;
import com.litemes.domain.entity.UserDataPermissionProcess;
import com.litemes.domain.entity.UserDataPermissionWorkCenter;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for UserDataPermission entity.
 * Infrastructure layer provides the MyBatis-Plus implementation.
 */
public interface UserDataPermissionRepository {

    UserDataPermission save(UserDataPermission entity);

    Optional<UserDataPermission> findById(Long id);

    Optional<UserDataPermission> findByUserId(Long userId);

    IPage<User> findUserPermissionPage(IPage<User> page, String username, String realName);

    UserDataPermission update(UserDataPermission entity);

    void deleteById(Long id);

    // Factory association operations
    List<UserDataPermissionFactory> findFactoriesByUserPermissionId(Long userPermissionId);

    List<UserDataPermissionFactory> findFactoriesByUserPermissionIdAndSource(Long userPermissionId, String source);

    void saveFactoryAssociationsBatch(List<UserDataPermissionFactory> associations);

    void deleteFactoryAssociationsByUserPermissionId(Long userPermissionId);

    void deleteFactoryAssociationsByUserPermissionIdAndSource(Long userPermissionId, String source);

    // Work Center association operations
    List<UserDataPermissionWorkCenter> findWorkCentersByUserPermissionId(Long userPermissionId);

    List<UserDataPermissionWorkCenter> findWorkCentersByUserPermissionIdAndSource(Long userPermissionId, String source);

    void saveWorkCenterAssociationsBatch(List<UserDataPermissionWorkCenter> associations);

    void deleteWorkCenterAssociationsByUserPermissionId(Long userPermissionId);

    void deleteWorkCenterAssociationsByUserPermissionIdAndSource(Long userPermissionId, String source);

    // Process association operations
    List<UserDataPermissionProcess> findProcessesByUserPermissionId(Long userPermissionId);

    List<UserDataPermissionProcess> findProcessesByUserPermissionIdAndSource(Long userPermissionId, String source);

    void saveProcessAssociationsBatch(List<UserDataPermissionProcess> associations);

    void deleteProcessAssociationsByUserPermissionId(Long userPermissionId);

    void deleteProcessAssociationsByUserPermissionIdAndSource(Long userPermissionId, String source);
}
