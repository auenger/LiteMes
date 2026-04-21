package com.litemes.domain.repository;

import com.litemes.domain.entity.CustomerMaterial;

import java.util.List;

/**
 * Domain repository interface for CustomerMaterial entity.
 * Infrastructure layer provides the MyBatis-Plus implementation.
 */
public interface CustomerMaterialRepository {

    CustomerMaterial save(CustomerMaterial entity);

    void deleteById(Long id);

    List<CustomerMaterial> findByCustomerId(Long customerId);

    boolean existsByCustomerIdAndMaterialId(Long customerId, Long materialId);

    List<CustomerMaterial> findByMaterialId(Long materialId);
}
