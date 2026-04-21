package com.litemes.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litemes.domain.entity.MaterialCategory;
import com.litemes.domain.event.BusinessException;
import com.litemes.domain.repository.MaterialCategoryRepository;
import com.litemes.web.dto.MaterialCategoryCreateDto;
import com.litemes.web.dto.MaterialCategoryDto;
import com.litemes.web.dto.MaterialCategoryQueryDto;
import com.litemes.web.dto.MaterialCategoryTreeDto;
import com.litemes.web.dto.MaterialCategoryUpdateDto;
import com.litemes.web.dto.PagedResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Application service for MaterialCategory CRUD operations.
 * Handles DTO <-> Entity conversion, business validation, and orchestration.
 */
@ApplicationScoped
public class MaterialCategoryService {

    private static final Logger LOG = Logger.getLogger(MaterialCategoryService.class);

    @Inject
    MaterialCategoryRepository materialCategoryRepository;

    @Inject
    AuditLogService auditLogService;

    @Transactional
    public Long create(MaterialCategoryCreateDto dto) {
        LOG.debugf("Creating material category: %s", dto.getCategoryCode());

        // Check uniqueness of category code
        if (materialCategoryRepository.existsByCategoryCode(dto.getCategoryCode())) {
            throw new BusinessException("CATEGORY_CODE_DUPLICATE", "物料分类编码已存在");
        }

        // Check uniqueness of category name
        if (materialCategoryRepository.existsByCategoryName(dto.getCategoryName())) {
            throw new BusinessException("CATEGORY_NAME_DUPLICATE", "物料分类名称已存在");
        }

        // Validate parent category if specified
        if (dto.getParentId() != null) {
            materialCategoryRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new BusinessException("PARENT_NOT_FOUND", "上级分类不存在"));
        }

        MaterialCategory entity = new MaterialCategory(
                dto.getCategoryCode(),
                dto.getCategoryName(),
                dto.getIsQualityCategory(),
                dto.getParentId()
        );
        materialCategoryRepository.save(entity);

        // Audit log
        auditLogService.logCreate("material_category", entity.getId(), toDto(entity), null);

