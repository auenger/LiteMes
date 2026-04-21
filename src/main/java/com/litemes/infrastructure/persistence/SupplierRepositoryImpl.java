package com.litemes.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litemes.domain.entity.Supplier;
import com.litemes.domain.repository.SupplierRepository;
import com.litemes.infrastructure.persistence.mapper.SupplierMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

/**
 * MyBatis-Plus implementation of SupplierRepository.
 */
@ApplicationScoped
public class SupplierRepositoryImpl implements SupplierRepository {

    @Inject
    SupplierMapper supplierMapper;

    @Override
    public Supplier save(Supplier entity) {
        supplierMapper.insert(entity);
        return entity;
    }

    @Override
    public Optional<Supplier> findById(Long id) {
        return Optional.ofNullable(supplierMapper.selectById(id));
    }

    @Override
    public Optional<Supplier> findBySupplierCode(String supplierCode) {
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Supplier::getSupplierCode, supplierCode);
        return Optional.ofNullable(supplierMapper.selectOne(wrapper));
    }

    @Override
    public IPage<Supplier> findPage(IPage<Supplier> page, String supplierCode, String supplierName, Integer status) {
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        if (supplierCode != null && !supplierCode.isBlank()) {
            wrapper.like(Supplier::getSupplierCode, supplierCode);
        }
        if (supplierName != null && !supplierName.isBlank()) {
            wrapper.like(Supplier::getSupplierName, supplierName);
        }
        if (status != null) {
            wrapper.eq(Supplier::getStatus, status);
        }
        wrapper.orderByDesc(Supplier::getCreatedAt);
        return supplierMapper.selectPage(page, wrapper);
    }

    @Override
    public Supplier update(Supplier entity) {
        supplierMapper.updateById(entity);
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        supplierMapper.deleteById(id);
    }

    @Override
    public boolean existsBySupplierCode(String supplierCode) {
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Supplier::getSupplierCode, supplierCode);
        return supplierMapper.selectCount(wrapper) > 0;
    }

    @Override
    public List<Supplier> findAllActive() {
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Supplier::getStatus, 1)
               .orderByAsc(Supplier::getSupplierCode);
        return supplierMapper.selectList(wrapper);
    }

    @Override
    public boolean existsById(Long id) {
        return supplierMapper.selectById(id) != null;
    }
}
