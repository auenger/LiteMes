package com.litemes.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litemes.domain.entity.MaterialCategory;
import com.litemes.domain.entity.MaterialMaster;
import com.litemes.domain.entity.Uom;
import com.litemes.domain.enums.AttributeCategory;
import com.litemes.domain.enums.BasicCategory;
import com.litemes.domain.event.BusinessException;
import com.litemes.domain.repository.MaterialCategoryRepository;
import com.litemes.domain.repository.MaterialMasterRepository;
import com.litemes.domain.repository.UomRepository;
import com.litemes.web.dto.MaterialCreateDto;
import com.litemes.web.dto.MaterialDto;
import com.litemes.web.dto.MaterialQueryDto;
import com.litemes.web.dto.MaterialUpdateDto;
import com.litemes.web.dto.PagedResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Application service for MaterialMaster CRUD operations.
 * Handles DTO <-> Entity conversion, business validation, and orchestration.
 */
@ApplicationScoped
public class MaterialService {

    private static final Logger LOG = Logger.getLogger(MaterialService.class);

    @Inject
    MaterialMasterRepository materialMasterRepository;

    @Inject
    MaterialCategoryRepository materialCategoryRepository;

    @Inject
    UomRepository uomRepository;

    @Transactional
    public Long create(MaterialCreateDto dto) {
        LOG.debugf("Creating material: %s", dto.getMaterialCode());

        // Validate material code uniqueness
        if (materialMasterRepository.existsByMaterialCode(dto.getMaterialCode())) {
            throw new BusinessException("MATERIAL_CODE_DUPLICATE", "物料编码已存在");
        }

        // Validate material name uniqueness
        if (materialMasterRepository.existsByMaterialName(dto.getMaterialName())) {
            throw new BusinessException("MATERIAL_NAME_DUPLICATE", "物料名称已存在");
        }

        // Validate basic category enum
        if (!BasicCategory.isValid(dto.getBasicCategory())) {
            throw new BusinessException("INVALID_BASIC_CATEGORY", "无效的基本分类: " + dto.getBasicCategory());
        }

        // Validate attribute category enum
        if (!AttributeCategory.isValid(dto.getAttributeCategory())) {
            throw new BusinessException("INVALID_ATTRIBUTE_CATEGORY", "无效的属性分类: " + dto.getAttributeCategory());
        }

        // Validate category exists
        materialCategoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new BusinessException("CATEGORY_NOT_FOUND", "物料分类不存在: " + dto.getCategoryId()));

        // Validate uom exists
        uomRepository.findById(dto.getUomId())
                .orElseThrow(() -> new BusinessException("UOM_NOT_FOUND", "单位不存在: " + dto.getUomId()));

        MaterialMaster entity = new MaterialMaster();
        entity.setMaterialCode(dto.getMaterialCode());
        entity.setMaterialName(dto.getMaterialName());
        entity.setStatus(1);
        entity.setBasicCategory(dto.getBasicCategory());
        entity.setCategoryId(dto.getCategoryId());
        entity.setAttributeCategory(dto.getAttributeCategory());
        entity.setUomId(dto.getUomId());
        entity.setSize(dto.getSize());
        entity.setLength(dto.getLength());
        entity.setWidth(dto.getWidth());
        entity.setModel(dto.getModel());
        entity.setSpecification(dto.getSpecification());
        entity.setThickness(dto.getThickness());
        entity.setColor(dto.getColor());
        entity.setTgValue(dto.getTgValue());
        entity.setCopperThickness(dto.getCopperThickness());
        entity.setIsCopperContained(dto.getIsCopperContained());
        entity.setDiameter(dto.getDiameter());
        entity.setBladeLength(dto.getBladeLength());
        entity.setTotalLength(dto.getTotalLength());
        entity.setExt1(dto.getExt1());
        entity.setExt2(dto.getExt2());
        entity.setExt3(dto.getExt3());
        entity.setExt4(dto.getExt4());
        entity.setExt5(dto.getExt5());

        materialMasterRepository.save(entity);

