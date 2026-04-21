package com.litemes.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litemes.domain.entity.Company;
import com.litemes.domain.event.BusinessException;
import com.litemes.domain.repository.CompanyRepository;
import com.litemes.web.dto.CompanyCreateDto;
import com.litemes.web.dto.CompanyDto;
import com.litemes.web.dto.CompanyQueryDto;
import com.litemes.web.dto.CompanyUpdateDto;
import com.litemes.web.dto.PagedResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Application service for Company CRUD operations.
 * Handles DTO <-> Entity conversion, business validation, and orchestration.
 */
@ApplicationScoped
public class CompanyService {

    private static final Logger LOG = Logger.getLogger(CompanyService.class);

    @Inject
    CompanyRepository companyRepository;

    @Transactional
    public Long create(CompanyCreateDto dto) {
        LOG.debugf("Creating company: %s", dto.getCompanyCode());

        // Check uniqueness of company code
        if (companyRepository.existsByCompanyCode(dto.getCompanyCode())) {
            throw new BusinessException("COMPANY_CODE_DUPLICATE", "公司编码已存在");
        }

        Company entity = new Company(dto.getCompanyCode(), dto.getName(), dto.getShortCode());
        companyRepository.save(entity);

        LOG.infof("Created company with id: %d, code: %s", entity.getId(), entity.getCompanyCode());
        return entity.getId();
    }

    public CompanyDto getById(Long id) {
        LOG.debugf("Getting company by id: %d", id);
        Company entity = findOrThrow(id);
        return toDto(entity);
    }

    public PagedResult<CompanyDto> list(CompanyQueryDto query) {
        LOG.debugf("Listing companies with query: code=%s, name=%s, status=%s",
                query.getCompanyCode(), query.getName(), query.getStatus());

        IPage<Company> page = new Page<>(query.getPage(), query.getSize());
        IPage<Company> result = companyRepository.findPage(
                page, query.getCompanyCode(), query.getName(), query.getStatus());

        List<CompanyDto> records = result.getRecords().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new PagedResult<>(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Transactional
    public void update(Long id, CompanyUpdateDto dto) {
        LOG.debugf("Updating company: %d", id);
        Company entity = findOrThrow(id);

        // Company code is immutable - not updated
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getShortCode() != null) {
            entity.setShortCode(dto.getShortCode());
        }

        companyRepository.update(entity);
        LOG.infof("Updated company: %d", id);
    }

    @Transactional
    public void delete(Long id) {
        LOG.debugf("Deleting company: %d", id);
        Company entity = findOrThrow(id);

        // Reference check: Factory.company_id
        // Note: Factory entity does not exist yet, so we skip the actual check
        // This will be implemented when feat-factory is developed
        // TODO: Check if any Factory references this company before deleting

        companyRepository.deleteById(id);
        LOG.infof("Deleted company: %d", id);
    }

    @Transactional
    public void updateStatus(Long id, Integer status) {
        LOG.debugf("Updating company status: id=%d, status=%d", id, status);
        Company entity = findOrThrow(id);

        if (entity.getStatus().equals(status)) {
            throw new BusinessException("STATUS_UNCHANGED", "公司状态未发生变化");
        }

        // Disable check: verify no active factories reference this company
        // Note: Factory entity does not exist yet, so we skip the actual check
        // TODO: Check if any active Factory references this company before disabling

        entity.setStatus(status);
        companyRepository.update(entity);
        LOG.infof("Updated company status: id=%d, status=%d", id, status);
    }

    private Company findOrThrow(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "公司不存在: " + id));
    }

    private CompanyDto toDto(Company entity) {
        CompanyDto dto = new CompanyDto();
        dto.setId(entity.getId());
        dto.setCompanyCode(entity.getCompanyCode());
        dto.setName(entity.getName());
        dto.setShortCode(entity.getShortCode());
        dto.setStatus(entity.getStatus());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedBy(entity.getUpdatedBy());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
