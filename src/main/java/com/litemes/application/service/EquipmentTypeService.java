package com.litemes.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litemes.domain.entity.EquipmentType;
import com.litemes.domain.event.BusinessException;
import com.litemes.domain.repository.EquipmentTypeRepository;
import com.litemes.web.dto.EquipmentTypeCreateDto;
import com.litemes.web.dto.EquipmentTypeDto;
import com.litemes.web.dto.EquipmentTypeQueryDto;
import com.litemes.web.dto.EquipmentTypeUpdateDto;
import com.litemes.web.dto.PagedResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Application service for EquipmentType CRUD operations.
 * Handles DTO <-> Entity conversion, business validation, and orchestration.
 */
@ApplicationScoped
public class EquipmentTypeService {

    private static final Logger LOG = Logger.getLogger(EquipmentTypeService.class);

    @Inject
    EquipmentTypeRepository equipmentTypeRepository;

    @Transactional
    public Long create(EquipmentTypeCreateDto dto) {
        LOG.debugf("Creating equipment type: %s", dto.getTypeCode());

        // Check uniqueness of type code
        if (equipmentTypeRepository.existsByTypeCode(dto.getTypeCode())) {
            throw new BusinessException("EQUIPMENT_TYPE_CODE_DUPLICATE", "设备类型编码已存在");
        }

        EquipmentType entity = new EquipmentType(dto.getTypeCode(), dto.getTypeName());
        equipmentTypeRepository.save(entity);

        LOG.infof("Created equipment type with id: %d, code: %s", entity.getId(), entity.getTypeCode());
        return entity.getId();
    }

    public EquipmentTypeDto getById(Long id) {
        LOG.debugf("Getting equipment type by id: %d", id);
        EquipmentType entity = findOrThrow(id);
        return toDto(entity);
    }

    public PagedResult<EquipmentTypeDto> list(EquipmentTypeQueryDto query) {
        LOG.debugf("Listing equipment types with query: code=%s, name=%s, status=%s",
                query.getTypeCode(), query.getTypeName(), query.getStatus());

        IPage<EquipmentType> page = new Page<>(query.getPage(), query.getSize());
        IPage<EquipmentType> result = equipmentTypeRepository.findPage(
                page, query.getTypeCode(), query.getTypeName(), query.getStatus());

        List<EquipmentTypeDto> records = result.getRecords().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new PagedResult<>(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Transactional
    public void update(Long id, EquipmentTypeUpdateDto dto) {
        LOG.debugf("Updating equipment type: %d", id);
        EquipmentType entity = findOrThrow(id);

        // Type code is immutable - not updated

        if (dto.getTypeName() != null && !dto.getTypeName().equals(entity.getTypeName())) {
            entity.setTypeName(dto.getTypeName());
        }

        equipmentTypeRepository.update(entity);
        LOG.infof("Updated equipment type: %d", id);
    }

    @Transactional
    public void delete(Long id) {
        LOG.debugf("Deleting equipment type: %d", id);
        EquipmentType entity = findOrThrow(id);

        // Reference check: equipment_model table references equipment_type_id
        // Note: equipment_model table does not exist yet (feat-equipment-model not implemented),
        // so for now we just perform the soft delete.
        // When feat-equipment-model is implemented, add reference check here:
        // if (equipmentModelRepository.existsByEquipmentTypeId(id)) {
        //     throw new BusinessException("EQUIPMENT_TYPE_REFERENCED", "该设备类型已被设备型号引用，不可删除");
        // }

        equipmentTypeRepository.deleteById(id);
        LOG.infof("Deleted equipment type: %d", id);
    }

    @Transactional
    public void updateStatus(Long id, Integer status) {
        LOG.debugf("Updating equipment type status: id=%d, status=%d", id, status);
        EquipmentType entity = findOrThrow(id);

        if (entity.getStatus().equals(status)) {
            throw new BusinessException("STATUS_UNCHANGED", "设备类型状态未发生变化");
        }

        entity.setStatus(status);
        equipmentTypeRepository.update(entity);
        LOG.infof("Updated equipment type status: id=%d, status=%d", id, status);
    }

    public List<EquipmentTypeDto> listAllActive() {
        LOG.debug("Listing all active equipment types");
        return equipmentTypeRepository.findAllActive().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private EquipmentType findOrThrow(Long id) {
        return equipmentTypeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "设备类型不存在: " + id));
    }

    private EquipmentTypeDto toDto(EquipmentType entity) {
        EquipmentTypeDto dto = new EquipmentTypeDto();
        dto.setId(entity.getId());
        dto.setTypeCode(entity.getTypeCode());
        dto.setTypeName(entity.getTypeName());
        dto.setStatus(entity.getStatus());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedBy(entity.getUpdatedBy());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
