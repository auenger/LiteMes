package com.litemes.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litemes.domain.entity.UomConversion;
import com.litemes.domain.repository.UomConversionRepository;
import com.litemes.infrastructure.persistence.mapper.UomConversionMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;

/**
 * MyBatis-Plus implementation of UomConversionRepository.
 */
@ApplicationScoped
public class UomConversionRepositoryImpl implements UomConversionRepository {

    @Inject
    UomConversionMapper uomConversionMapper;

    @Override
    public UomConversion save(UomConversion entity) {
        uomConversionMapper.insert(entity);
        return entity;
    }

    @Override
    public Optional<UomConversion> findById(Long id) {
        return Optional.ofNullable(uomConversionMapper.selectById(id));
    }

    @Override
    public IPage<UomConversion> findPage(IPage<UomConversion> page, String fromUom, String toUom, Integer status) {
        LambdaQueryWrapper<UomConversion> wrapper = new LambdaQueryWrapper<>();
        if (fromUom != null && !fromUom.isBlank()) {
            wrapper.and(w -> w.like(UomConversion::getFromUomCode, fromUom)
                              .or().like(UomConversion::getFromUomName, fromUom));
        }
        if (toUom != null && !toUom.isBlank()) {
            wrapper.and(w -> w.like(UomConversion::getToUomCode, toUom)
                              .or().like(UomConversion::getToUomName, toUom));
        }
        if (status != null) {
            wrapper.eq(UomConversion::getStatus, status);
        }
        wrapper.orderByDesc(UomConversion::getCreatedAt);
        return uomConversionMapper.selectPage(page, wrapper);
    }

    @Override
    public UomConversion update(UomConversion entity) {
        uomConversionMapper.updateById(entity);
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        uomConversionMapper.deleteById(id);
    }

    @Override
    public boolean existsByFromUomIdAndToUomId(Long fromUomId, Long toUomId) {
        LambdaQueryWrapper<UomConversion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UomConversion::getFromUomId, fromUomId)
               .eq(UomConversion::getToUomId, toUomId);
        return uomConversionMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsByFromUomId(Long fromUomId) {
        LambdaQueryWrapper<UomConversion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UomConversion::getFromUomId, fromUomId);
        return uomConversionMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsByToUomId(Long toUomId) {
        LambdaQueryWrapper<UomConversion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UomConversion::getToUomId, toUomId);
        return uomConversionMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsByFromUomIdAndToUomIdExcludingId(Long fromUomId, Long toUomId, Long excludeId) {
        LambdaQueryWrapper<UomConversion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UomConversion::getFromUomId, fromUomId)
               .eq(UomConversion::getToUomId, toUomId)
               .ne(UomConversion::getId, excludeId);
        return uomConversionMapper.selectCount(wrapper) > 0;
    }
}
