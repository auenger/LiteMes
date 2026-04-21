package com.litemes.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.litemes.domain.entity.SupplierMaterial;
import com.litemes.domain.repository.SupplierMaterialRepository;
import com.litemes.infrastructure.persistence.mapper.SupplierMaterialMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

/**
 * MyBatis-Plus implementation of SupplierMaterialRepository.
 */
@ApplicationScoped
public class SupplierMaterialRepositoryImpl implements SupplierMaterialRepository {

    @Inject
    SupplierMaterialMapper supplierMaterialMapper;

    @Override
    public SupplierMaterial save(SupplierMaterial entity) {
        supplierMaterialMapper.insert(entity);
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        supplierMaterialMapper.deleteById(id);
    }

    @Override
    public List<SupplierMaterial> findBySupplierId(Long supplierId) {
        LambdaQueryWrapper<SupplierMaterial> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SupplierMaterial::getSupplierId, supplierId);
        return supplierMaterialMapper.selectList(wrapper);
    }

    @Override
    public boolean existsBySupplierIdAndMaterialId(Long supplierId, Long materialId) {
        LambdaQueryWrapper<SupplierMaterial> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SupplierMaterial::getSupplierId, supplierId)
               .eq(SupplierMaterial::getMaterialId, materialId);
        return supplierMaterialMapper.selectCount(wrapper) > 0;
    }

    @Override
    public List<SupplierMaterial> findByMaterialId(Long materialId) {
        LambdaQueryWrapper<SupplierMaterial> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SupplierMaterial::getMaterialId, materialId);
        return supplierMaterialMapper.selectList(wrapper);
    }
}
