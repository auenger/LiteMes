package com.litemes.domain.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litemes.domain.entity.UomConversion;

import java.util.Optional;

/**
 * Domain repository interface for UomConversion entity.
 * Infrastructure layer provides the MyBatis-Plus implementation.
 */
public interface UomConversionRepository {

    UomConversion save(UomConversion entity);

    Optional<UomConversion> findById(Long id);

    IPage<UomConversion> findPage(IPage<UomConversion> page, String fromUom, String toUom, Integer status);

    UomConversion update(UomConversion entity);

    void deleteById(Long id);

    boolean existsByFromUomIdAndToUomId(Long fromUomId, Long toUomId);

    boolean existsByFromUomId(Long fromUomId);

    boolean existsByToUomId(Long toUomId);

    boolean existsByFromUomIdAndToUomIdExcludingId(Long fromUomId, Long toUomId, Long excludeId);
}
