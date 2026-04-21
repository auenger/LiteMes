package com.litemes.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.litemes.domain.entity.CustomerMaterial;
import com.litemes.domain.repository.CustomerMaterialRepository;
import com.litemes.infrastructure.persistence.mapper.CustomerMaterialMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

/**
 * MyBatis-Plus implementation of CustomerMaterialRepository.
 */
@ApplicationScoped
public class CustomerMaterialRepositoryImpl implements CustomerMaterialRepository {

    @Inject
    CustomerMaterialMapper customerMaterialMapper;

    @Override
    public CustomerMaterial save(CustomerMaterial entity) {
        customerMaterialMapper.insert(entity);
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        customerMaterialMapper.deleteById(id);
    }

    @Override
    public List<CustomerMaterial> findByCustomerId(Long customerId) {
        LambdaQueryWrapper<CustomerMaterial> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerMaterial::getCustomerId, customerId);
        return customerMaterialMapper.selectList(wrapper);
    }

    @Override
    public boolean existsByCustomerIdAndMaterialId(Long customerId, Long materialId) {
        LambdaQueryWrapper<CustomerMaterial> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerMaterial::getCustomerId, customerId)
               .eq(CustomerMaterial::getMaterialId, materialId);
        return customerMaterialMapper.selectCount(wrapper) > 0;
    }

    @Override
    public List<CustomerMaterial> findByMaterialId(Long materialId) {
        LambdaQueryWrapper<CustomerMaterial> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerMaterial::getMaterialId, materialId);
        return customerMaterialMapper.selectList(wrapper);
    }
}
