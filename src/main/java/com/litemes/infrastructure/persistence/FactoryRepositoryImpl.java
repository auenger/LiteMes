package com.litemes.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litemes.domain.entity.Factory;
import com.litemes.domain.repository.FactoryRepository;
import com.litemes.infrastructure.persistence.mapper.FactoryMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;

/**
 * MyBatis-Plus implementation of FactoryRepository.
 */
@ApplicationScoped
public class FactoryRepositoryImpl implements FactoryRepository {

    @Inject
    FactoryMapper factoryMapper;

    @Override
    public Factory save(Factory entity) {
        factoryMapper.insert(entity);
        return entity;
    }

    @Override
    public Optional<Factory> findById(Long id) {
        return Optional.ofNullable(factoryMapper.selectById(id));
    }

    @Override
    public Optional<Factory> findByFactoryCode(String factoryCode) {
        LambdaQueryWrapper<Factory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Factory::getFactoryCode, factoryCode);
        return Optional.ofNullable(factoryMapper.selectOne(wrapper));
    }

    @Override
    public IPage<Factory> findPage(IPage<Factory> page, String factoryCode, String name, Long companyId, Integer status) {
        LambdaQueryWrapper<Factory> wrapper = new LambdaQueryWrapper<>();
        if (factoryCode != null && !factoryCode.isBlank()) {
            wrapper.like(Factory::getFactoryCode, factoryCode);
        }
        if (name != null && !name.isBlank()) {
            wrapper.like(Factory::getName, name);
        }
        if (companyId != null) {
            wrapper.eq(Factory::getCompanyId, companyId);
        }
        if (status != null) {
            wrapper.eq(Factory::getStatus, status);
        }
        wrapper.orderByDesc(Factory::getCreatedAt);
        return factoryMapper.selectPage(page, wrapper);
    }

    @Override
    public Factory update(Factory entity) {
        factoryMapper.updateById(entity);
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        factoryMapper.deleteById(id);
    }

    @Override
    public boolean existsByFactoryCode(String factoryCode) {
        LambdaQueryWrapper<Factory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Factory::getFactoryCode, factoryCode);
        return factoryMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsByCompanyId(Long companyId) {
        LambdaQueryWrapper<Factory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Factory::getCompanyId, companyId);
        return factoryMapper.selectCount(wrapper) > 0;
    }

    @Override
    public long countActiveByCompanyId(Long companyId) {
        LambdaQueryWrapper<Factory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Factory::getCompanyId, companyId)
               .eq(Factory::getStatus, 1);
        return factoryMapper.selectCount(wrapper);
    }
}
