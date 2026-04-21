package com.litemes.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litemes.domain.entity.Customer;
import com.litemes.domain.entity.CustomerMaterial;
import com.litemes.domain.entity.MaterialMaster;
import com.litemes.domain.event.BusinessException;
import com.litemes.domain.repository.CustomerMaterialRepository;
import com.litemes.domain.repository.CustomerRepository;
import com.litemes.domain.repository.MaterialMasterRepository;
import com.litemes.web.dto.CustomerCreateDto;
import com.litemes.web.dto.CustomerDto;
import com.litemes.web.dto.CustomerMaterialDto;
import com.litemes.web.dto.CustomerQueryDto;
import com.litemes.web.dto.CustomerUpdateDto;
import com.litemes.web.dto.PagedResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Application service for Customer CRUD operations.
 * Handles DTO <-> Entity conversion, business validation, and orchestration.
 */
@ApplicationScoped
public class CustomerService {

    private static final Logger LOG = Logger.getLogger(CustomerService.class);

    @Inject
    CustomerRepository customerRepository;

    @Inject
    CustomerMaterialRepository customerMaterialRepository;

    @Inject
    MaterialMasterRepository materialMasterRepository;

    @Inject
    AuditLogService auditLogService;

    @Transactional
    public Long create(CustomerCreateDto dto) {
        LOG.debugf("Creating customer: %s", dto.getCustomerCode());

        // Check uniqueness of customer code
        if (customerRepository.existsByCustomerCode(dto.getCustomerCode())) {
            throw new BusinessException("CUSTOMER_CODE_DUPLICATE", "客户编码已存在");
        }

        Customer entity = new Customer(dto.getCustomerCode(), dto.getCustomerName());
        entity.setType(dto.getType());
        entity.setShortName(dto.getShortName());
        entity.setContactPerson(dto.getContactPerson());
        entity.setPhone(dto.getPhone());
        entity.setAddress(dto.getAddress());
        entity.setEmail(dto.getEmail());

        customerRepository.save(entity);

        // Record audit log
        auditLogService.logCreate("customer", entity.getId(), toDto(entity), null);

        LOG.infof("Created customer with id: %d, code: %s", entity.getId(), entity.getCustomerCode());
        return entity.getId();
    }

    public CustomerDto getById(Long id) {
        LOG.debugf("Getting customer by id: %d", id);
        Customer entity = findOrThrow(id);
        CustomerDto dto = toDto(entity);
        dto.setMaterials(getCustomerMaterials(id));
        return dto;
    }

