package com.litemes.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litemes.domain.entity.EquipmentLedger;
import com.litemes.domain.entity.EquipmentModel;
import com.litemes.domain.entity.Factory;
import com.litemes.domain.event.BusinessException;
import com.litemes.domain.repository.EquipmentLedgerRepository;
import com.litemes.domain.repository.EquipmentModelRepository;
import com.litemes.domain.repository.FactoryRepository;
import com.litemes.web.dto.EquipmentLedgerCreateDto;
import com.litemes.web.dto.EquipmentLedgerDto;
import com.litemes.web.dto.EquipmentLedgerQueryDto;
import com.litemes.web.dto.EquipmentLedgerUpdateDto;
import com.litemes.web.dto.PagedResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Application service for EquipmentLedger CRUD operations.
 * Handles DTO <-> Entity conversion, business validation, and orchestration.
 * Validates equipment code uniqueness, model/factory existence,
 * and auto-fills redundant type/model/factory fields.
 */
@ApplicationScoped
public class EquipmentLedgerService {

    private static final Logger LOG = Logger.getLogger(EquipmentLedgerService.class);

    @Inject
    EquipmentLedgerRepository equipmentLedgerRepository;

    @Inject
    EquipmentModelRepository equipmentModelRepository;

    @Inject
    FactoryRepository factoryRepository;

    @Inject
    AuditLogService auditLogService;

    @Transactional
    public Long create(EquipmentLedgerCreateDto dto) {
        LOG.debugf("Creating equipment ledger: %s", dto.getEquipmentCode());

        // Check uniqueness of equipment code
        if (equipmentLedgerRepository.existsByEquipmentCode(dto.getEquipmentCode())) {
            throw new BusinessException("EQUIPMENT_CODE_DUPLICATE", "设备编码已存在");
        }

        // Validate equipment model exists and auto-fill model/type redundant fields
        EquipmentModel model = equipmentModelRepository.findById(dto.getEquipmentModelId())
                .orElseThrow(() -> new BusinessException("EQUIPMENT_MODEL_NOT_FOUND", "设备型号不存在"));

        // Validate factory exists and auto-fill factory redundant fields
        Factory factory = factoryRepository.findById(dto.getFactoryId())
                .orElseThrow(() -> new BusinessException("FACTORY_NOT_FOUND", "工厂不存在"));

        EquipmentLedger entity = new EquipmentLedger(
                dto.getEquipmentCode(),
                dto.getEquipmentName(),
                model.getId(),
                model.getModelCode(),
                model.getModelName(),
                model.getEquipmentTypeId(),
                model.getTypeCode(),
                model.getTypeName(),
                dto.getRunningStatus(),
                dto.getManageStatus(),
                factory.getId(),
                factory.getFactoryCode(),
                factory.getName(),
                dto.getCommissioningDate()
        );
        entity.setManufacturer(dto.getManufacturer());

        equipmentLedgerRepository.save(entity);

        // Record audit log
        auditLogService.logCreate("equipment_ledger", entity.getId(), toDto(entity), null);

        LOG.infof("Created equipment ledger with id: %d, code: %s", entity.getId(), entity.getEquipmentCode());
        return entity.getId();
    }

    public EquipmentLedgerDto getById(Long id) {
        LOG.debugf("Getting equipment ledger by id: %d", id);
        EquipmentLedger entity = findOrThrow(id);
        return toDto(entity);
    }

