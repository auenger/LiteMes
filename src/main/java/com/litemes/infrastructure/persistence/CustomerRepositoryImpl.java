package com.litemes.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litemes.domain.entity.Customer;
import com.litemes.domain.repository.CustomerRepository;
import com.litemes.infrastructure.persistence.mapper.CustomerMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

/**
 * MyBatis-Plus implementation of CustomerRepository.
 */
@ApplicationScoped
public class CustomerRepositoryImpl implements CustomerRepository {

    @Inject
    CustomerMapper customerMapper;

    @Override
    public Customer save(Customer entity) {
        customerMapper.insert(entity);
        return entity;
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return Optional.ofNullable(customerMapper.selectById(id));
    }

    @Override
    public Optional<Customer> findByCustomerCode(String customerCode) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getCustomerCode, customerCode);
        return Optional.ofNullable(customerMapper.selectOne(wrapper));
    }

    @Override
    public IPage<Customer> findPage(IPage<Customer> page, String customerCode, String customerName, String type, Integer status) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        if (customerCode != null && !customerCode.isBlank()) {
            wrapper.like(Customer::getCustomerCode, customerCode);
        }
        if (customerName != null && !customerName.isBlank()) {
            wrapper.like(Customer::getCustomerName, customerName);
        }
        if (type != null && !type.isBlank()) {
            wrapper.eq(Customer::getType, type);
        }
        if (status != null) {
            wrapper.eq(Customer::getStatus, status);
        }
        wrapper.orderByDesc(Customer::getCreatedAt);
        return customerMapper.selectPage(page, wrapper);
    }

    @Override
    public Customer update(Customer entity) {
        customerMapper.updateById(entity);
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        customerMapper.deleteById(id);
    }

    @Override
    public boolean existsByCustomerCode(String customerCode) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getCustomerCode, customerCode);
        return customerMapper.selectCount(wrapper) > 0;
    }

    @Override
    public List<Customer> findAllActive() {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getStatus, 1)
               .orderByAsc(Customer::getCustomerCode);
        return customerMapper.selectList(wrapper);
    }

    @Override
    public boolean existsById(Long id) {
        return customerMapper.selectById(id) != null;
    }
}
