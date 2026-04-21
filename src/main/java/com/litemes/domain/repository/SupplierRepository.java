package com.litemes.domain.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litemes.domain.entity.Supplier;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for Supplier entity.
 * Infrastructure layer provides the MyBatis-Plus implementation.
 */
public interface SupplierRepository {

    Supplier save(Supplier entity);

    Optional<Supplier> findById(Long id);

    Optional<Supplier> findBySupplierCode(String supplierCode);

    IPage<Supplier> findPage(IPage<Supplier> page, String supplierCode, String supplierName, Integer status);

    Supplier update(Supplier entity);

    void deleteById(Long id);

    boolean existsBySupplierCode(String supplierCode);

    List<Supplier> findAllActive();

    boolean existsById(Long id);
}
