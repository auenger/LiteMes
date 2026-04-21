package com.litemes.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litemes.application.service.AuditLogService;
import com.litemes.domain.entity.InspectionExemption;
import com.litemes.domain.entity.MaterialMaster;
import com.litemes.domain.event.BusinessException;
import com.litemes.domain.repository.InspectionExemptionRepository;
import com.litemes.domain.repository.MaterialMasterRepository;
import com.litemes.web.dto.InspectionExemptionCreateDto;
import com.litemes.web.dto.InspectionExemptionDto;
import com.litemes.web.dto.InspectionExemptionQueryDto;
import com.litemes.web.dto.InspectionExemptionUpdateDto;
import com.litemes.web.dto.PagedResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Application service for InspectionExemption CRUD operations.
 * Handles DTO <-> Entity conversion, business validation, and orchestration.
 *
 * Business matrix:
 * | Supplier | Validity | Meaning                         |
 * |----------|----------|---------------------------------|
 * | Empty    | Empty    | Global permanent exemption      |
 * | Present  | Empty    | Supplier-specific permanent      |
 * | Present  | Present  | Supplier-specific with validity  |
 * | Empty    | Present  | Global with validity period      |
 */
@ApplicationScoped
public class InspectionExemptionService {

    private static final Logger LOG = Logger.getLogger(InspectionExemptionService.class);

    @Inject
    InspectionExemptionRepository inspectionExemptionRepository;

    @Inject
    MaterialMasterRepository materialMasterRepository;

    @Inject
    AuditLogService auditLogService;

    @Transactional
    public Long create(InspectionExemptionCreateDto dto) {
        LOG.debugf("Creating inspection exemption: materialId=%d, supplierId=%s", dto.getMaterialId(), dto.getSupplierId());

        // Validate material exists and auto-fill material code/name
        MaterialMaster material = materialMasterRepository.findById(dto.getMaterialId())
                .orElseThrow(() -> new BusinessException("MATERIAL_NOT_FOUND", "物料不存在: " + dto.getMaterialId()));

        // Validate validity date range
        validateDateRange(dto.getValidFrom(), dto.getValidTo());

        InspectionExemption entity = new InspectionExemption();
        entity.setMaterialId(material.getId());
        entity.setMaterialCode(material.getMaterialCode());
        entity.setMaterialName(material.getMaterialName());
        entity.setStatus(1);

        // Supplier fields - nullable, will be populated when feat-supplier is available
        entity.setSupplierId(dto.getSupplierId());
        entity.setSupplierCode(null);
        entity.setSupplierName(null);

        entity.setValidFrom(dto.getValidFrom());
        entity.setValidTo(dto.getValidTo());

        inspectionExemptionRepository.save(entity);

        // Record audit log
        auditLogService.logCreate("inspection_exemption", entity.getId(), toDto(entity), null);

        LOG.infof("Created inspection exemption with id: %d, material: %s", entity.getId(), material.getMaterialCode());
        return entity.getId();
    }

    public InspectionExemptionDto getById(Long id) {
        LOG.debugf("Getting inspection exemption by id: %d", id);
        InspectionExemption entity = findOrThrow(id);
        return toDto(entity);
    }

