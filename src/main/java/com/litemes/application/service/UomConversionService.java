package com.litemes.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litemes.domain.entity.Uom;
import com.litemes.domain.entity.UomConversion;
import com.litemes.domain.event.BusinessException;
import com.litemes.domain.repository.UomConversionRepository;
import com.litemes.domain.repository.UomRepository;
import com.litemes.web.dto.PagedResult;
import com.litemes.web.dto.UomConversionCreateDto;
import com.litemes.web.dto.UomConversionDto;
import com.litemes.web.dto.UomConversionQueryDto;
import com.litemes.web.dto.UomConversionUpdateDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Application service for UomConversion CRUD operations.
 * Handles DTO <-> Entity conversion, business validation, and orchestration.
 */
@ApplicationScoped
public class UomConversionService {

    private static final Logger LOG = Logger.getLogger(UomConversionService.class);

    @Inject
    UomConversionRepository uomConversionRepository;

    @Inject
    UomRepository uomRepository;

    @Transactional
    public Long create(UomConversionCreateDto dto) {
        LOG.debugf("Creating uom conversion: from=%d, to=%d", dto.getFromUomId(), dto.getToUomId());

        // Validate from and to uom exist and are active
        Uom fromUom = uomRepository.findById(dto.getFromUomId())
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "原单位不存在: " + dto.getFromUomId()));
        Uom toUom = uomRepository.findById(dto.getToUomId())
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "目标单位不存在: " + dto.getToUomId()));

        if (fromUom.getStatus() != 1) {
            throw new BusinessException("UOM_DISABLED", "原单位已禁用，不可创建换算比例");
        }
        if (toUom.getStatus() != 1) {
            throw new BusinessException("UOM_DISABLED", "目标单位已禁用，不可创建换算比例");
        }

        // Check uniqueness of from_uom + to_uom
        if (uomConversionRepository.existsByFromUomIdAndToUomId(dto.getFromUomId(), dto.getToUomId())) {
            throw new BusinessException("UOM_CONVERSION_DUPLICATE", "换算比例已存在");
        }

        UomConversion entity = new UomConversion(
                fromUom.getId(), fromUom.getUomCode(), fromUom.getUomName(),
                toUom.getId(), toUom.getUomCode(), toUom.getUomName(),
                dto.getConversionRate()
        );
        uomConversionRepository.save(entity);

        LOG.infof("Created uom conversion with id: %d", entity.getId());
        return entity.getId();
    }

    public UomConversionDto getById(Long id) {
        LOG.debugf("Getting uom conversion by id: %d", id);
        UomConversion entity = findOrThrow(id);
        return toDto(entity);
    }

    public PagedResult<UomConversionDto> list(UomConversionQueryDto query) {
        LOG.debugf("Listing uom conversions with query: from=%s, to=%s, status=%s",
                query.getFromUom(), query.getToUom(), query.getStatus());

        IPage<UomConversion> page = new Page<>(query.getPage(), query.getSize());
        IPage<UomConversion> result = uomConversionRepository.findPage(
                page, query.getFromUom(), query.getToUom(), query.getStatus());

        List<UomConversionDto> records = result.getRecords().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new PagedResult<>(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Transactional
    public void update(Long id, UomConversionUpdateDto dto) {
        LOG.debugf("Updating uom conversion: %d", id);
        UomConversion entity = findOrThrow(id);

        Long newFromUomId = dto.getFromUomId() != null ? dto.getFromUomId() : entity.getFromUomId();
        Long newToUomId = dto.getToUomId() != null ? dto.getToUomId() : entity.getToUomId();

        // If uom references changed, validate them
        if (dto.getFromUomId() != null || dto.getToUomId() != null) {
            Uom fromUom = uomRepository.findById(newFromUomId)
                    .orElseThrow(() -> new BusinessException("NOT_FOUND", "原单位不存在: " + newFromUomId));
            Uom toUom = uomRepository.findById(newToUomId)
                    .orElseThrow(() -> new BusinessException("NOT_FOUND", "目标单位不存在: " + newToUomId));

            // Check uniqueness of new from_uom + to_uom (excluding self)
            if (uomConversionRepository.existsByFromUomIdAndToUomIdExcludingId(newFromUomId, newToUomId, id)) {
                throw new BusinessException("UOM_CONVERSION_DUPLICATE", "换算比例已存在");
            }

            entity.setFromUomId(fromUom.getId());
            entity.setFromUomCode(fromUom.getUomCode());
            entity.setFromUomName(fromUom.getUomName());
            entity.setToUomId(toUom.getId());
            entity.setToUomCode(toUom.getUomCode());
            entity.setToUomName(toUom.getUomName());
        }

        if (dto.getConversionRate() != null) {
            entity.setConversionRate(dto.getConversionRate());
        }

        uomConversionRepository.update(entity);
        LOG.infof("Updated uom conversion: %d", id);
    }

    @Transactional
    public void delete(Long id) {
        LOG.debugf("Deleting uom conversion: %d", id);
        findOrThrow(id);
        uomConversionRepository.deleteById(id);
        LOG.infof("Deleted uom conversion: %d", id);
    }

    private UomConversion findOrThrow(Long id) {
        return uomConversionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "换算比例不存在: " + id));
    }

    private UomConversionDto toDto(UomConversion entity) {
        UomConversionDto dto = new UomConversionDto();
        dto.setId(entity.getId());
        dto.setFromUomId(entity.getFromUomId());
        dto.setFromUomCode(entity.getFromUomCode());
        dto.setFromUomName(entity.getFromUomName());
        dto.setToUomId(entity.getToUomId());
        dto.setToUomCode(entity.getToUomCode());
        dto.setToUomName(entity.getToUomName());
        dto.setConversionRate(entity.getConversionRate());
        dto.setStatus(entity.getStatus());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedBy(entity.getUpdatedBy());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
