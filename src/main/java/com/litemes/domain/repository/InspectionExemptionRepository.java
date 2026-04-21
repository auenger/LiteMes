package com.litemes.domain.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litemes.domain.entity.InspectionExemption;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for InspectionExemption entity.
 * Infrastructure layer provides the MyBatis-Plus implementation.
 */
public interface InspectionExemptionRepository {

    InspectionExemption save(InspectionExemption entity);

    Optional<InspectionExemption> findById(Long id);

    IPage<InspectionExemption> findPage(IPage<InspectionExemption> page,
                                         Long materialId, Long supplierId,
                                         Integer status);

    InspectionExemption update(InspectionExemption entity);

    void deleteById(Long id);

    /**
     * Find expired exemption rules (validTo before the given date, status enabled).
     */
    List<InspectionExemption> findExpiredRules();
}
