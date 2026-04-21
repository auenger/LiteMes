package com.litemes.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litemes.domain.entity.DataPermissionGroup;
import com.litemes.domain.entity.DataPermissionGroupFactory;
import com.litemes.domain.entity.DataPermissionGroupProcess;
import com.litemes.domain.entity.DataPermissionGroupWorkCenter;
import com.litemes.domain.entity.Factory;
import com.litemes.domain.entity.Process;
import com.litemes.domain.entity.WorkCenter;
import com.litemes.domain.event.BusinessException;
import com.litemes.domain.repository.DataPermissionGroupRepository;
import com.litemes.domain.repository.FactoryRepository;
import com.litemes.domain.repository.ProcessRepository;
import com.litemes.domain.repository.WorkCenterRepository;
import com.litemes.web.dto.AssociatedEntityDto;
import com.litemes.web.dto.AssociatedItemDto;
import com.litemes.web.dto.DataPermissionGroupCreateDto;
import com.litemes.web.dto.DataPermissionGroupDto;
import com.litemes.web.dto.DataPermissionGroupQueryDto;
import com.litemes.web.dto.DataPermissionGroupUpdateDto;
import com.litemes.web.dto.PagedResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Application service for DataPermissionGroup CRUD and association operations.
 * Handles DTO <-> Entity conversion, business validation, and orchestration.
 */
@ApplicationScoped
public class DataPermissionGroupService {

    private static final Logger LOG = Logger.getLogger(DataPermissionGroupService.class);

    @Inject
    DataPermissionGroupRepository repository;

    @Inject
    FactoryRepository factoryRepository;

    @Inject
    WorkCenterRepository workCenterRepository;

    @Inject
    ProcessRepository processRepository;

    @Transactional
    public Long create(DataPermissionGroupCreateDto dto) {
        LOG.debugf("Creating data permission group: %s", dto.getGroupName());

        // Check uniqueness of group name
        if (repository.existsByGroupName(dto.getGroupName())) {
            throw new BusinessException("GROUP_NAME_DUPLICATE", "权限组名称已存在");
        }

        DataPermissionGroup entity = new DataPermissionGroup(dto.getGroupName(), dto.getRemark());
        repository.save(entity);

        LOG.infof("Created data permission group with id: %d, name: %s", entity.getId(), entity.getGroupName());
        return entity.getId();
    }

    public DataPermissionGroupDto getById(Long id) {
        LOG.debugf("Getting data permission group by id: %d", id);
        DataPermissionGroup entity = findOrThrow(id);
        DataPermissionGroupDto dto = toDto(entity);

        // Load association counts
        dto.setFactoryCount(repository.findFactoriesByGroupId(id).size());
        dto.setWorkCenterCount(repository.findWorkCentersByGroupId(id).size());
        dto.setProcessCount(repository.findProcessesByGroupId(id).size());

        // Reference check (will be used by feat-user-permission, default false for now)
        dto.setReferenced(false);

        return dto;
    }

