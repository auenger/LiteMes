package com.litemes.domain.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litemes.domain.entity.Factory;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for Factory entity.
 * Infrastructure layer provides the MyBatis-Plus implementation.
 */
public interface FactoryRepository {

    Factory save(Factory entity);

    Optional<Factory> findById(Long id);

    Optional<Factory> findByFactoryCode(String factoryCode);

    IPage<Factory> findPage(IPage<Factory> page, String factoryCode, String name, Long companyId, Integer status);

    Factory update(Factory entity);

    void deleteById(Long id);

    boolean existsByFactoryCode(String factoryCode);

    boolean existsByCompanyId(Long companyId);

    long countActiveByCompanyId(Long companyId);

    List<Factory> findByCompanyId(Long companyId);

    List<Factory> findAllActive();
}
