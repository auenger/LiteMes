package com.litemes.application.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litemes.domain.entity.InspectionExemption;
import com.litemes.domain.entity.MaterialMaster;
import com.litemes.domain.entity.Supplier;
import com.litemes.domain.entity.SupplierMaterial;
import com.litemes.domain.event.BusinessException;
import com.litemes.domain.repository.InspectionExemptionRepository;
import com.litemes.domain.repository.MaterialMasterRepository;
import com.litemes.domain.repository.SupplierMaterialRepository;
import com.litemes.domain.repository.SupplierRepository;
import com.litemes.infrastructure.persistence.mapper.InspectionExemptionMapper;
import com.litemes.web.dto.PagedResult;
import com.litemes.web.dto.SupplierCreateDto;
import com.litemes.web.dto.SupplierDto;
import com.litemes.web.dto.SupplierMaterialDto;
import com.litemes.web.dto.SupplierQueryDto;
import com.litemes.web.dto.SupplierUpdateDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Application service for Supplier CRUD operations.
 * Handles DTO <-> Entity conversion, business validation, and orchestration.
 */
@ApplicationScoped
public class SupplierService {

    private static final Logger LOG = Logger.getLogger(SupplierService.class);

    @Inject
    SupplierRepository supplierRepository;

    @Inject
    SupplierMaterialRepository supplierMaterialRepository;

    @Inject
    MaterialMasterRepository materialMasterRepository;

    @Inject
    InspectionExemptionMapper inspectionExemptionMapper;

    @Inject
    AuditLogService auditLogService;

    @Transactional
    public Long create(SupplierCreateDto dto) {
        LOG.debugf("Creating supplier: %s", dto.getSupplierCode());

        // Check uniqueness of supplier code
        if (supplierRepository.existsBySupplierCode(dto.getSupplierCode())) {
            throw new BusinessException("SUPPLIER_CODE_DUPLICATE", "供应商编码已存在");
        }

        Supplier entity = new Supplier(dto.getSupplierCode(), dto.getSupplierName());
        entity.setShortName(dto.getShortName());
        entity.setContactPerson(dto.getContactPerson());
        entity.setPhone(dto.getPhone());
        entity.setAddress(dto.getAddress());
        entity.setEmail(dto.getEmail());
        entity.setDescription(dto.getDescription());

        supplierRepository.save(entity);

        // Record audit log
        auditLogService.logCreate("supplier", entity.getId(), toDto(entity), null);

        LOG.infof("Created supplier with id: %d, code: %s", entity.getId(), entity.getSupplierCode());
        return entity.getId();
    }

    public SupplierDto getById(Long id) {
        LOG.debugf("Getting supplier by id: %d", id);
        Supplier entity = findOrThrow(id);
        SupplierDto dto = toDto(entity);
        dto.setMaterials(getSupplierMaterials(id));
        return dto;
    }

