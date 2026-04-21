package com.litemes.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litemes.domain.entity.EquipmentModel;
import com.litemes.domain.entity.EquipmentType;
import com.litemes.domain.event.BusinessException;
import com.litemes.domain.repository.EquipmentModelRepository;
import com.litemes.domain.repository.EquipmentTypeRepository;
import com.litemes.web.dto.EquipmentModelCreateDto;
import com.litemes.web.dto.EquipmentModelDto;
import com.litemes.web.dto.EquipmentModelQueryDto;
import com.litemes.web.dto.EquipmentModelUpdateDto;
import com.litemes.web.dto.PagedResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Application service for EquipmentModel CRUD operations.
 * Handles DTO <-> Entity conversion, business validation, and orchestration.
 */
@ApplicationScoped
public class EquipmentModelService {

    private static final Logger LOG = Logger.getLogger(EquipmentModelService.class);

    @Inject
    EquipmentModelRepository equipmentModelRepository;

    @Inject
    EquipmentTypeRepository equipmentTypeRepository;

    @Transactional
    public Long create(EquipmentModelCreateDto dto) {
        LOG.debugf("Creating equipment model: %s", dto.getModelCode());

        // Check uniqueness of model code
        if (equipmentModelRepository.existsByModelCode(dto.getModelCode())) {
            throw new BusinessException("EQUIPMENT_MODEL_CODE_DUPLICATE", "设备型号编码已存在");
        }

        // Validate equipment type exists and fill redundant fields
        EquipmentType type = equipmentTypeRepository.findById(dto.getEquipmentTypeId())
                .orElseThrow(() -> new BusinessException("EQUIPMENT_TYPE_NOT_FOUND", "设备类型不存在"));

        EquipmentModel entity = new EquipmentModel(
                dto.getModelCode(),
                dto.getModelName(),
                dto.getEquipmentTypeId(),
                type.getTypeCode(),
                type.getTypeName()
        );
        equipmentModelRepository.save(entity);

        LOG.infof("Created equipment model with id: %d, code: %s", entity.getId(), entity.getModelCode());
        return entity.getId();
    }

    public EquipmentModelDto getById(Long id) {
        LOG.debugf("Getting equipment model by id: %d", id);
        EquipmentModel entity = findOrThrow(id);
        return toDto(entity);
    }

    public PagedResult<EquipmentModelDto> list(EquipmentModelQueryDto query) {
        LOG.debugf("Listing equipment models with query: code=%s, name=%s, typeId=%s, status=%s",
                query.getModelCode(), query.getModelName(), query.getEquipmentTypeId(), query.getStatus());

        IPage<EquipmentModel> page = new Page<>(query.getPage(), query.getSize());
        IPage<EquipmentModel> result = equipmentModelRepository.findPage(
                page, query.getModelCode(), query.getModelName(), query.getEquipmentTypeId(), query.getStatus());

        List<EquipmentModelDto> records = result.getRecords().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new PagedResult<>(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Transactional
    public void update(Long id, EquipmentModelUpdateDto dto) {
        LOG.debugf("Updating equipment model: %d", id);
        EquipmentModel entity = findOrThrow(id);

        // Model code is immutable - not updated

        if (dto.getModelName() != null && !dto.getModelName().equals(entity.getModelName())) {
            entity.setModelName(dto.getModelName());
        }

        // If equipment type changed, update redundant fields
        if (dto.getEquipmentTypeId() != null && !dto.getEquipmentTypeId().equals(entity.getEquipmentTypeId())) {
            EquipmentType type = equipmentTypeRepository.findById(dto.getEquipmentTypeId())
                    .orElseThrow(() -> new BusinessException("EQUIPMENT_TYPE_NOT_FOUND", "设备类型不存在"));
            entity.setEquipmentTypeId(dto.getEquipmentTypeId());
            entity.setTypeCode(type.getTypeCode());
            entity.setTypeName(type.getTypeName());
        }

        equipmentModelRepository.update(entity);
        LOG.infof("Updated equipment model: %d", id);
    }

    @Transactional
    public void delete(Long id) {
        LOG.debugf("Deleting equipment model: %d", id);
        EquipmentModel entity = findOrThrow(id);

        // Reference check: equipment_model may be referenced by equipment_ledger (future feature)
        // When feat-equipment-ledger is implemented, add reference check here:
        // if (equipmentLedgerRepository.existsByEquipmentModelId(id)) {
        //     throw new BusinessException("EQUIPMENT_MODEL_REFERENCED", "该设备型号已被设备台账引用，不可删除");
        // }

        equipmentModelRepository.deleteById(id);
        LOG.infof("Deleted equipment model: %d", id);
    }

    @Transactional
    public void updateStatus(Long id, Integer status) {
        LOG.debugf("Updating equipment model status: id=%d, status=%d", id, status);
        EquipmentModel entity = findOrThrow(id);

        if (entity.getStatus().equals(status)) {
            throw new BusinessException("STATUS_UNCHANGED", "设备型号状态未发生变化");
        }

        entity.setStatus(status);
        equipmentModelRepository.update(entity);
        LOG.infof("Updated equipment model status: id=%d, status=%d", id, status);
    }

    private EquipmentModel findOrThrow(Long id) {
        return equipmentModelRepository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "设备型号不存在: " + id));
    }

    private EquipmentModelDto toDto(EquipmentModel entity) {
        EquipmentModelDto dto = new EquipmentModelDto();
        dto.setId(entity.getId());
        dto.setModelCode(entity.getModelCode());
        dto.setModelName(entity.getModelName());
        dto.setEquipmentTypeId(entity.getEquipmentTypeId());
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
