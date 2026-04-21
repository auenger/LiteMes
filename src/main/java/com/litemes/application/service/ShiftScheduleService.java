package com.litemes.application.service;

import com.litemes.domain.entity.ShiftSchedule;
import com.litemes.domain.event.BusinessException;
import com.litemes.domain.repository.ShiftRepository;
import com.litemes.domain.repository.ShiftScheduleRepository;
import com.litemes.web.dto.PageDto;
import com.litemes.web.dto.ShiftScheduleCreateDto;
import com.litemes.web.dto.ShiftScheduleDto;
import com.litemes.web.dto.ShiftScheduleUpdateDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Application service for ShiftSchedule CRUD operations.
 * Handles DTO <-> Entity conversion, uniqueness validation, and default schedule logic.
 */
@ApplicationScoped
public class ShiftScheduleService {

    private static final Logger LOG = Logger.getLogger(ShiftScheduleService.class);

    @Inject
    ShiftScheduleRepository shiftScheduleRepository;

    @Inject
    ShiftRepository shiftRepository;

    @Transactional
    public Long create(ShiftScheduleCreateDto dto) {
        LOG.debugf("Creating shift schedule: %s", dto.getShiftCode());

        // Uniqueness check
        if (shiftScheduleRepository.findByShiftCode(dto.getShiftCode()).isPresent()) {
            throw new BusinessException("DUPLICATE_CODE", "班制编码已存在: " + dto.getShiftCode());
        }

        // Default schedule logic: only one default at a time
        if (dto.getIsDefault() != null && dto.getIsDefault() == 1) {
            shiftScheduleRepository.clearDefault();
        }

        ShiftSchedule entity = new ShiftSchedule();
        entity.setShiftCode(dto.getShiftCode());
        entity.setName(dto.getName());
        entity.setIsDefault(dto.getIsDefault() != null ? dto.getIsDefault() : 0);
        entity.setStatus(1); // Default enabled
        shiftScheduleRepository.save(entity);

        LOG.infof("Created shift schedule with id: %d, code: %s", entity.getId(), entity.getShiftCode());
        return entity.getId();
    }

    public ShiftScheduleDto getById(Long id) {
        LOG.debugf("Getting shift schedule by id: %d", id);
        ShiftSchedule entity = shiftScheduleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "班制不存在: " + id));
        return toDto(entity);
    }

    public PageDto<ShiftScheduleDto> list(String shiftCode, String name, Integer status, int pageNum, int pageSize) {
        LOG.debugf("Listing shift schedules: code=%s, name=%s, status=%d", shiftCode, name, status);
        List<ShiftSchedule> records = shiftScheduleRepository.findByCondition(shiftCode, name, status, pageNum, pageSize);
        long total = shiftScheduleRepository.countByCondition(shiftCode, name, status);
        List<ShiftScheduleDto> dtos = records.stream().map(this::toDto).collect(Collectors.toList());
        return new PageDto<>(dtos, total, pageNum, pageSize);
    }

    public List<ShiftScheduleDto> listAll() {
        LOG.debug("Listing all shift schedules");
        return shiftScheduleRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public void update(Long id, ShiftScheduleUpdateDto dto) {
        LOG.debugf("Updating shift schedule: %d", id);
        ShiftSchedule entity = shiftScheduleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "班制不存在: " + id));

        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }

        // Default schedule logic
        if (dto.getIsDefault() != null && dto.getIsDefault() == 1) {
            shiftScheduleRepository.clearDefault();
            entity.setIsDefault(1);
        } else if (dto.getIsDefault() != null) {
            entity.setIsDefault(dto.getIsDefault());
        }

        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }

        shiftScheduleRepository.update(entity);
        LOG.infof("Updated shift schedule: %d", id);
    }

    @Transactional
    public void delete(Long id) {
        LOG.debugf("Deleting shift schedule: %d", id);
        ShiftSchedule entity = shiftScheduleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "班制不存在: " + id));

        // Check if shift schedule has shifts referencing it
        if (shiftRepository.existsByShiftScheduleId(id)) {
            throw new BusinessException("HAS_REFERENCE", "班制下存在班次，无法删除: " + entity.getShiftCode());
        }

        shiftScheduleRepository.deleteById(id);
        LOG.infof("Deleted shift schedule: %d", id);
    }

    @Transactional
    public void updateStatus(Long id, Integer status) {
        LOG.debugf("Updating shift schedule status: %d -> %d", id, status);
        ShiftSchedule entity = shiftScheduleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "班制不存在: " + id));
        entity.setStatus(status);
        shiftScheduleRepository.update(entity);
        LOG.infof("Updated shift schedule %d status to %d", id, status);
    }

    private ShiftScheduleDto toDto(ShiftSchedule entity) {
        ShiftScheduleDto dto = new ShiftScheduleDto();
        dto.setId(entity.getId());
        dto.setShiftCode(entity.getShiftCode());
        dto.setName(entity.getName());
        dto.setIsDefault(entity.getIsDefault());
        dto.setStatus(entity.getStatus());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedBy(entity.getUpdatedBy());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