    public PagedResult<SupplierDto> list(SupplierQueryDto query) {
        LOG.debugf("Listing suppliers with query: code=%s, name=%s, status=%s",
                query.getSupplierCode(), query.getSupplierName(), query.getStatus());

        IPage<Supplier> page = new Page<>(query.getPage(), query.getSize());
        IPage<Supplier> result = supplierRepository.findPage(
                page, query.getSupplierCode(), query.getSupplierName(), query.getStatus());

        List<SupplierDto> records = result.getRecords().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new PagedResult<>(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Transactional
    public void update(Long id, SupplierUpdateDto dto) {
        LOG.debugf("Updating supplier: %d", id);
        Supplier entity = findOrThrow(id);
        SupplierDto oldValue = toDto(entity);

        // Supplier code is immutable - not updated
        if (dto.getSupplierName() != null) {
            entity.setSupplierName(dto.getSupplierName());
        }
        if (dto.getShortName() != null) {
            entity.setShortName(dto.getShortName());
        }
        if (dto.getContactPerson() != null) {
            entity.setContactPerson(dto.getContactPerson());
        }
        if (dto.getPhone() != null) {
            entity.setPhone(dto.getPhone());
        }
        if (dto.getAddress() != null) {
            entity.setAddress(dto.getAddress());
        }
        if (dto.getEmail() != null) {
            entity.setEmail(dto.getEmail());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }

        supplierRepository.update(entity);

        // Record audit log
        auditLogService.logUpdate("supplier", entity.getId(), oldValue, toDto(entity), null);

        LOG.infof("Updated supplier: %d", id);
    }

    @Transactional
    public void delete(Long id) {
        LOG.debugf("Deleting supplier: %d", id);
        Supplier entity = findOrThrow(id);
        SupplierDto oldValue = toDto(entity);

        // Reference check: check if any supplier_material associations exist
        List<SupplierMaterial> materials = supplierMaterialRepository.findBySupplierId(id);
        if (!materials.isEmpty()) {
            throw new BusinessException("SUPPLIER_REFERENCED", "供应商已被物料关联，不可删除");
        }

        // Reference check: check if any inspection_exemption references this supplier
        LambdaQueryWrapper<InspectionExemption> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InspectionExemption::getSupplierId, id);
        long exemptionCount = inspectionExemptionMapper.selectCount(wrapper);
        if (exemptionCount > 0) {
            throw new BusinessException("SUPPLIER_REFERENCED", "供应商已被免检清单引用，不可删除");
        }

        supplierRepository.deleteById(id);

        // Record audit log
        auditLogService.logDelete("supplier", entity.getId(), oldValue, null);

        LOG.infof("Deleted supplier: %d", id);
    }

    @Transactional
    public void updateStatus(Long id, Integer status) {
        LOG.debugf("Updating supplier status: id=%d, status=%d", id, status);
        Supplier entity = findOrThrow(id);

        if (entity.getStatus().equals(status)) {
            throw new BusinessException("STATUS_UNCHANGED", "供应商状态未发生变化");
        }

        SupplierDto oldValue = toDto(entity);
        entity.setStatus(status);
        supplierRepository.update(entity);

        // Record audit log
        auditLogService.logUpdate("supplier", entity.getId(), oldValue, toDto(entity), null);

        LOG.infof("Updated supplier status: id=%d, status=%d", id, status);
    }

    /**
     * Link materials to a supplier. Supports multiple material IDs with duplicate check.
     */
    @Transactional
    public void linkMaterials(Long supplierId, List<Long> materialIds) {
        LOG.debugf("Linking materials to supplier %d: %s", supplierId, materialIds);
        findOrThrow(supplierId);

        for (Long materialId : materialIds) {
            // Validate material exists
            MaterialMaster material = materialMasterRepository.findById(materialId)
                    .orElseThrow(() -> new BusinessException("MATERIAL_NOT_FOUND", "物料不存在: " + materialId));

            // Check duplicate
            if (supplierMaterialRepository.existsBySupplierIdAndMaterialId(supplierId, materialId)) {
                throw new BusinessException("MATERIAL_ALREADY_LINKED",
                        "物料 " + material.getMaterialCode() + " 已关联，不可重复添加");
            }

            SupplierMaterial sm = new SupplierMaterial(supplierId, materialId);
            supplierMaterialRepository.save(sm);
        }

        LOG.infof("Linked %d materials to supplier %d", materialIds.size(), supplierId);
    }

    /**
     * Unlink a material from a supplier.
     */
    @Transactional
    public void unlinkMaterial(Long supplierId, Long materialId) {
        LOG.debugf("Unlinking material %d from supplier %d", materialId, supplierId);

        List<SupplierMaterial> associations = supplierMaterialRepository.findBySupplierId(supplierId);
        SupplierMaterial toDelete = associations.stream()
                .filter(sm -> sm.getMaterialId().equals(materialId))
                .findFirst()
                .orElseThrow(() -> new BusinessException("LINK_NOT_FOUND", "物料关联不存在"));

        supplierMaterialRepository.deleteById(toDelete.getId());
        LOG.infof("Unlinked material %d from supplier %d", materialId, supplierId);
    }

    /**
     * Get all materials linked to a supplier.
     */
    public List<SupplierMaterialDto> getSupplierMaterials(Long supplierId) {
        LOG.debugf("Getting materials for supplier %d", supplierId);
        List<SupplierMaterial> associations = supplierMaterialRepository.findBySupplierId(supplierId);

        return associations.stream().map(sm -> {
            SupplierMaterialDto dto = new SupplierMaterialDto();
            dto.setId(sm.getId());
            dto.setSupplierId(sm.getSupplierId());
            dto.setMaterialId(sm.getMaterialId());

            // Enrich with material code/name
            materialMasterRepository.findById(sm.getMaterialId()).ifPresent(m -> {
                dto.setMaterialCode(m.getMaterialCode());
                dto.setMaterialName(m.getMaterialName());
            });

            return dto;
        }).collect(Collectors.toList());
    }

    private Supplier findOrThrow(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "供应商不存在: " + id));
    }

    private SupplierDto toDto(Supplier entity) {
        SupplierDto dto = new SupplierDto();
        dto.setId(entity.getId());
        dto.setSupplierCode(entity.getSupplierCode());
        dto.setSupplierName(entity.getSupplierName());
        dto.setStatus(entity.getStatus());
        dto.setShortName(entity.getShortName());
        dto.setContactPerson(entity.getContactPerson());
        dto.setPhone(entity.getPhone());
        dto.setAddress(entity.getAddress());
        dto.setEmail(entity.getEmail());
        dto.setDescription(entity.getDescription());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedBy(entity.getUpdatedBy());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
