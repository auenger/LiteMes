package com.litemes.domain.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litemes.domain.entity.DataPermissionGroup;
import com.litemes.domain.entity.DataPermissionGroupFactory;
import com.litemes.domain.entity.DataPermissionGroupProcess;
import com.litemes.domain.entity.DataPermissionGroupWorkCenter;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for DataPermissionGroup entity.
 * Infrastructure layer provides the MyBatis-Plus implementation.
 */
public interface DataPermissionGroupRepository {

    DataPermissionGroup save(DataPermissionGroup entity);

    Optional<DataPermissionGroup> findById(Long id);

    IPage<DataPermissionGroup> findPage(IPage<DataPermissionGroup> page, String groupName);

    DataPermissionGroup update(DataPermissionGroup entity);

    void deleteById(Long id);

    boolean existsByGroupName(String groupName);

    boolean existsByGroupNameExcludeId(String groupName, Long excludeId);

    // Factory association operations
    List<DataPermissionGroupFactory> findFactoriesByGroupId(Long groupId);

    void saveFactoryAssociation(DataPermissionGroupFactory association);

    void deleteFactoryAssociationsByGroupId(Long groupId);

    void deleteFactoryAssociationsByGroupIdNotInIds(Long groupId, List<Long> factoryIds);

    void saveFactoryAssociationsBatch(List<DataPermissionGroupFactory> associations);

    // Work Center association operations
    List<DataPermissionGroupWorkCenter> findWorkCentersByGroupId(Long groupId);

    void saveWorkCenterAssociation(DataPermissionGroupWorkCenter association);

    void deleteWorkCenterAssociationsByGroupId(Long groupId);

    void deleteWorkCenterAssociationsByGroupIdNotInIds(Long groupId, List<Long> workCenterIds);

    void saveWorkCenterAssociationsBatch(List<DataPermissionGroupWorkCenter> associations);

    // Process association operations
    List<DataPermissionGroupProcess> findProcessesByGroupId(Long groupId);

    void saveProcessAssociation(DataPermissionGroupProcess association);

    void deleteProcessAssociationsByGroupId(Long groupId);

    void deleteProcessAssociationsByGroupIdNotInIds(Long groupId, List<Long> processIds);

    void saveProcessAssociationsBatch(List<DataPermissionGroupProcess> associations);
}
