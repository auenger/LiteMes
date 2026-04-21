package com.litemes.application.service;

import com.litemes.domain.entity.Shift;
import com.litemes.domain.entity.ShiftSchedule;
import com.litemes.domain.event.BusinessException;
import com.litemes.domain.repository.ShiftRepository;
import com.litemes.domain.repository.ShiftScheduleRepository;
import com.litemes.web.dto.ShiftCreateDto;
import com.litemes.web.dto.ShiftDto;
import com.litemes.web.dto.ShiftUpdateDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Application service for Shift CRUD operations.
 * Handles DTO <-> Entity conversion, cross-day logic, and work hour calculation.
 */
@ApplicationScoped
public class ShiftService {

    private static final Logger LOG = Logger.getLogger(ShiftService.class);

    @Inject
    ShiftRepository shiftRepository;

    @Inject
    ShiftScheduleRepository shiftScheduleRepository;

    @Transactional
    public Long create(ShiftCreateDto dto) {
        LOG.debugf("Creating shift: %s under schedule %d", dto.getShiftCode(), dto.getShiftScheduleId());

        // Validate shift schedule exists
        ShiftSchedule schedule = shiftScheduleRepository.findById(dto.getShiftScheduleId())
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "班制不存在: " + dto.getShiftScheduleId()));

        // Uniqueness check for shift code
        if (shiftRepository.findByShiftCode(dto.getShiftCode()).isPresent()) {
            throw new BusinessException("DUPLICATE_CODE", "班次编码已存在: " + dto.getShiftCode());
        }

        Shift entity = new Shift();
        entity.setShiftScheduleId(dto.getShiftScheduleId());
        entity.setShiftCode(dto.getShiftCode());
        entity.setName(dto.getName());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());

        // Auto-detect cross-day if not specified
        if (dto.getCrossDay() != null) {
            entity.setCrossDay(dto.getCrossDay());
        } else {
            entity.setCrossDay(autoDetectCrossDay(dto.getStartTime(), dto.getEndTime()));
        }

        entity.setStatus(1); // Default enabled
        shiftRepository.save(entity);

        LOG.infof("Created shift with id: %d, code: %s, crossDay: %d",
                entity.getId(), entity.getShiftCode(), entity.getCrossDay());
        return entity.getId();
    }

    public ShiftDto getById(Long id) {
        LOG.debugf("Getting shift by id: %d", id);
        Shift entity = shiftRepository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "班次不存在: " + id));
        return toDto(entity);
    }

    public List<ShiftDto> listByScheduleId(Long shiftScheduleId) {
        LOG.debugf("Listing shifts for schedule: %d", shiftScheduleId);
        // Validate schedule exists
        shiftScheduleRepository.findById(shiftScheduleId)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "班制不存在: " + shiftScheduleId));
        return shiftRepository.findByShiftScheduleId(shiftScheduleId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void update(Long id, ShiftUpdateDto dto) {
        LOG.debugf("Updating shift: %d", id);
        Shift entity = shiftRepository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "班次不存在: " + id));

        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getStartTime() != null) {
            entity.setStartTime(dto.getStartTime());
        }
        if (dto.getEndTime() != null) {
            entity.setEndTime(dto.getEndTime());
        }
        if (dto.getCrossDay() != null) {
            entity.setCrossDay(dto.getCrossDay());
        }
        // Re-detect cross-day when both times are updated and crossDay not specified
        if (dto.getStartTime() != null && dto.getEndTime() != null && dto.getCrossDay() == null) {
            entity.setCrossDay(autoDetectCrossDay(dto.getStartTime(), dto.getEndTime()));
        }
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }

        shiftRepository.update(entity);
        LOG.infof("Updated shift: %d", id);
    }

    @Transactional
    public void delete(Long id) {
        LOG.debugf("Deleting shift: %d", id);
        if (shiftRepository.findById(id).isEmpty()) {
            throw new BusinessException("NOT_FOUND", "班次不存在: " + id);
        }
        shiftRepository.deleteById(id);
        LOG.infof("Deleted shift: %d", id);
    }

    /**
     * Auto-detect if a shift crosses midnight.
     * If end time is before or equal to start time, it's a cross-day shift.
     */
    private Integer autoDetectCrossDay(java.time.LocalTime startTime, java.time.LocalTime endTime) {
        if (startTime != null && endTime != null && !endTime.isAfter(startTime)) {
            return 1;
        }
        return 0;
    }

    private ShiftDto toDto(Shift entity) {
        ShiftDto dto = new ShiftDto();
        dto.setId(entity.getId());
        dto.setShiftScheduleId(entity.getShiftScheduleId());
        dto.setShiftCode(entity.getShiftCode());
        dto.setName(entity.getName());
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        dto.setCrossDay(entity.getCrossDay());
        dto.setStatus(entity.getStatus());
        dto.setWorkHours(entity.calculateWorkHours());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedBy(entity.getUpdatedBy());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