    public PagedResult<DataPermissionGroupDto> list(DataPermissionGroupQueryDto query) {
        LOG.debugf("Listing data permission groups with query: groupName=%s", query.getGroupName());

        IPage<DataPermissionGroup> page = new Page<>(query.getPage(), query.getSize());
        IPage<DataPermissionGroup> result = repository.findPage(page, query.getGroupName());

        List<DataPermissionGroupDto> records = result.getRecords().stream()
                .map(entity -> {
                    DataPermissionGroupDto dto = toDto(entity);
                    dto.setFactoryCount(repository.findFactoriesByGroupId(entity.getId()).size());
                    dto.setWorkCenterCount(repository.findWorkCentersByGroupId(entity.getId()).size());
                    dto.setProcessCount(repository.findProcessesByGroupId(entity.getId()).size());
                    dto.setReferenced(false);
                    return dto;
                })
                .collect(Collectors.toList());

        return new PagedResult<>(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Transactional
    public void update(Long id, DataPermissionGroupUpdateDto dto) {
        LOG.debugf("Updating data permission group: %d", id);
        DataPermissionGroup entity = findOrThrow(id);

        if (dto.getGroupName() != null && !dto.getGroupName().isBlank()) {
            // Check uniqueness of group name excluding self
            if (repository.existsByGroupNameExcludeId(dto.getGroupName(), id)) {
                throw new BusinessException("GROUP_NAME_DUPLICATE", "权限组名称已存在");
            }
            entity.setGroupName(dto.getGroupName());
        }
        if (dto.getRemark() != null) {
            entity.setRemark(dto.getRemark());
        }

        repository.update(entity);
        LOG.infof("Updated data permission group: %d", id);
    }

    @Transactional
    public void delete(Long id) {
        LOG.debugf("Deleting data permission group: %d", id);
        findOrThrow(id);

        // Delete all associations first
        repository.deleteFactoryAssociationsByGroupId(id);
        repository.deleteWorkCenterAssociationsByGroupId(id);
        repository.deleteProcessAssociationsByGroupId(id);

        // Soft delete the group
        repository.deleteById(id);
        LOG.infof("Deleted data permission group: %d", id);
    }

    // Factory association operations

    public List<AssociatedEntityDto> getFactories(Long groupId) {
        LOG.debugf("Getting factories for group: %d", groupId);
        findOrThrow(groupId);

        List<DataPermissionGroupFactory> associations = repository.findFactoriesByGroupId(groupId);
        if (associations.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> factoryIds = associations.stream()
                .map(DataPermissionGroupFactory::getFactoryId)
                .collect(Collectors.toList());

        // Batch load factories
        Map<Long, Factory> factoryMap = factoryIds.stream()
                .map(factoryRepository::findById)
                .filter(opt -> opt.isPresent())
                .map(opt -> opt.get())
                .collect(Collectors.toMap(Factory::getId, Function.identity()));

        return associations.stream()
                .map(a -> {
                    Factory f = factoryMap.get(a.getFactoryId());
                    if (f != null) {
                        return new AssociatedEntityDto(f.getId(), f.getFactoryCode(), f.getName());
                    }
                    return new AssociatedEntityDto(a.getFactoryId(), String.valueOf(a.getFactoryId()), "未知工厂");
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveFactories(Long groupId, AssociatedItemDto dto) {
        LOG.debug("Saving factories for group: " + groupId);
        findOrThrow(groupId);

        // Full replacement strategy: delete removed, add new
        List<DataPermissionGroupFactory> existing = repository.findFactoriesByGroupId(groupId);
        List<Long> existingIds = existing.stream()
                .map(DataPermissionGroupFactory::getFactoryId)
                .collect(Collectors.toList());

        List<Long> newIds = dto.getIds() != null ? dto.getIds() : new ArrayList<>();

        // Find ids to remove (in existing but not in new)
        List<Long> toRemove = existingIds.stream()
                .filter(id -> !newIds.contains(id))
                .collect(Collectors.toList());

        // Find ids to add (in new but not in existing)
        List<Long> toAdd = newIds.stream()
                .filter(id -> !existingIds.contains(id))
                .collect(Collectors.toList());

        // Remove old associations
        if (!toRemove.isEmpty()) {
            repository.deleteFactoryAssociationsByGroupIdNotInIds(groupId,
                    existingIds.stream().filter(id -> !toRemove.contains(id)).collect(Collectors.toList()));
            // Actually, we need to delete the specific ones. Let me use a different approach
            // Delete only the ones that should be removed
            for (Long factoryId : toRemove) {
                List<DataPermissionGroupFactory> toDeleteRecords = existing.stream()
                        .filter(a -> a.getFactoryId().equals(factoryId))
                        .collect(Collectors.toList());
                // For simplicity, delete all and re-add the kept ones + new ones
            }
        }

        // Simpler full replacement: delete all, then insert all new
        repository.deleteFactoryAssociationsByGroupId(groupId);

        List<DataPermissionGroupFactory> newAssociations = newIds.stream()
                .map(factoryId -> new DataPermissionGroupFactory(groupId, factoryId))
                .collect(Collectors.toList());

        if (!newAssociations.isEmpty()) {
            repository.saveFactoryAssociationsBatch(newAssociations);
        }

        LOG.infof("Saved factories for group %d: %d associations", groupId, newIds.size());
    }

    // Work Center association operations

    public List<AssociatedEntityDto> getWorkCenters(Long groupId) {
        LOG.debugf("Getting work centers for group: %d", groupId);
        findOrThrow(groupId);

        List<DataPermissionGroupWorkCenter> associations = repository.findWorkCentersByGroupId(groupId);
        if (associations.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> wcIds = associations.stream()
                .map(DataPermissionGroupWorkCenter::getWorkCenterId)
                .collect(Collectors.toList());

        Map<Long, WorkCenter> wcMap = wcIds.stream()
                .map(workCenterRepository::findById)
                .filter(opt -> opt.isPresent())
                .map(opt -> opt.get())
                .collect(Collectors.toMap(WorkCenter::getId, Function.identity()));

        return associations.stream()
                .map(a -> {
                    WorkCenter wc = wcMap.get(a.getWorkCenterId());
                    if (wc != null) {
                        return new AssociatedEntityDto(wc.getId(), wc.getWorkCenterCode(), wc.getName());
                    }
                    return new AssociatedEntityDto(a.getWorkCenterId(), String.valueOf(a.getWorkCenterId()), "未知工作中心");
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveWorkCenters(Long groupId, AssociatedItemDto dto) {
        LOG.debug("Saving work centers for group: " + groupId);
        findOrThrow(groupId);

        // Full replacement strategy
        repository.deleteWorkCenterAssociationsByGroupId(groupId);

        List<Long> newIds = dto.getIds() != null ? dto.getIds() : new ArrayList<>();
        List<DataPermissionGroupWorkCenter> newAssociations = newIds.stream()
                .map(wcId -> new DataPermissionGroupWorkCenter(groupId, wcId))
                .collect(Collectors.toList());

        if (!newAssociations.isEmpty()) {
            repository.saveWorkCenterAssociationsBatch(newAssociations);
        }

        LOG.infof("Saved work centers for group %d: %d associations", groupId, newIds.size());
    }

    // Process association operations

    public List<AssociatedEntityDto> getProcesses(Long groupId) {
        LOG.debugf("Getting processes for group: %d", groupId);
        findOrThrow(groupId);

        List<DataPermissionGroupProcess> associations = repository.findProcessesByGroupId(groupId);
        if (associations.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> processIds = associations.stream()
                .map(DataPermissionGroupProcess::getProcessId)
                .collect(Collectors.toList());

        Map<Long, Process> processMap = processIds.stream()
                .map(processRepository::findById)
                .filter(opt -> opt.isPresent())
                .map(opt -> opt.get())
                .collect(Collectors.toMap(Process::getId, Function.identity()));

        return associations.stream()
                .map(a -> {
                    Process p = processMap.get(a.getProcessId());
                    if (p != null) {
                        return new AssociatedEntityDto(p.getId(), p.getProcessCode(), p.getName());
                    }
                    return new AssociatedEntityDto(a.getProcessId(), String.valueOf(a.getProcessId()), "未知工序");
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveProcesses(Long groupId, AssociatedItemDto dto) {
        LOG.debug("Saving processes for group: " + groupId);
        findOrThrow(groupId);

        // Full replacement strategy
        repository.deleteProcessAssociationsByGroupId(groupId);

        List<Long> newIds = dto.getIds() != null ? dto.getIds() : new ArrayList<>();
        List<DataPermissionGroupProcess> newAssociations = newIds.stream()
                .map(processId -> new DataPermissionGroupProcess(groupId, processId))
                .collect(Collectors.toList());

        if (!newAssociations.isEmpty()) {
            repository.saveProcessAssociationsBatch(newAssociations);
        }

        LOG.infof("Saved processes for group %d: %d associations", groupId, newIds.size());
    }

    private DataPermissionGroup findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "数据权限组不存在: " + id));
    }

    private DataPermissionGroupDto toDto(DataPermissionGroup entity) {
        DataPermissionGroupDto dto = new DataPermissionGroupDto();
        dto.setId(entity.getId());
        dto.setGroupName(entity.getGroupName());
        dto.setRemark(entity.getRemark());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedBy(entity.getUpdatedBy());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