    public PagedResult<EquipmentLedgerDto> list(EquipmentLedgerQueryDto query) {
        LOG.debugf("Listing equipment ledgers with query: code=%s, name=%s, typeId=%s, modelId=%s, running=%s, manage=%s, factory=%s",
                query.getEquipmentCode(), query.getEquipmentName(), query.getEquipmentTypeId(),
                query.getEquipmentModelId(), query.getRunningStatus(), query.getManageStatus(), query.getFactoryId());

        IPage<EquipmentLedger> page = new Page<>(query.getPage(), query.getSize());
        IPage<EquipmentLedger> result = equipmentLedgerRepository.findPage(
                page, query.getEquipmentCode(), query.getEquipmentName(),
                query.getEquipmentTypeId(), query.getEquipmentModelId(),
                query.getRunningStatus(), query.getManageStatus(),
                query.getFactoryId(), query.getStatus());

        List<EquipmentLedgerDto> records = result.getRecords().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new PagedResult<>(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Transactional
    public void update(Long id, EquipmentLedgerUpdateDto dto) {
        LOG.debugf("Updating equipment ledger: %d", id);
        EquipmentLedger entity = findOrThrow(id);
        EquipmentLedgerDto oldValue = toDto(entity);

        // Equipment code is immutable - not updated

        if (dto.getEquipmentName() != null && !dto.getEquipmentName().equals(entity.getEquipmentName())) {
            entity.setEquipmentName(dto.getEquipmentName());
        }

        // If equipment model changed, update model and type redundant fields
        if (dto.getEquipmentModelId() != null && !dto.getEquipmentModelId().equals(entity.getEquipmentModelId())) {
            EquipmentModel model = equipmentModelRepository.findById(dto.getEquipmentModelId())
                    .orElseThrow(() -> new BusinessException("EQUIPMENT_MODEL_NOT_FOUND", "设备型号不存在"));
            entity.setEquipmentModelId(model.getId());
            entity.setModelCode(model.getModelCode());
            entity.setModelName(model.getModelName());
            entity.setEquipmentTypeId(model.getEquipmentTypeId());
            entity.setTypeCode(model.getTypeCode());
            entity.setTypeName(model.getTypeName());
        }

        // If factory changed, update factory redundant fields
        if (dto.getFactoryId() != null && !dto.getFactoryId().equals(entity.getFactoryId())) {
            Factory factory = factoryRepository.findById(dto.getFactoryId())
                    .orElseThrow(() -> new BusinessException("FACTORY_NOT_FOUND", "工厂不存在"));
            entity.setFactoryId(factory.getId());
            entity.setFactoryCode(factory.getFactoryCode());
            entity.setFactoryName(factory.getName());
        }

        // Update running status
        if (dto.getRunningStatus() != null) {
            entity.setRunningStatus(dto.getRunningStatus());
        }

        // Update manage status
        if (dto.getManageStatus() != null) {
            entity.setManageStatus(dto.getManageStatus());
        }

        // Update manufacturer
        if (dto.getManufacturer() != null) {
            entity.setManufacturer(dto.getManufacturer());
        }

        // Update commissioning date
        if (dto.getCommissioningDate() != null) {
            entity.setCommissioningDate(dto.getCommissioningDate());
        }

        equipmentLedgerRepository.update(entity);

        // Record audit log
        auditLogService.logUpdate("equipment_ledger", entity.getId(), oldValue, toDto(entity), null);

        LOG.infof("Updated equipment ledger: %d", id);
    }

    @Transactional
    public void delete(Long id) {
        LOG.debugf("Deleting equipment ledger: %d", id);
        EquipmentLedger entity = findOrThrow(id);
        EquipmentLedgerDto oldValue = toDto(entity);

        // Reference check: equipment ledger may be referenced by work orders, maintenance records, etc.
        // For now, soft delete is sufficient. Future features will add reference checks.

        equipmentLedgerRepository.deleteById(id);

        // Record audit log
        auditLogService.logDelete("equipment_ledger", entity.getId(), oldValue, null);

        LOG.infof("Deleted equipment ledger: %d", id);
    }

    @Transactional
    public void updateStatus(Long id, Integer status) {
        LOG.debugf("Updating equipment ledger status: id=%d, status=%d", id, status);
        EquipmentLedger entity = findOrThrow(id);

        if (entity.getStatus().equals(status)) {
            throw new BusinessException("STATUS_UNCHANGED", "设备台账状态未发生变化");
        }

        EquipmentLedgerDto oldValue = toDto(entity);
        entity.setStatus(status);
        equipmentLedgerRepository.update(entity);

        // Record audit log
        auditLogService.logUpdate("equipment_ledger", entity.getId(), oldValue, toDto(entity), null);

        LOG.infof("Updated equipment ledger status: id=%d, status=%d", id, status);
    }

    private EquipmentLedger findOrThrow(Long id) {
        return equipmentLedgerRepository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "设备台账不存在: " + id));
    }

    private EquipmentLedgerDto toDto(EquipmentLedger entity) {
        EquipmentLedgerDto dto = new EquipmentLedgerDto();
        dto.setId(entity.getId());
        dto.setEquipmentCode(entity.getEquipmentCode());
        dto.setEquipmentName(entity.getEquipmentName());
        dto.setEquipmentModelId(entity.getEquipmentModelId());
        dto.setModelCode(entity.getModelCode());
        dto.setModelName(entity.getModelName());
        dto.setEquipmentTypeId(entity.getEquipmentTypeId());
        dto.setTypeCode(entity.getTypeCode());
        dto.setTypeName(entity.getTypeName());
        dto.setRunningStatus(entity.getRunningStatus());
        dto.setManageStatus(entity.getManageStatus());
        dto.setFactoryId(entity.getFactoryId());
        dto.setFactoryCode(entity.getFactoryCode());
        dto.setFactoryName(entity.getFactoryName());
        dto.setManufacturer(entity.getManufacturer());
        dto.setCommissioningDate(entity.getCommissioningDate());
        dto.setStatus(entity.getStatus());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedBy(entity.getUpdatedBy());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