    public PagedResult<CustomerDto> list(CustomerQueryDto query) {
        LOG.debugf("Listing customers with query: code=%s, name=%s, type=%s, status=%s",
                query.getCustomerCode(), query.getCustomerName(), query.getType(), query.getStatus());

        IPage<Customer> page = new Page<>(query.getPage(), query.getSize());
        IPage<Customer> result = customerRepository.findPage(
                page, query.getCustomerCode(), query.getCustomerName(), query.getType(), query.getStatus());

        List<CustomerDto> records = result.getRecords().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new PagedResult<>(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Transactional
    public void update(Long id, CustomerUpdateDto dto) {
        LOG.debugf("Updating customer: %d", id);
        Customer entity = findOrThrow(id);
        CustomerDto oldValue = toDto(entity);

        // Customer code is immutable - not updated
        if (dto.getCustomerName() != null) {
            entity.setCustomerName(dto.getCustomerName());
        }
        if (dto.getType() != null) {
            entity.setType(dto.getType());
        }
        if (dto.getShortName() != null) {
            entity.setShortName(dto.getShortName());
        }
        if (dto.getContactPerson() != null) {
            entity.setContactPerson(dto.getContactPerson());
        }
        if (dto.getPhone() != null) {
            entity.setPhone(dto.getPhone());
        }
        if (dto.getAddress() != null) {
            entity.setAddress(dto.getAddress());
        }
        if (dto.getEmail() != null) {
            entity.setEmail(dto.getEmail());
        }

        customerRepository.update(entity);

        // Record audit log
        auditLogService.logUpdate("customer", entity.getId(), oldValue, toDto(entity), null);

        LOG.infof("Updated customer: %d", id);
    }

    @Transactional
    public void delete(Long id) {
        LOG.debugf("Deleting customer: %d", id);
        Customer entity = findOrThrow(id);
        CustomerDto oldValue = toDto(entity);

        // Reference check: check if any customer_material associations exist
        List<CustomerMaterial> materials = customerMaterialRepository.findByCustomerId(id);
        if (!materials.isEmpty()) {
            throw new BusinessException("CUSTOMER_REFERENCED", "客户已被物料关联，不可删除");
        }

        customerRepository.deleteById(id);

        // Record audit log
        auditLogService.logDelete("customer", entity.getId(), oldValue, null);

        LOG.infof("Deleted customer: %d", id);
    }

    @Transactional
    public void updateStatus(Long id, Integer status) {
        LOG.debugf("Updating customer status: id=%d, status=%d", id, status);
        Customer entity = findOrThrow(id);

        if (entity.getStatus().equals(status)) {
            throw new BusinessException("STATUS_UNCHANGED", "客户状态未发生变化");
        }

        CustomerDto oldValue = toDto(entity);
        entity.setStatus(status);
        customerRepository.update(entity);

        // Record audit log
        auditLogService.logUpdate("customer", entity.getId(), oldValue, toDto(entity), null);

        LOG.infof("Updated customer status: id=%d, status=%d", id, status);
    }

    /**
     * Link materials to a customer. Supports multiple material IDs with duplicate check.
     */
    @Transactional
    public void linkMaterials(Long customerId, List<Long> materialIds) {
        LOG.debugf("Linking materials to customer %d: %s", customerId, materialIds);
        findOrThrow(customerId);

        for (Long materialId : materialIds) {
            // Validate material exists
            MaterialMaster material = materialMasterRepository.findById(materialId)
                    .orElseThrow(() -> new BusinessException("MATERIAL_NOT_FOUND", "物料不存在: " + materialId));

            // Check duplicate
            if (customerMaterialRepository.existsByCustomerIdAndMaterialId(customerId, materialId)) {
                throw new BusinessException("MATERIAL_ALREADY_LINKED",
                        "物料 " + material.getMaterialCode() + " 已关联，不可重复添加");
            }

            CustomerMaterial cm = new CustomerMaterial(customerId, materialId);
            customerMaterialRepository.save(cm);
        }

        LOG.infof("Linked %d materials to customer %d", materialIds.size(), customerId);
    }

    /**
     * Unlink a material from a customer.
     */
    @Transactional
    public void unlinkMaterial(Long customerId, Long materialId) {
        LOG.debugf("Unlinking material %d from customer %d", materialId, customerId);

        List<CustomerMaterial> associations = customerMaterialRepository.findByCustomerId(customerId);
        CustomerMaterial toDelete = associations.stream()
                .filter(cm -> cm.getMaterialId().equals(materialId))
                .findFirst()
                .orElseThrow(() -> new BusinessException("LINK_NOT_FOUND", "物料关联不存在"));

        customerMaterialRepository.deleteById(toDelete.getId());
        LOG.infof("Unlinked material %d from customer %d", materialId, customerId);
    }

    /**
     * Get all materials linked to a customer.
     */
    public List<CustomerMaterialDto> getCustomerMaterials(Long customerId) {
        LOG.debugf("Getting materials for customer %d", customerId);
        List<CustomerMaterial> associations = customerMaterialRepository.findByCustomerId(customerId);

        return associations.stream().map(cm -> {
            CustomerMaterialDto dto = new CustomerMaterialDto();
            dto.setId(cm.getId());
            dto.setCustomerId(cm.getCustomerId());
            dto.setMaterialId(cm.getMaterialId());

            // Enrich with material code/name
            materialMasterRepository.findById(cm.getMaterialId()).ifPresent(m -> {
                dto.setMaterialCode(m.getMaterialCode());
                dto.setMaterialName(m.getMaterialName());
            });

            return dto;
        }).collect(Collectors.toList());
    }

    private Customer findOrThrow(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "客户不存在: " + id));
    }

    private CustomerDto toDto(Customer entity) {
        CustomerDto dto = new CustomerDto();
        dto.setId(entity.getId());
        dto.setCustomerCode(entity.getCustomerCode());
        dto.setCustomerName(entity.getCustomerName());
        dto.setStatus(entity.getStatus());
        dto.setType(entity.getType());
        dto.setShortName(entity.getShortName());
        dto.setContactPerson(entity.getContactPerson());
        dto.setPhone(entity.getPhone());
        dto.setAddress(entity.getAddress());
        dto.setEmail(entity.getEmail());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedBy(entity.getUpdatedBy());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
