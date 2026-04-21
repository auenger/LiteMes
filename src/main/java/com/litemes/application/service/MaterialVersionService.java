package com.litemes.application.service;

import com.litemes.domain.entity.MaterialMaster;
import com.litemes.domain.entity.MaterialVersion;
import com.litemes.domain.event.BusinessException;
import com.litemes.domain.repository.MaterialMasterRepository;
import com.litemes.domain.repository.MaterialVersionRepository;
import com.litemes.web.dto.MaterialVersionCreateDto;
import com.litemes.web.dto.MaterialVersionDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Application service for MaterialVersion CRUD operations.
 * Handles version management for materials (e.g., A.1, A.2).
 */
@ApplicationScoped
public class MaterialVersionService {

    private static final Logger LOG = Logger.getLogger(MaterialVersionService.class);

    @Inject
    MaterialVersionRepository materialVersionRepository;

    @Inject
    MaterialMasterRepository materialMasterRepository;

    @Transactional
    public Long create(MaterialVersionCreateDto dto) {
        LOG.debugf("Creating version for material: %d, version: %s", dto.getMaterialId(), dto.getVersionNo());

        // Validate material exists
        MaterialMaster material = materialMasterRepository.findById(dto.getMaterialId())
                .orElseThrow(() -> new BusinessException("MATERIAL_NOT_FOUND", "物料不存在: " + dto.getMaterialId()));

        // Validate version uniqueness for this material
        if (materialVersionRepository.existsByMaterialIdAndVersionNo(dto.getMaterialId(), dto.getVersionNo())) {
            throw new BusinessException("VERSION_DUPLICATE", "物料版本号已存在: " + dto.getVersionNo());
        }

        MaterialVersion entity = new MaterialVersion(dto.getMaterialId(), dto.getVersionNo());
        materialVersionRepository.save(entity);

        LOG.infof("Created material version with id: %d, material: %s, version: %s",
                entity.getId(), material.getMaterialCode(), dto.getVersionNo());
        return entity.getId();
    }

    public List<MaterialVersionDto> listByMaterialId(Long materialId) {
        LOG.debugf("Listing versions for material: %d", materialId);

        // Validate material exists
        materialMasterRepository.findById(materialId)
                .orElseThrow(() -> new BusinessException("MATERIAL_NOT_FOUND", "物料不存在: " + materialId));

        return materialVersionRepository.findByMaterialId(materialId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public MaterialVersionDto getById(Long id) {
        LOG.debugf("Getting material version by id: %d", id);
        MaterialVersion entity = findOrThrow(id);
        return toDto(entity);
    }

    private MaterialVersion findOrThrow(Long id) {
        return materialVersionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "物料版本不存在: " + id));
    }

    private MaterialVersionDto toDto(MaterialVersion entity) {
        MaterialVersionDto dto = new MaterialVersionDto();
        dto.setId(entity.getId());
        dto.setMaterialId(entity.getMaterialId());
        dto.setVersionNo(entity.getVersionNo());
        dto.setStatus(entity.getStatus());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedBy(entity.getUpdatedBy());
        dto.setUpdatedAt(entity.getUpdatedAt());

        // Enrich with material code and name
        materialMasterRepository.findById(entity.getMaterialId())
                .ifPresent(m -> {
                    dto.setMaterialCode(m.getMaterialCode());
                    dto.setMaterialName(m.getMaterialName());
                });

        return dto;
    }
}
