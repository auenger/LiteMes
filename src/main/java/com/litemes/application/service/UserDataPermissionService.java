package com.litemes.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litemes.domain.entity.DataPermissionGroup;
import com.litemes.domain.entity.DataPermissionGroupFactory;
import com.litemes.domain.entity.DataPermissionGroupProcess;
import com.litemes.domain.entity.DataPermissionGroupWorkCenter;
import com.litemes.domain.entity.Factory;
import com.litemes.domain.entity.Process;
import com.litemes.domain.entity.User;
import com.litemes.domain.entity.UserDataPermission;
import com.litemes.domain.entity.UserDataPermissionFactory;
import com.litemes.domain.entity.UserDataPermissionProcess;
import com.litemes.domain.entity.UserDataPermissionWorkCenter;
import com.litemes.domain.entity.WorkCenter;
import com.litemes.domain.event.BusinessException;
import com.litemes.domain.repository.DataPermissionGroupRepository;
import com.litemes.domain.repository.FactoryRepository;
import com.litemes.domain.repository.ProcessRepository;
import com.litemes.domain.repository.UserDataPermissionRepository;
import com.litemes.domain.repository.UserRepository;
import com.litemes.domain.repository.WorkCenterRepository;
import com.litemes.web.dto.BatchAssignPermissionDto;
import com.litemes.web.dto.DirectAssignDto;
import com.litemes.web.dto.PagedResult;
import com.litemes.web.dto.UserDataPermissionQueryDto;
import com.litemes.web.dto.UserDataPermissionVo;
import com.litemes.web.dto.UserPermissionAssociatedEntityDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Application service for UserDataPermission management.
 * Handles batch assign, direct assign, list, delete, and association operations.
 */
@ApplicationScoped
public class UserDataPermissionService {

    private static final Logger LOG = Logger.getLogger(UserDataPermissionService.class);
    private static final String SOURCE_GROUP = "GROUP";
    private static final String SOURCE_DIRECT = "DIRECT";

    @Inject
    UserDataPermissionRepository repository;

    @Inject
    DataPermissionGroupRepository groupRepository;

    @Inject
    FactoryRepository factoryRepository;

    @Inject
    WorkCenterRepository workCenterRepository;

    @Inject
    ProcessRepository processRepository;

    @Inject
    UserRepository userRepository;

    /**
     * Paginated list of user permissions with username/realName fuzzy search.
     */
    public PagedResult<UserDataPermissionVo> list(UserDataPermissionQueryDto query) {
        LOG.debugf("Listing user data permissions with query: username=%s, realName=%s", query.getUsername(), query.getRealName());

        IPage<User> page = new Page<>(query.getPage(), query.getSize());
        IPage<User> userPage = repository.findUserPermissionPage(page, query.getUsername(), query.getRealName());

        List<UserDataPermissionVo> records = userPage.getRecords().stream()
                .map(user -> {
                    UserDataPermissionVo vo = new UserDataPermissionVo();
                    vo.setUserId(user.getId());
                    vo.setUsername(user.getUsername());
                    vo.setRealName(user.getRealName());

                    // Load permission record for this user
                    Optional<UserDataPermission> permOpt = repository.findByUserId(user.getId());
                    if (permOpt.isPresent()) {
                        UserDataPermission perm = permOpt.get();
                        vo.setId(perm.getId());
                        vo.setGroupId(perm.getGroupId());
                        vo.setCreatedBy(perm.getCreatedBy());
                        vo.setCreatedAt(perm.getCreatedAt());
                        vo.setUpdatedBy(perm.getUpdatedBy());
                        vo.setUpdatedAt(perm.getUpdatedAt());

                        // Load group name
                        if (perm.getGroupId() != null) {
                            groupRepository.findById(perm.getGroupId())
                                    .ifPresent(group -> vo.setGroupName(group.getGroupName()));
                        }

                        // Load association counts
                        vo.setFactoryCount(repository.findFactoriesByUserPermissionId(perm.getId()).size());
                        vo.setWorkCenterCount(repository.findWorkCentersByUserPermissionId(perm.getId()).size());
                        vo.setProcessCount(repository.findProcessesByUserPermissionId(perm.getId()).size());
                    }
                    return vo;
                })
                .collect(Collectors.toList());

        return new PagedResult<>(records, userPage.getTotal(), userPage.getCurrent(), userPage.getSize());
    }

