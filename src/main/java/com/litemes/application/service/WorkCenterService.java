package com.litemes.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litemes.domain.entity.Factory;
import com.litemes.domain.entity.WorkCenter;
import com.litemes.domain.event.BusinessException;
import com.litemes.domain.repository.FactoryRepository;
import com.litemes.domain.repository.WorkCenterRepository;
import com.litemes.web.dto.PagedResult;
import com.litemes.web.dto.WorkCenterCreateDto;
import com.litemes.web.dto.WorkCenterDto;
import com.litemes.web.dto.WorkCenterQueryDto;
import com.litemes.web.dto.WorkCenterUpdateDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Application service for WorkCenter CRUD operations.
 * Handles DTO <-> Entity conversion, business validation, and orchestration.
 */
@ApplicationScoped
public class WorkCenterService {

    private static final Logger LOG = Logger.getLogger(WorkCenterService.class);

    @Inject
    WorkCenterRepository workCenterRepository;

    @Inject
    FactoryRepository factoryRepository;

    @Transactional
    public Long create(WorkCenterCreateDto dto) {
        LOG.debugf("Creating work center: %s", dto.getWorkCenterCode());

        // Validate factory exists
        Factory factory = factoryRepository.findById(dto.getFactoryId())
                .orElseThrow(() -> new BusinessException("FACTORY_NOT_FOUND", "所属工厂不存在"));

        // Check uniqueness of work center code
        if (workCenterRepository.existsByWorkCenterCode(dto.getWorkCenterCode())) {
            throw new BusinessException("WORK_CENTER_CODE_DUPLICATE", "工作中心编码已存在");
        }

        WorkCenter entity = new WorkCenter(dto.getWorkCenterCode(), dto.getName(), dto.getFactoryId());
        workCenterRepository.save(entity);

        LOG.infof("Created work center with id: %d, code: %s", entity.getId(), entity.getWorkCenterCode());
        return entity.getId();
    }

    public WorkCenterDto getById(Long id) {
        LOG.debugf("Getting work center by id: %d", id);
        WorkCenter entity = findOrThrow(id);
        return toDto(entity);
    }

    public PagedResult<WorkCenterDto> list(WorkCenterQueryDto query) {
        LOG.debugf("Listing work centers with query: code=%s, name=%s, factoryId=%s, status=%s",
                query.getWorkCenterCode(), query.getName(), query.getFactoryId(), query.getStatus());

        IPage<WorkCenter> page = new Page<>(query.getPage(), query.getSize());
        IPage<WorkCenter> result = workCenterRepository.findPage(
                page, query.getWorkCenterCode(), query.getName(), query.getFactoryId(), query.getStatus());

        List<WorkCenterDto> records = result.getRecords().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new PagedResult<>(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Transactional
    public void update(Long id, WorkCenterUpdateDto dto) {
        LOG.debugf("Updating work center: %d", id);
        WorkCenter entity = findOrThrow(id);

        // Work center code is immutable - not updated
        // Factory is immutable - not updated
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }

        workCenterRepository.update(entity);
        LOG.infof("Updated work center: %d", id);
    }

    @Transactional
    public void delete(Long id) {
        LOG.debugf("Deleting work center: %d", id);
        WorkCenter entity = findOrThrow(id);

        // Reference check: Process.work_center_id
        // Note: Process entity does not exist yet, placeholder for future check
        // TODO: Check if any Process references this work center before deleting

        // Reference check: DataPermissionGroupWorkCenter / UserDataPermissionWorkCenter
        // Note: Data permission entities do not exist yet, placeholder for future check
        // TODO: Check if any DataPermission references this work center before deleting

        workCenterRepository.deleteById(id);
        LOG.infof("Deleted work center: %d", id);
    }

    @Transactional
    public void updateStatus(Long id, Integer status) {
        LOG.debugf("Updating work center status: id=%d, status=%d", id, status);
        WorkCenter entity = findOrThrow(id);

        if (entity.getStatus().equals(status)) {
            throw new BusinessException("STATUS_UNCHANGED", "工作中心状态未发生变化");
        }

        entity.setStatus(status);
        workCenterRepository.update(entity);
        LOG.infof("Updated work center status: id=%d, status=%d", id, status);
    }

    private WorkCenter findOrThrow(Long id) {
        return workCenterRepository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "工作中心不存在: " + id));
    }

    private WorkCenterDto toDto(WorkCenter entity) {
        WorkCenterDto dto = new WorkCenterDto();
        dto.setId(entity.getId());
        dto.setWorkCenterCode(entity.getWorkCenterCode());
        dto.setName(entity.getName());
        dto.setFactoryId(entity.getFactoryId());
        dto.setStatus(entity.getStatus());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedBy(entity.getUpdatedBy());
        dto.setUpdatedAt(entity.getUpdatedAt());

        // Enrich with factory name
        factoryRepository.findById(entity.getFactoryId()).ifPresent(factory -> {
            dto.setFactoryName(factory.getName());
        });

        return dto;
    }
}
