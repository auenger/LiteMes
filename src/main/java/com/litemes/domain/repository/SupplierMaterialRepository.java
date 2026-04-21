package com.litemes.domain.repository;

import com.litemes.domain.entity.SupplierMaterial;

import java.util.List;

/**
 * Domain repository interface for SupplierMaterial entity.
 * Infrastructure layer provides the MyBatis-Plus implementation.
 */
public interface SupplierMaterialRepository {

    SupplierMaterial save(SupplierMaterial entity);

    void deleteById(Long id);

    List<SupplierMaterial> findBySupplierId(Long supplierId);

    boolean existsBySupplierIdAndMaterialId(Long supplierId, Long materialId);

    List<SupplierMaterial> findByMaterialId(Long materialId);
}