    /**
     * Batch assign a permission group to multiple users.
     * Creates or updates UserDataPermission records and copies group associations.
     */
    @Transactional
    public void batchAssign(BatchAssignPermissionDto dto) {
        LOG.debugf("Batch assigning permission group %d to %s users", Long.valueOf(dto.getGroupId()), dto.getUserIds() == null ? "0" : String.valueOf(dto.getUserIds().size()));

        // Validate group exists
        DataPermissionGroup group = groupRepository.findById(dto.getGroupId())
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "权限组不存在: " + dto.getGroupId()));

        // Load group associations
        List<DataPermissionGroupFactory> groupFactories = groupRepository.findFactoriesByGroupId(dto.getGroupId());
        List<DataPermissionGroupWorkCenter> groupWorkCenters = groupRepository.findWorkCentersByGroupId(dto.getGroupId());
        List<DataPermissionGroupProcess> groupProcesses = groupRepository.findProcessesByGroupId(dto.getGroupId());

        for (Long userId : dto.getUserIds()) {
            // Validate user exists
            userRepository.findById(userId)
                    .orElseThrow(() -> new BusinessException("NOT_FOUND", "用户不存在: " + userId));

            // Find or create permission record
            Optional<UserDataPermission> existingOpt = repository.findByUserId(userId);
            UserDataPermission permission;

            if (existingOpt.isPresent()) {
                permission = existingOpt.get();
                permission.setGroupId(dto.getGroupId());
                repository.update(permission);

                // Delete old GROUP-sourced associations, keep DIRECT ones
                repository.deleteFactoryAssociationsByUserPermissionIdAndSource(permission.getId(), SOURCE_GROUP);
                repository.deleteWorkCenterAssociationsByUserPermissionIdAndSource(permission.getId(), SOURCE_GROUP);
                repository.deleteProcessAssociationsByUserPermissionIdAndSource(permission.getId(), SOURCE_GROUP);
            } else {
                permission = new UserDataPermission(userId, dto.getGroupId());
                repository.save(permission);
            }

            // Copy group associations with source=GROUP
            List<UserDataPermissionFactory> factoryAssociations = groupFactories.stream()
                    .map(gf -> new UserDataPermissionFactory(permission.getId(), gf.getFactoryId(), SOURCE_GROUP))
                    .collect(Collectors.toList());
            if (!factoryAssociations.isEmpty()) {
                repository.saveFactoryAssociationsBatch(factoryAssociations);
            }

            List<UserDataPermissionWorkCenter> wcAssociations = groupWorkCenters.stream()
                    .map(gw -> new UserDataPermissionWorkCenter(permission.getId(), gw.getWorkCenterId(), SOURCE_GROUP))
                    .collect(Collectors.toList());
            if (!wcAssociations.isEmpty()) {
                repository.saveWorkCenterAssociationsBatch(wcAssociations);
            }

            List<UserDataPermissionProcess> processAssociations = groupProcesses.stream()
                    .map(gp -> new UserDataPermissionProcess(permission.getId(), gp.getProcessId(), SOURCE_GROUP))
                    .collect(Collectors.toList());
            if (!processAssociations.isEmpty()) {
                repository.saveProcessAssociationsBatch(processAssociations);
            }
        }

        LOG.infof("Batch assigned permission group %d to %d users", dto.getGroupId(), dto.getUserIds().size());
    }

    /**
     * Delete a user's data permission and all associated records.
     */
    @Transactional
    public void delete(Long id) {
        LOG.debugf("Deleting user data permission: %d", id);
        findPermissionOrThrow(id);

        // Delete all associations first
        repository.deleteFactoryAssociationsByUserPermissionId(id);
        repository.deleteWorkCenterAssociationsByUserPermissionId(id);
        repository.deleteProcessAssociationsByUserPermissionId(id);

        // Delete main record
        repository.deleteById(id);
        LOG.infof("Deleted user data permission: %d", id);
    }

    /**
     * Get factories associated with a user's permission.
     */
    public List<UserPermissionAssociatedEntityDto> getFactories(Long permissionId) {
        LOG.debugf("Getting factories for user permission: %d", permissionId);
        findPermissionOrThrow(permissionId);

        List<UserDataPermissionFactory> associations = repository.findFactoriesByUserPermissionId(permissionId);
        return buildAssociatedEntityList(associations, UserDataPermissionFactory::getFactoryId,
                UserDataPermissionFactory::getSource, factoryRepository::findById,
                f -> new UserPermissionAssociatedEntityDto(f.getId(), f.getFactoryCode(), f.getName(), ""));
    }

    /**
     * Directly assign factories to a user's permission (source=DIRECT).
     * Keeps existing GROUP-sourced records.
     */
    @Transactional
    public void directAssignFactories(Long permissionId, DirectAssignDto dto) {
        LOG.debugf("Direct assigning factories to permission: %d", permissionId);
        findPermissionOrThrow(permissionId);

        List<Long> newIds = dto.getIds() != null ? dto.getIds() : new ArrayList<>();

        // Delete only DIRECT-sourced factory associations
        repository.deleteFactoryAssociationsByUserPermissionIdAndSource(permissionId, SOURCE_DIRECT);

        // Add new DIRECT associations
        List<UserDataPermissionFactory> newAssociations = newIds.stream()
                .map(factoryId -> new UserDataPermissionFactory(permissionId, factoryId, SOURCE_DIRECT))
                .collect(Collectors.toList());
        if (!newAssociations.isEmpty()) {
            repository.saveFactoryAssociationsBatch(newAssociations);
        }

        LOG.infof("Direct assigned %d factories to permission %d", newIds.size(), permissionId);
    }

    /**
     * Get work centers associated with a user's permission.
     */
    public List<UserPermissionAssociatedEntityDto> getWorkCenters(Long permissionId) {
        LOG.debugf("Getting work centers for user permission: %d", permissionId);
        findPermissionOrThrow(permissionId);

        List<UserDataPermissionWorkCenter> associations = repository.findWorkCentersByUserPermissionId(permissionId);
        return buildAssociatedEntityList(associations, UserDataPermissionWorkCenter::getWorkCenterId,
                UserDataPermissionWorkCenter::getSource, workCenterRepository::findById,
                wc -> new UserPermissionAssociatedEntityDto(wc.getId(), wc.getWorkCenterCode(), wc.getName(), ""));
    }

    /**
     * Directly assign work centers to a user's permission (source=DIRECT).
     */
    @Transactional
    public void directAssignWorkCenters(Long permissionId, DirectAssignDto dto) {
        LOG.debugf("Direct assigning work centers to permission: %d", permissionId);
        findPermissionOrThrow(permissionId);

        List<Long> newIds = dto.getIds() != null ? dto.getIds() : new ArrayList<>();

        repository.deleteWorkCenterAssociationsByUserPermissionIdAndSource(permissionId, SOURCE_DIRECT);

        List<UserDataPermissionWorkCenter> newAssociations = newIds.stream()
                .map(wcId -> new UserDataPermissionWorkCenter(permissionId, wcId, SOURCE_DIRECT))
                .collect(Collectors.toList());
        if (!newAssociations.isEmpty()) {
            repository.saveWorkCenterAssociationsBatch(newAssociations);
        }

        LOG.infof("Direct assigned %d work centers to permission %d", newIds.size(), permissionId);
    }

    /**
     * Get processes associated with a user's permission.
     */
    public List<UserPermissionAssociatedEntityDto> getProcesses(Long permissionId) {
        LOG.debugf("Getting processes for user permission: %d", permissionId);
        findPermissionOrThrow(permissionId);

        List<UserDataPermissionProcess> associations = repository.findProcessesByUserPermissionId(permissionId);
        return buildAssociatedEntityList(associations, UserDataPermissionProcess::getProcessId,
                UserDataPermissionProcess::getSource, processRepository::findById,
                p -> new UserPermissionAssociatedEntityDto(p.getId(), p.getProcessCode(), p.getName(), ""));
    }

    /**
     * Directly assign processes to a user's permission (source=DIRECT).
     */
    @Transactional
    public void directAssignProcesses(Long permissionId, DirectAssignDto dto) {
        LOG.debugf("Direct assigning processes to permission: %d", permissionId);
        findPermissionOrThrow(permissionId);

        List<Long> newIds = dto.getIds() != null ? dto.getIds() : new ArrayList<>();

        repository.deleteProcessAssociationsByUserPermissionIdAndSource(permissionId, SOURCE_DIRECT);

        List<UserDataPermissionProcess> newAssociations = newIds.stream()
                .map(processId -> new UserDataPermissionProcess(permissionId, processId, SOURCE_DIRECT))
                .collect(Collectors.toList());
        if (!newAssociations.isEmpty()) {
            repository.saveProcessAssociationsBatch(newAssociations);
        }

        LOG.infof("Direct assigned %d processes to permission %d", newIds.size(), permissionId);
    }

    /**
     * Get a user's effective permission scope (all factory/work-center/process IDs they have access to).
     * Used by the permission cache.
     */
    public UserPermissionScope getEffectivePermissionScope(Long userId) {
        Optional<UserDataPermission> permOpt = repository.findByUserId(userId);
        if (permOpt.isEmpty()) {
            return new UserPermissionScope(List.of(), List.of(), List.of());
        }

        UserDataPermission perm = permOpt.get();
        List<Long> factoryIds = repository.findFactoriesByUserPermissionId(perm.getId()).stream()
                .map(UserDataPermissionFactory::getFactoryId)
                .collect(Collectors.toList());
        List<Long> workCenterIds = repository.findWorkCentersByUserPermissionId(perm.getId()).stream()
                .map(UserDataPermissionWorkCenter::getWorkCenterId)
                .collect(Collectors.toList());
        List<Long> processIds = repository.findProcessesByUserPermissionId(perm.getId()).stream()
                .map(UserDataPermissionProcess::getProcessId)
                .collect(Collectors.toList());

        return new UserPermissionScope(factoryIds, workCenterIds, processIds);
    }

    /**
     * Find user permission by ID, or throw BusinessException.
     */
    private UserDataPermission findPermissionOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "用户数据权限不存在: " + id));
    }

    /**
     * Generic helper to build associated entity DTOs with source info.
     */
    private <T, E> List<UserPermissionAssociatedEntityDto> buildAssociatedEntityList(
            List<T> associations,
            Function<T, Long> idExtractor,
            Function<T, String> sourceExtractor,
            Function<Long, Optional<E>> entityLoader,
            Function<E, UserPermissionAssociatedEntityDto> dtoMapper) {

        if (associations.isEmpty()) {
            return new ArrayList<>();
        }

        return associations.stream()
                .map(a -> {
                    Long entityId = idExtractor.apply(a);
                    String source = sourceExtractor.apply(a);
                    Optional<E> entityOpt = entityLoader.apply(entityId);
                    UserPermissionAssociatedEntityDto dto;
                    if (entityOpt.isPresent()) {
                        dto = dtoMapper.apply(entityOpt.get());
                    } else {
                        dto = new UserPermissionAssociatedEntityDto(entityId, String.valueOf(entityId), "未知", "");
                    }
                    dto.setSource(source);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Simple data class representing a user's effective permission scope.
     */
    public static class UserPermissionScope {
        private final List<Long> factoryIds;
        private final List<Long> workCenterIds;
        private final List<Long> processIds;

        public UserPermissionScope(List<Long> factoryIds, List<Long> workCenterIds, List<Long> processIds) {
            this.factoryIds = factoryIds;
            this.workCenterIds = workCenterIds;
            this.processIds = processIds;
        }

        public List<Long> getFactoryIds() {
            return factoryIds;
        }

        public List<Long> getWorkCenterIds() {
            return workCenterIds;
        }

        public List<Long> getProcessIds() {
            return processIds;
        }
    }
}
