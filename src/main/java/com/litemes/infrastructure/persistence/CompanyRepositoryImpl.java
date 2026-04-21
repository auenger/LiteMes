package com.litemes.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litemes.domain.entity.Company;
import com.litemes.domain.repository.CompanyRepository;
import com.litemes.infrastructure.persistence.mapper.CompanyMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

/**
 * MyBatis-Plus implementation of CompanyRepository.
 */
@ApplicationScoped
public class CompanyRepositoryImpl implements CompanyRepository {

    @Inject
    CompanyMapper companyMapper;

    @Override
    public Company save(Company entity) {
        companyMapper.insert(entity);
        return entity;
    }

    @Override
    public Optional<Company> findById(Long id) {
        return Optional.ofNullable(companyMapper.selectById(id));
    }

    @Override
    public Optional<Company> findByCompanyCode(String companyCode) {
        LambdaQueryWrapper<Company> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Company::getCompanyCode, companyCode);
        return Optional.ofNullable(companyMapper.selectOne(wrapper));
    }

    @Override
    public IPage<Company> findPage(IPage<Company> page, String companyCode, String name, Integer status) {
        LambdaQueryWrapper<Company> wrapper = new LambdaQueryWrapper<>();
        if (companyCode != null && !companyCode.isBlank()) {
            wrapper.like(Company::getCompanyCode, companyCode);
        }
        if (name != null && !name.isBlank()) {
            wrapper.like(Company::getName, name);
        }
        if (status != null) {
            wrapper.eq(Company::getStatus, status);
        }
        wrapper.orderByDesc(Company::getCreatedAt);
        return companyMapper.selectPage(page, wrapper);
    }

    @Override
    public Company update(Company entity) {
        companyMapper.updateById(entity);
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        companyMapper.deleteById(id);
    }

    @Override
    public boolean existsByCompanyCode(String companyCode) {
        LambdaQueryWrapper<Company> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Company::getCompanyCode, companyCode);
        return companyMapper.selectCount(wrapper) > 0;
    }

    @Override
    public List<Company> findAllActive() {
        LambdaQueryWrapper<Company> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Company::getStatus, 1)
               .orderByAsc(Company::getCompanyCode);
        return companyMapper.selectList(wrapper);
    }
}