        LOG.infof("Created material with id: %d, code: %s", entity.getId(), entity.getMaterialCode());
        return entity.getId();
    }

    public MaterialDto getById(Long id) {
        LOG.debugf("Getting material by id: %d", id);
        MaterialMaster entity = findOrThrow(id);
        return toDto(entity);
    }

    public PagedResult<MaterialDto> list(MaterialQueryDto query) {
        LOG.debugf("Listing materials with query: code=%s, name=%s, categoryId=%s, basicCategory=%s, status=%s",
                query.getMaterialCode(), query.getMaterialName(), query.getCategoryId(),
                query.getBasicCategory(), query.getStatus());

        IPage<MaterialMaster> page = new Page<>(query.getPage(), query.getSize());
        IPage<MaterialMaster> result = materialMasterRepository.findPage(
                page, query.getMaterialCode(), query.getMaterialName(),
                query.getCategoryId(), query.getBasicCategory(), query.getStatus());

        List<MaterialDto> records = result.getRecords().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new PagedResult<>(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Transactional
    public void update(Long id, MaterialUpdateDto dto) {
        LOG.debugf("Updating material: %d", id);
        MaterialMaster entity = findOrThrow(id);

        // Material code is immutable - not updated

        // Check name uniqueness if name is being changed
        if (dto.getMaterialName() != null && !dto.getMaterialName().equals(entity.getMaterialName())) {
            if (materialMasterRepository.existsByMaterialName(dto.getMaterialName())) {
                throw new BusinessException("MATERIAL_NAME_DUPLICATE", "物料名称已存在");
            }
            entity.setMaterialName(dto.getMaterialName());
        }

        if (dto.getBasicCategory() != null) {
            if (!BasicCategory.isValid(dto.getBasicCategory())) {
                throw new BusinessException("INVALID_BASIC_CATEGORY", "无效的基本分类: " + dto.getBasicCategory());
            }
            entity.setBasicCategory(dto.getBasicCategory());
        }

        if (dto.getAttributeCategory() != null) {
            if (!AttributeCategory.isValid(dto.getAttributeCategory())) {
                throw new BusinessException("INVALID_ATTRIBUTE_CATEGORY", "无效的属性分类: " + dto.getAttributeCategory());
            }
            entity.setAttributeCategory(dto.getAttributeCategory());
        }

        if (dto.getCategoryId() != null) {
            materialCategoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new BusinessException("CATEGORY_NOT_FOUND", "物料分类不存在: " + dto.getCategoryId()));
            entity.setCategoryId(dto.getCategoryId());
        }

        if (dto.getUomId() != null) {
            uomRepository.findById(dto.getUomId())
                    .orElseThrow(() -> new BusinessException("UOM_NOT_FOUND", "单位不存在: " + dto.getUomId()));
            entity.setUomId(dto.getUomId());
        }

        // Update optional fields
        if (dto.getSize() != null) entity.setSize(dto.getSize());
        if (dto.getLength() != null) entity.setLength(dto.getLength());
        if (dto.getWidth() != null) entity.setWidth(dto.getWidth());
        if (dto.getModel() != null) entity.setModel(dto.getModel());
        if (dto.getSpecification() != null) entity.setSpecification(dto.getSpecification());
        if (dto.getThickness() != null) entity.setThickness(dto.getThickness());
        if (dto.getColor() != null) entity.setColor(dto.getColor());
        if (dto.getTgValue() != null) entity.setTgValue(dto.getTgValue());
        if (dto.getCopperThickness() != null) entity.setCopperThickness(dto.getCopperThickness());
        if (dto.getIsCopperContained() != null) entity.setIsCopperContained(dto.getIsCopperContained());
        if (dto.getDiameter() != null) entity.setDiameter(dto.getDiameter());
        if (dto.getBladeLength() != null) entity.setBladeLength(dto.getBladeLength());
        if (dto.getTotalLength() != null) entity.setTotalLength(dto.getTotalLength());
        if (dto.getExt1() != null) entity.setExt1(dto.getExt1());
        if (dto.getExt2() != null) entity.setExt2(dto.getExt2());
        if (dto.getExt3() != null) entity.setExt3(dto.getExt3());
        if (dto.getExt4() != null) entity.setExt4(dto.getExt4());
        if (dto.getExt5() != null) entity.setExt5(dto.getExt5());

        materialMasterRepository.update(entity);
        LOG.infof("Updated material: %d", id);
    }

    @Transactional
    public void delete(Long id) {
        LOG.debugf("Deleting material: %d", id);
        MaterialMaster entity = findOrThrow(id);

        // Note: Reference check for InspectionExemption will be added when that feature is implemented
        // Currently we allow deletion as the inspection_exemption table does not exist yet

        materialMasterRepository.deleteById(id);
        LOG.infof("Deleted material: %d", id);
    }

    @Transactional
    public void updateStatus(Long id, Integer status) {
        LOG.debugf("Updating material status: id=%d, status=%d", id, status);
        MaterialMaster entity = findOrThrow(id);

        if (entity.getStatus().equals(status)) {
            throw new BusinessException("STATUS_UNCHANGED", "物料状态未发生变化");
        }

        entity.setStatus(status);
        materialMasterRepository.update(entity);
        LOG.infof("Updated material status: id=%d, status=%d", id, status);
    }

    private MaterialMaster findOrThrow(Long id) {
        return materialMasterRepository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "物料不存在: " + id));
    }

    private MaterialDto toDto(MaterialMaster entity) {
        MaterialDto dto = new MaterialDto();
        dto.setId(entity.getId());
        dto.setMaterialCode(entity.getMaterialCode());
        dto.setMaterialName(entity.getMaterialName());
        dto.setStatus(entity.getStatus());
        dto.setBasicCategory(entity.getBasicCategory());
        dto.setCategoryId(entity.getCategoryId());
        dto.setAttributeCategory(entity.getAttributeCategory());
        dto.setUomId(entity.getUomId());
        dto.setSize(entity.getSize());
        dto.setLength(entity.getLength());
        dto.setWidth(entity.getWidth());
        dto.setModel(entity.getModel());
        dto.setSpecification(entity.getSpecification());
        dto.setThickness(entity.getThickness());
        dto.setColor(entity.getColor());
        dto.setTgValue(entity.getTgValue());
        dto.setCopperThickness(entity.getCopperThickness());
        dto.setIsCopperContained(entity.getIsCopperContained());
        dto.setDiameter(entity.getDiameter());
        dto.setBladeLength(entity.getBladeLength());
        dto.setTotalLength(entity.getTotalLength());
        dto.setExt1(entity.getExt1());
        dto.setExt2(entity.getExt2());
        dto.setExt3(entity.getExt3());
        dto.setExt4(entity.getExt4());
        dto.setExt5(entity.getExt5());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedBy(entity.getUpdatedBy());
        dto.setUpdatedAt(entity.getUpdatedAt());

        // Enrich with category name
        if (entity.getCategoryId() != null) {
            materialCategoryRepository.findById(entity.getCategoryId())
                    .ifPresent(cat -> dto.setCategoryName(cat.getCategoryName()));
        }

        // Enrich with uom name
        if (entity.getUomId() != null) {
            uomRepository.findById(entity.getUomId())
                    .ifPresent(uom -> dto.setUomName(uom.getUomName()));
        }

        return dto;
    }
}
