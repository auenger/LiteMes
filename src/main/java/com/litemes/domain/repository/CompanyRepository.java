package com.litemes.domain.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litemes.domain.entity.Company;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for Company entity.
 * Infrastructure layer provides the MyBatis-Plus implementation.
 */
public interface CompanyRepository {

    Company save(Company entity);

    Optional<Company> findById(Long id);

    Optional<Company> findByCompanyCode(String companyCode);

    IPage<Company> findPage(IPage<Company> page, String companyCode, String name, Integer status);

    Company update(Company entity);

    void deleteById(Long id);

    boolean existsByCompanyCode(String companyCode);

    List<Company> findAllActive();
}
