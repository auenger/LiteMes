package com.litemes.domain.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litemes.domain.entity.Customer;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for Customer entity.
 * Infrastructure layer provides the MyBatis-Plus implementation.
 */
public interface CustomerRepository {

    Customer save(Customer entity);

    Optional<Customer> findById(Long id);

    Optional<Customer> findByCustomerCode(String customerCode);

    IPage<Customer> findPage(IPage<Customer> page, String customerCode, String customerName, String type, Integer status);

    Customer update(Customer entity);

    void deleteById(Long id);

    boolean existsByCustomerCode(String customerCode);

    List<Customer> findAllActive();

    boolean existsById(Long id);
}