    public PagedResult<InspectionExemptionDto> list(InspectionExemptionQueryDto query) {
        LOG.debugf("Listing inspection exemptions: materialId=%s, supplierId=%s, status=%s",
                query.getMaterialId(), query.getSupplierId(), query.getStatus());

        IPage<InspectionExemption> page = new Page<>(query.getPage(), query.getSize());
        IPage<InspectionExemption> result = inspectionExemptionRepository.findPage(
                page, query.getMaterialId(), query.getSupplierId(), query.getStatus());

        List<InspectionExemptionDto> records = result.getRecords().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new PagedResult<>(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Transactional
    public void update(Long id, InspectionExemptionUpdateDto dto) {
        LOG.debugf("Updating inspection exemption: %d", id);
        InspectionExemption entity = findOrThrow(id);
        InspectionExemptionDto oldValue = toDto(entity);

        // Update material if changed
        if (dto.getMaterialId() != null && !dto.getMaterialId().equals(entity.getMaterialId())) {
            MaterialMaster material = materialMasterRepository.findById(dto.getMaterialId())
                    .orElseThrow(() -> new BusinessException("MATERIAL_NOT_FOUND", "物料不存在: " + dto.getMaterialId()));
            entity.setMaterialId(material.getId());
            entity.setMaterialCode(material.getMaterialCode());
            entity.setMaterialName(material.getMaterialName());
        }

        // Update supplier fields
        if (dto.getSupplierId() != null) {
            entity.setSupplierId(dto.getSupplierId());
            // supplier_code/name will be populated when feat-supplier is available
        } else {
            // Allow clearing supplier
            entity.setSupplierId(null);
            entity.setSupplierCode(null);
            entity.setSupplierName(null);
        }

        // Update validity period
        LocalDate validFrom = dto.getValidFrom() != null ? dto.getValidFrom() : entity.getValidFrom();
        LocalDate validTo = dto.getValidTo() != null ? dto.getValidTo() : entity.getValidTo();
        validateDateRange(validFrom, validTo);
        entity.setValidFrom(dto.getValidFrom());
        entity.setValidTo(dto.getValidTo());

        inspectionExemptionRepository.update(entity);

        // Record audit log
        auditLogService.logUpdate("inspection_exemption", entity.getId(), oldValue, toDto(entity), null);

        LOG.infof("Updated inspection exemption: %d", id);
    }

    @Transactional
    public void delete(Long id) {
        LOG.debugf("Deleting inspection exemption: %d", id);
        InspectionExemption entity = findOrThrow(id);
        InspectionExemptionDto oldValue = toDto(entity);

        // Reference check: in the future, check if this exemption is referenced
        // by incoming inspection records or other business documents.
        // For now, soft delete is sufficient.

        inspectionExemptionRepository.deleteById(id);

        // Record audit log
        auditLogService.logDelete("inspection_exemption", entity.getId(), oldValue, null);

        LOG.infof("Deleted inspection exemption: %d", id);
    }

    @Transactional
    public void updateStatus(Long id, Integer status) {
        LOG.debugf("Updating inspection exemption status: id=%d, status=%d", id, status);
        InspectionExemption entity = findOrThrow(id);

        if (entity.getStatus().equals(status)) {
            throw new BusinessException("STATUS_UNCHANGED", "免检规则状态未发生变化");
        }

        InspectionExemptionDto oldValue = toDto(entity);
        entity.setStatus(status);
        inspectionExemptionRepository.update(entity);

        // Record audit log
        auditLogService.logUpdate("inspection_exemption", entity.getId(), oldValue, toDto(entity), null);

        LOG.infof("Updated inspection exemption status: id=%d, status=%d", id, status);
    }

    /**
     * Scan and disable expired exemption rules.
     * Can be called by Quarkus Scheduler or manually.
     */
    @Transactional
    public int disableExpiredRules() {
        LOG.debug("Scanning for expired inspection exemption rules");
        List<InspectionExemption> expired = inspectionExemptionRepository.findExpiredRules();
        for (InspectionExemption rule : expired) {
            rule.setStatus(0);
            inspectionExemptionRepository.update(rule);
            LOG.infof("Auto-disabled expired exemption rule: id=%d, materialCode=%s, validTo=%s",
                    rule.getId(), rule.getMaterialCode(), rule.getValidTo());
        }
        if (!expired.isEmpty()) {
            LOG.infof("Disabled %d expired inspection exemption rules", expired.size());
        }
        return expired.size();
    }

    private InspectionExemption findOrThrow(Long id) {
        return inspectionExemptionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "免检规则不存在: " + id));
    }

    private void validateDateRange(LocalDate validFrom, LocalDate validTo) {
        if (validFrom != null && validTo != null && validFrom.isAfter(validTo)) {
            throw new BusinessException("INVALID_DATE_RANGE", "有效开始日期不能晚于有效结束日期");
        }
    }

    private InspectionExemptionDto toDto(InspectionExemption entity) {
        InspectionExemptionDto dto = new InspectionExemptionDto();
        dto.setId(entity.getId());
        dto.setMaterialId(entity.getMaterialId());
        dto.setMaterialCode(entity.getMaterialCode());
        dto.setMaterialName(entity.getMaterialName());
        dto.setSupplierId(entity.getSupplierId());
        dto.setSupplierCode(entity.getSupplierCode());
        dto.setSupplierName(entity.getSupplierName());
        dto.setStatus(entity.getStatus());
        dto.setValidFrom(entity.getValidFrom());
        dto.setValidTo(entity.getValidTo());
        dto.setExpired(entity.isExpired());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedBy(entity.getUpdatedBy());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
