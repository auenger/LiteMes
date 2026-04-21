package com.litemes.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litemes.domain.entity.Process;
import com.litemes.domain.entity.WorkCenter;
import com.litemes.domain.event.BusinessException;
import com.litemes.domain.repository.FactoryRepository;
import com.litemes.domain.repository.ProcessRepository;
import com.litemes.domain.repository.WorkCenterRepository;
import com.litemes.web.dto.PagedResult;
import com.litemes.web.dto.ProcessCreateDto;
import com.litemes.web.dto.ProcessDto;
import com.litemes.web.dto.ProcessQueryDto;
import com.litemes.web.dto.ProcessUpdateDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Application service for Process CRUD operations.
 * Handles DTO <-> Entity conversion, business validation, and orchestration.
 */
@ApplicationScoped
public class ProcessService {

    private static final Logger LOG = Logger.getLogger(ProcessService.class);

    @Inject
    ProcessRepository processRepository;

    @Inject
    WorkCenterRepository workCenterRepository;

    @Inject
    FactoryRepository factoryRepository;

    @Transactional
    public Long create(ProcessCreateDto dto) {
        LOG.debugf("Creating process: %s", dto.getProcessCode());

        // Validate work center exists
        WorkCenter workCenter = workCenterRepository.findById(dto.getWorkCenterId())
                .orElseThrow(() -> new BusinessException("WORK_CENTER_NOT_FOUND", "所属工作中心不存在"));

        // Check uniqueness of process code
        if (processRepository.existsByProcessCode(dto.getProcessCode())) {
            throw new BusinessException("PROCESS_CODE_DUPLICATE", "工序编码已存在");
        }

        Process entity = new Process(dto.getProcessCode(), dto.getName(), dto.getWorkCenterId());
        processRepository.save(entity);

        LOG.infof("Created process with id: %d, code: %s", entity.getId(), entity.getProcessCode());
        return entity.getId();
    }

    public ProcessDto getById(Long id) {
        LOG.debugf("Getting process by id: %d", id);
        Process entity = findOrThrow(id);
        return toDto(entity);
    }

    public PagedResult<ProcessDto> list(ProcessQueryDto query) {
        LOG.debugf("Listing processes with query: code=%s, name=%s, workCenterId=%s, factoryId=%s, status=%s",
                query.getProcessCode(), query.getName(), query.getWorkCenterId(), query.getFactoryId(), query.getStatus());

        IPage<Process> page = new Page<>(query.getPage(), query.getSize());
        IPage<Process> result = processRepository.findPage(
                page, query.getProcessCode(), query.getName(), query.getWorkCenterId(), query.getFactoryId(), query.getStatus());

        List<ProcessDto> records = result.getRecords().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new PagedResult<>(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Transactional
    public void update(Long id, ProcessUpdateDto dto) {
        LOG.debugf("Updating process: %d", id);
        Process entity = findOrThrow(id);

        // Process code is immutable - not updated
        // Work center is immutable - not updated
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }

        processRepository.update(entity);
        LOG.infof("Updated process: %d", id);
    }

    @Transactional
    public void delete(Long id) {
        LOG.debugf("Deleting process: %d", id);
        Process entity = findOrThrow(id);

        // Reference check: DataPermissionGroupProcess / UserDataPermissionProcess
        // Note: Data permission entities do not exist yet, placeholder for future check
        // TODO: Check if any DataPermission references this process before deleting

        processRepository.deleteById(id);
        LOG.infof("Deleted process: %d", id);
    }

    @Transactional
    public void updateStatus(Long id, Integer status) {
        LOG.debugf("Updating process status: id=%d, status=%d", id, status);
        Process entity = findOrThrow(id);

        if (entity.getStatus().equals(status)) {
            throw new BusinessException("STATUS_UNCHANGED", "工序状态未发生变化");
        }

        entity.setStatus(status);
        processRepository.update(entity);
        LOG.infof("Updated process status: id=%d, status=%d", id, status);
    }

    private Process findOrThrow(Long id) {
        return processRepository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "工序不存在: " + id));
    }

    private ProcessDto toDto(Process entity) {
        ProcessDto dto = new ProcessDto();
        dto.setId(entity.getId());
        dto.setProcessCode(entity.getProcessCode());
        dto.setName(entity.getName());
        dto.setWorkCenterId(entity.getWorkCenterId());
        dto.setStatus(entity.getStatus());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedBy(entity.getUpdatedBy());
        dto.setUpdatedAt(entity.getUpdatedAt());

        // Enrich with work center name and factory info
        workCenterRepository.findById(entity.getWorkCenterId()).ifPresent(wc -> {
            dto.setWorkCenterName(wc.getName());
            dto.setFactoryId(wc.getFactoryId());

            // Enrich with factory name
            factoryRepository.findById(wc.getFactoryId()).ifPresent(factory -> {
                dto.setFactoryName(factory.getName());
            });
        });

        return dto;
    }
}