        LOG.infof("Created material category with id: %d, code: %s", entity.getId(), entity.getCategoryCode());
        return entity.getId();
    }

    public MaterialCategoryDto getById(Long id) {
        LOG.debugf("Getting material category by id: %d", id);
        MaterialCategory entity = findOrThrow(id);
        return toDto(entity);
    }

    public PagedResult<MaterialCategoryDto> list(MaterialCategoryQueryDto query) {
        LOG.debugf("Listing material categories with query: code=%s, name=%s, status=%s",
                query.getCategoryCode(), query.getCategoryName(), query.getStatus());

        IPage<MaterialCategory> page = new Page<>(query.getPage(), query.getSize());
        IPage<MaterialCategory> result = materialCategoryRepository.findPage(
                page, query.getCategoryCode(), query.getCategoryName(), query.getStatus());

        List<MaterialCategoryDto> records = result.getRecords().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new PagedResult<>(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    public List<MaterialCategoryTreeDto> tree() {
        LOG.debug("Building material category tree");
        List<MaterialCategory> all = materialCategoryRepository.findAll();
        return buildTree(all);
    }

    @Transactional
    public void update(Long id, MaterialCategoryUpdateDto dto) {
        LOG.debugf("Updating material category: %d", id);
        MaterialCategory entity = findOrThrow(id);

        // Category code is immutable - not updated
        MaterialCategoryDto oldValue = toDto(entity);

        if (dto.getCategoryName() != null) {
            // Check name uniqueness if name is changing
            if (!dto.getCategoryName().equals(entity.getCategoryName())) {
                if (materialCategoryRepository.existsByCategoryName(dto.getCategoryName())) {
                    throw new BusinessException("CATEGORY_NAME_DUPLICATE", "物料分类名称已存在");
                }
            }
            entity.setCategoryName(dto.getCategoryName());
        }
        if (dto.getIsQualityCategory() != null) {
            entity.setIsQualityCategory(dto.getIsQualityCategory());
        }
        if (dto.getParentId() != null) {
            // Cannot set self as parent
            if (dto.getParentId().equals(id)) {
                throw new BusinessException("PARENT_SELF_REFERENCE", "不能将自身设为上级分类");
            }
            // Validate parent exists
            materialCategoryRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new BusinessException("PARENT_NOT_FOUND", "上级分类不存在"));
            entity.setParentId(dto.getParentId());
        }

        materialCategoryRepository.update(entity);

        // Audit log
        auditLogService.logUpdate("material_category", id, oldValue, toDto(entity), null);

        LOG.infof("Updated material category: %d", id);
    }

    @Transactional
    public void delete(Long id) {
        LOG.debugf("Deleting material category: %d", id);
        MaterialCategory entity = findOrThrow(id);

        // Check for child categories
        if (materialCategoryRepository.hasChildren(id)) {
            throw new BusinessException("HAS_CHILDREN", "该分类下存在子分类，无法删除");
        }

        // Note: Reference check for MaterialMaster will be added when feat-material-info is implemented
        // TODO: Check if any MaterialMaster references this category before deleting

        MaterialCategoryDto oldValue = toDto(entity);
        materialCategoryRepository.deleteById(id);

        // Audit log
        auditLogService.logDelete("material_category", id, oldValue, null);

        LOG.infof("Deleted material category: %d", id);
    }

    @Transactional
    public void updateStatus(Long id, Integer status) {
        LOG.debugf("Updating material category status: id=%d, status=%d", id, status);
        MaterialCategory entity = findOrThrow(id);

        if (entity.getStatus().equals(status)) {
            throw new BusinessException("STATUS_UNCHANGED", "分类状态未发生变化");
        }

        entity.setStatus(status);
        materialCategoryRepository.update(entity);
        LOG.infof("Updated material category status: id=%d, status=%d", id, status);
    }

    private MaterialCategory findOrThrow(Long id) {
        return materialCategoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "物料分类不存在: " + id));
    }

    private MaterialCategoryDto toDto(MaterialCategory entity) {
        MaterialCategoryDto dto = new MaterialCategoryDto();
        dto.setId(entity.getId());
        dto.setCategoryCode(entity.getCategoryCode());
        dto.setCategoryName(entity.getCategoryName());
        dto.setIsQualityCategory(entity.getIsQualityCategory());
        dto.setParentId(entity.getParentId());
        dto.setStatus(entity.getStatus());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedBy(entity.getUpdatedBy());
        dto.setUpdatedAt(entity.getUpdatedAt());

        // Enrich with parent category name
        if (entity.getParentId() != null) {
            materialCategoryRepository.findById(entity.getParentId()).ifPresent(parent -> {
                dto.setParentName(parent.getCategoryName());
            });
        }

        return dto;
    }

    /**
     * Build tree structure from flat list of MaterialCategory entities.
     */
    private List<MaterialCategoryTreeDto> buildTree(List<MaterialCategory> all) {
        Map<Long, MaterialCategoryTreeDto> nodeMap = new LinkedHashMap<>();
        List<MaterialCategoryTreeDto> roots = new ArrayList<>();

        // Create all nodes
        for (MaterialCategory cat : all) {
            MaterialCategoryTreeDto node = new MaterialCategoryTreeDto();
            node.setId(cat.getId());
            node.setCategoryCode(cat.getCategoryCode());
            node.setCategoryName(cat.getCategoryName());
            node.setIsQualityCategory(cat.getIsQualityCategory());
            node.setParentId(cat.getParentId());
            node.setStatus(cat.getStatus());
            node.setChildren(new ArrayList<>());
            nodeMap.put(cat.getId(), node);
        }

        // Build parent-child relationships
        for (MaterialCategory cat : all) {
            MaterialCategoryTreeDto node = nodeMap.get(cat.getId());
            if (cat.getParentId() == null) {
                roots.add(node);
            } else {
                MaterialCategoryTreeDto parent = nodeMap.get(cat.getParentId());
                if (parent != null) {
                    parent.getChildren().add(node);
                } else {
                    // Parent not found (orphan), treat as root
                    roots.add(node);
                }
            }
        }

        return roots;
    }
}
