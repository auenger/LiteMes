package com.litemes.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litemes.domain.entity.Department;
import com.litemes.domain.entity.Factory;
import com.litemes.domain.event.BusinessException;
import com.litemes.domain.repository.DepartmentRepository;
import com.litemes.domain.repository.FactoryRepository;
import com.litemes.web.dto.DepartmentCreateDto;
import com.litemes.web.dto.DepartmentDto;
import com.litemes.web.dto.DepartmentQueryDto;
import com.litemes.web.dto.DepartmentUpdateDto;
import com.litemes.web.dto.PagedResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Application service for Department CRUD operations.
 * Handles DTO <-> Entity conversion, business validation, and orchestration.
 */
@ApplicationScoped
public class DepartmentService {

    private static final Logger LOG = Logger.getLogger(DepartmentService.class);

    @Inject
    DepartmentRepository departmentRepository;

    @Inject
    FactoryRepository factoryRepository;

    @Transactional
    public Long create(DepartmentCreateDto dto) {
        LOG.debugf("Creating department: %s", dto.getDepartmentCode());

        // Validate factory exists
        Factory factory = factoryRepository.findById(dto.getFactoryId())
                .orElseThrow(() -> new BusinessException("FACTORY_NOT_FOUND", "所属工厂不存在"));

        // Check uniqueness of department code
        if (departmentRepository.existsByDepartmentCode(dto.getDepartmentCode())) {
            throw new BusinessException("DEPARTMENT_CODE_DUPLICATE", "部门编码已存在");
        }

        // Validate parent department if specified
        if (dto.getParentId() != null) {
            Department parent = departmentRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new BusinessException("PARENT_NOT_FOUND", "上级部门不存在"));
            // Parent must belong to the same factory
            if (!parent.getFactoryId().equals(dto.getFactoryId())) {
                throw new BusinessException("PARENT_FACTORY_MISMATCH", "上级部门与所选工厂不一致");
            }
        }

        Department entity = new Department(
                dto.getDepartmentCode(),
                dto.getName(),
                dto.getFactoryId(),
                dto.getParentId(),
                dto.getSortOrder()
        );
        departmentRepository.save(entity);

        LOG.infof("Created department with id: %d, code: %s", entity.getId(), entity.getDepartmentCode());
        return entity.getId();
    }

    public DepartmentDto getById(Long id) {
        LOG.debugf("Getting department by id: %d", id);
        Department entity = findOrThrow(id);
        return toDto(entity);
    }

    public PagedResult<DepartmentDto> list(DepartmentQueryDto query) {
        LOG.debugf("Listing departments with query: code=%s, name=%s, factoryId=%s, status=%s",
                query.getDepartmentCode(), query.getName(), query.getFactoryId(), query.getStatus());

        IPage<Department> page = new Page<>(query.getPage(), query.getSize());
        IPage<Department> result = departmentRepository.findPage(
                page, query.getDepartmentCode(), query.getName(), query.getFactoryId(), query.getStatus());

        List<DepartmentDto> records = result.getRecords().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new PagedResult<>(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Transactional
    public void update(Long id, DepartmentUpdateDto dto) {
        LOG.debugf("Updating department: %d", id);
        Department entity = findOrThrow(id);

        // Department code is immutable - not updated
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getFactoryId() != null) {
            // Validate factory exists
            factoryRepository.findById(dto.getFactoryId())
                    .orElseThrow(() -> new BusinessException("FACTORY_NOT_FOUND", "所属工厂不存在"));
            entity.setFactoryId(dto.getFactoryId());
        }
        if (dto.getParentId() != null) {
            // Cannot set self as parent
            if (dto.getParentId().equals(id)) {
                throw new BusinessException("PARENT_SELF_REFERENCE", "不能将自身设为上级部门");
            }
            // Validate parent exists
            Department parent = departmentRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new BusinessException("PARENT_NOT_FOUND", "上级部门不存在"));
            entity.setParentId(dto.getParentId());
        }
        if (dto.getSortOrder() != null) {
            entity.setSortOrder(dto.getSortOrder());
        }

        departmentRepository.update(entity);
        LOG.infof("Updated department: %d", id);
    }

    @Transactional
    public void delete(Long id) {
        LOG.debugf("Deleting department: %d", id);
        Department entity = findOrThrow(id);

        // Check for child departments
        if (departmentRepository.hasChildren(id)) {
            throw new BusinessException("HAS_CHILDREN", "该部门下存在子部门，无法删除");
        }

        // Note: DepartmentUser reference check will be added when feat-department-user is implemented
        // TODO: Check if any DepartmentUser references this department before deleting

        departmentRepository.deleteById(id);
        LOG.infof("Deleted department: %d", id);
    }

    @Transactional
    public void updateStatus(Long id, Integer status) {
        LOG.debugf("Updating department status: id=%d, status=%d", id, status);
        Department entity = findOrThrow(id);

        if (entity.getStatus().equals(status)) {
            throw new BusinessException("STATUS_UNCHANGED", "部门状态未发生变化");
        }

        entity.setStatus(status);
        departmentRepository.update(entity);
        LOG.infof("Updated department status: id=%d, status=%d", id, status);
    }

    private Department findOrThrow(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "部门不存在: " + id));
    }

    private DepartmentDto toDto(Department entity) {
        DepartmentDto dto = new DepartmentDto();
        dto.setId(entity.getId());
        dto.setDepartmentCode(entity.getDepartmentCode());
        dto.setName(entity.getName());
        dto.setFactoryId(entity.getFactoryId());
        dto.setParentId(entity.getParentId());
        dto.setSortOrder(entity.getSortOrder());
        dto.setStatus(entity.getStatus());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedBy(entity.getUpdatedBy());
        dto.setUpdatedAt(entity.getUpdatedAt());

        // Enrich with factory name
        factoryRepository.findById(entity.getFactoryId()).ifPresent(factory -> {
            dto.setFactoryName(factory.getName());
        });

        // Enrich with parent department name
        if (entity.getParentId() != null) {
            departmentRepository.findById(entity.getParentId()).ifPresent(parent -> {
                dto.setParentName(parent.getName());
            });
        }

        return dto;
    }
}
