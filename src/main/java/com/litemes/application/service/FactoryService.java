package com.litemes.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litemes.domain.entity.Company;
import com.litemes.domain.entity.Factory;
import com.litemes.domain.event.BusinessException;
import com.litemes.domain.repository.CompanyRepository;
import com.litemes.domain.repository.FactoryRepository;
import com.litemes.web.dto.FactoryCreateDto;
import com.litemes.web.dto.FactoryDto;
import com.litemes.web.dto.FactoryQueryDto;
import com.litemes.web.dto.FactoryUpdateDto;
import com.litemes.web.dto.PagedResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Application service for Factory CRUD operations.
 * Handles DTO <-> Entity conversion, business validation, and orchestration.
 */
@ApplicationScoped
public class FactoryService {

    private static final Logger LOG = Logger.getLogger(FactoryService.class);

    @Inject
    FactoryRepository factoryRepository;

    @Inject
    CompanyRepository companyRepository;

    @Inject
    com.litemes.domain.repository.DepartmentRepository departmentRepository;

    @Transactional
    public Long create(FactoryCreateDto dto) {
        LOG.debugf("Creating factory: %s", dto.getFactoryCode());

        // Validate company exists
        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new BusinessException("COMPANY_NOT_FOUND", "所属公司不存在"));

        // Check uniqueness of factory code
        if (factoryRepository.existsByFactoryCode(dto.getFactoryCode())) {
            throw new BusinessException("FACTORY_CODE_DUPLICATE", "工厂编码已存在");
        }

        Factory entity = new Factory(dto.getFactoryCode(), dto.getName(), dto.getShortName(), dto.getCompanyId());
        factoryRepository.save(entity);

        LOG.infof("Created factory with id: %d, code: %s", entity.getId(), entity.getFactoryCode());
        return entity.getId();
    }

    public FactoryDto getById(Long id) {
        LOG.debugf("Getting factory by id: %d", id);
        Factory entity = findOrThrow(id);
        return toDto(entity);
    }

    public PagedResult<FactoryDto> list(FactoryQueryDto query) {
        LOG.debugf("Listing factories with query: code=%s, name=%s, companyId=%s, status=%s",
                query.getFactoryCode(), query.getName(), query.getCompanyId(), query.getStatus());

        IPage<Factory> page = new Page<>(query.getPage(), query.getSize());
        IPage<Factory> result = factoryRepository.findPage(
                page, query.getFactoryCode(), query.getName(), query.getCompanyId(), query.getStatus());

        List<FactoryDto> records = result.getRecords().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new PagedResult<>(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Transactional
    public void update(Long id, FactoryUpdateDto dto) {
        LOG.debugf("Updating factory: %d", id);
        Factory entity = findOrThrow(id);

        // Factory code is immutable - not updated
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getShortName() != null) {
            entity.setShortName(dto.getShortName());
        }
        if (dto.getCompanyId() != null) {
            // Validate new company exists
            companyRepository.findById(dto.getCompanyId())
                    .orElseThrow(() -> new BusinessException("COMPANY_NOT_FOUND", "所属公司不存在"));
            entity.setCompanyId(dto.getCompanyId());
        }

        factoryRepository.update(entity);
        LOG.infof("Updated factory: %d", id);
    }

    @Transactional
    public void delete(Long id) {
        LOG.debugf("Deleting factory: %d", id);
        Factory entity = findOrThrow(id);

        // Reference check: Department.factory_id
        if (departmentRepository.existsByFactoryId(id)) {
            throw new BusinessException("FACTORY_REFERENCED", "该工厂下存在部门，无法删除");
        }

        factoryRepository.deleteById(id);
        LOG.infof("Deleted factory: %d", id);
    }

    @Transactional
    public void updateStatus(Long id, Integer status) {
        LOG.debugf("Updating factory status: id=%d, status=%d", id, status);
        Factory entity = findOrThrow(id);

        if (entity.getStatus().equals(status)) {
            throw new BusinessException("STATUS_UNCHANGED", "工厂状态未发生变化");
        }

        // Disable check: verify no active departments reference this factory
        // Note: Department entity does not exist yet, so we skip the actual check
        // TODO: Check if any active Department references this factory before disabling

        entity.setStatus(status);
        factoryRepository.update(entity);
        LOG.infof("Updated factory status: id=%d, status=%d", id, status);
    }

    private Factory findOrThrow(Long id) {
        return factoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "工厂不存在: " + id));
    }

    private FactoryDto toDto(Factory entity) {
        FactoryDto dto = new FactoryDto();
        dto.setId(entity.getId());
        dto.setFactoryCode(entity.getFactoryCode());
        dto.setName(entity.getName());
        dto.setShortName(entity.getShortName());
        dto.setCompanyId(entity.getCompanyId());
        dto.setStatus(entity.getStatus());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedBy(entity.getUpdatedBy());
        dto.setUpdatedAt(entity.getUpdatedAt());

        // Enrich with company name
        companyRepository.findById(entity.getCompanyId()).ifPresent(company -> {
            dto.setCompanyName(company.getName());
        });

        return dto;
    }
}
