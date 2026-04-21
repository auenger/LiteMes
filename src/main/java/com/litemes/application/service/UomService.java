package com.litemes.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litemes.domain.entity.Uom;
import com.litemes.domain.event.BusinessException;
import com.litemes.domain.repository.UomConversionRepository;
import com.litemes.domain.repository.UomRepository;
import com.litemes.web.dto.PagedResult;
import com.litemes.web.dto.UomCreateDto;
import com.litemes.web.dto.UomDto;
import com.litemes.web.dto.UomQueryDto;
import com.litemes.web.dto.UomUpdateDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Application service for Uom CRUD operations.
 * Handles DTO <-> Entity conversion, business validation, and orchestration.
 */
@ApplicationScoped
public class UomService {

    private static final Logger LOG = Logger.getLogger(UomService.class);

    @Inject
    UomRepository uomRepository;

    @Inject
    UomConversionRepository uomConversionRepository;

    @Transactional
    public Long create(UomCreateDto dto) {
        LOG.debugf("Creating uom: %s", dto.getUomCode());

        // Check uniqueness of uom code
        if (uomRepository.existsByUomCode(dto.getUomCode())) {
            throw new BusinessException("UOM_CODE_DUPLICATE", "单位编码已存在");
        }

        // Check uniqueness of uom name
        if (uomRepository.existsByUomName(dto.getUomName())) {
            throw new BusinessException("UOM_NAME_DUPLICATE", "单位名称已存在");
        }

        Uom entity = new Uom(dto.getUomCode(), dto.getUomName(), dto.getPrecision());
        uomRepository.save(entity);

        LOG.infof("Created uom with id: %d, code: %s", entity.getId(), entity.getUomCode());
        return entity.getId();
    }

    public UomDto getById(Long id) {
        LOG.debugf("Getting uom by id: %d", id);
        Uom entity = findOrThrow(id);
        return toDto(entity);
    }

    public PagedResult<UomDto> list(UomQueryDto query) {
        LOG.debugf("Listing uoms with query: code=%s, name=%s, status=%s",
                query.getUomCode(), query.getUomName(), query.getStatus());

        IPage<Uom> page = new Page<>(query.getPage(), query.getSize());
        IPage<Uom> result = uomRepository.findPage(
                page, query.getUomCode(), query.getUomName(), query.getStatus());

        List<UomDto> records = result.getRecords().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new PagedResult<>(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Transactional
    public void update(Long id, UomUpdateDto dto) {
        LOG.debugf("Updating uom: %d", id);
        Uom entity = findOrThrow(id);

        // Uom code is immutable - not updated

        // Check name uniqueness if name is being changed
        if (dto.getUomName() != null && !dto.getUomName().equals(entity.getUomName())) {
            if (uomRepository.existsByUomName(dto.getUomName())) {
                throw new BusinessException("UOM_NAME_DUPLICATE", "单位名称已存在");
            }
            entity.setUomName(dto.getUomName());
        }

        if (dto.getPrecision() != null) {
            entity.setUomPrecision(dto.getPrecision());
        }

        uomRepository.update(entity);
        LOG.infof("Updated uom: %d", id);
    }

    @Transactional
    public void delete(Long id) {
        LOG.debugf("Deleting uom: %d", id);
        Uom entity = findOrThrow(id);

        // Reference check: UomConversion references this uom
        if (uomConversionRepository.existsByFromUomId(id) || uomConversionRepository.existsByToUomId(id)) {
            throw new BusinessException("UOM_REFERENCED", "该单位已被换算比例引用，不可删除");
        }

        uomRepository.deleteById(id);
        LOG.infof("Deleted uom: %d", id);
    }

    @Transactional
    public void updateStatus(Long id, Integer status) {
        LOG.debugf("Updating uom status: id=%d, status=%d", id, status);
        Uom entity = findOrThrow(id);

        if (entity.getStatus().equals(status)) {
            throw new BusinessException("STATUS_UNCHANGED", "单位状态未发生变化");
        }

        entity.setStatus(status);
        uomRepository.update(entity);
        LOG.infof("Updated uom status: id=%d, status=%d", id, status);
    }

    public List<UomDto> listAllActive() {
        LOG.debug("Listing all active uoms");
        return uomRepository.findAllActive().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private Uom findOrThrow(Long id) {
        return uomRepository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "单位不存在: " + id));
    }

    private UomDto toDto(Uom entity) {
        UomDto dto = new UomDto();
        dto.setId(entity.getId());
        dto.setUomCode(entity.getUomCode());
        dto.setUomName(entity.getUomName());
        dto.setStatus(entity.getStatus());
        dto.setPrecision(entity.getUomPrecision());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedBy(entity.getUpdatedBy());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
