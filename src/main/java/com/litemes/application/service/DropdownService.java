package com.litemes.application.service;

import com.litemes.domain.entity.Company;
import com.litemes.domain.entity.Customer;
import com.litemes.domain.entity.DataPermissionGroup;
import com.litemes.domain.entity.Department;
import com.litemes.domain.entity.Factory;
import com.litemes.domain.entity.MaterialCategory;
import com.litemes.domain.entity.ShiftSchedule;
import com.litemes.domain.entity.EquipmentType;
import com.litemes.domain.entity.EquipmentModel;
import com.litemes.domain.entity.MaterialMaster;
import com.litemes.domain.entity.Process;
import com.litemes.domain.entity.Supplier;
import com.litemes.domain.entity.Uom;
import com.litemes.domain.entity.WorkCenter;
import com.litemes.domain.repository.CompanyRepository;
import com.litemes.domain.repository.CustomerRepository;
import com.litemes.domain.repository.DataPermissionGroupRepository;
import com.litemes.domain.repository.DepartmentRepository;
import com.litemes.domain.repository.EquipmentModelRepository;
import com.litemes.domain.repository.EquipmentTypeRepository;
import com.litemes.domain.repository.FactoryRepository;
import com.litemes.domain.repository.MaterialCategoryRepository;
import com.litemes.domain.repository.MaterialMasterRepository;
import com.litemes.domain.repository.ProcessRepository;
import com.litemes.domain.repository.ShiftScheduleRepository;
import com.litemes.domain.repository.SupplierRepository;
import com.litemes.domain.repository.UomRepository;
import com.litemes.domain.repository.WorkCenterRepository;
import com.litemes.web.dto.DropdownItem;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Application service for common dropdown data.
 * Provides dropdown options for company, factory, department, shift-schedule.
 * Supports cascade filtering (company -> factory -> department).
 */
@ApplicationScoped
public class DropdownService {

    private static final Logger LOG = Logger.getLogger(DropdownService.class);

    @Inject
    CompanyRepository companyRepository;

    @Inject
    FactoryRepository factoryRepository;

    @Inject
    DepartmentRepository departmentRepository;

    @Inject
    ShiftScheduleRepository shiftScheduleRepository;

    @Inject
    MaterialCategoryRepository materialCategoryRepository;

    @Inject
    UomRepository uomRepository;

    @Inject
    EquipmentTypeRepository equipmentTypeRepository;

    @Inject
    EquipmentModelRepository equipmentModelRepository;

    @Inject
    MaterialMasterRepository materialMasterRepository;

    @Inject
    CustomerRepository customerRepository;

    @Inject
    SupplierRepository supplierRepository;

    @Inject
    WorkCenterRepository workCenterRepository;

    @Inject
    ProcessRepository processRepository;

    @Inject
    DataPermissionGroupRepository dataPermissionGroupRepository;

    /**
     * Get all active companies as dropdown items.
     */
    public List<DropdownItem> getCompanyDropdown() {
        LOG.debug("Fetching company dropdown");
        return companyRepository.findAllActive().stream()
                .map(c -> new DropdownItem(c.getId(), c.getCompanyCode(), c.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Get active factories as dropdown items.
     * If companyId is provided, only return factories belonging to that company.
     */
    public List<DropdownItem> getFactoryDropdown(Long companyId) {
        LOG.debugf("Fetching factory dropdown, companyId=%s", companyId);
        if (companyId != null) {
            return factoryRepository.findByCompanyId(companyId).stream()
                    .map(f -> new DropdownItem(f.getId(), f.getFactoryCode(), f.getName(), f.getCompanyId()))
                    .collect(Collectors.toList());
        }
        return factoryRepository.findAllActive().stream()
                .map(f -> new DropdownItem(f.getId(), f.getFactoryCode(), f.getName(), f.getCompanyId()))
                .collect(Collectors.toList());
    }

    /**
     * Get active departments as dropdown items.
     * If factoryId is provided, only return departments belonging to that factory.
     */
    public List<DropdownItem> getDepartmentDropdown(Long factoryId) {
        LOG.debugf("Fetching department dropdown, factoryId=%s", factoryId);
        if (factoryId != null) {
            return departmentRepository.findByFactoryId(factoryId).stream()
                    .filter(d -> d.getStatus() == 1)
                    .map(d -> new DropdownItem(d.getId(), d.getDepartmentCode(), d.getName(), d.getParentId()))
                    .collect(Collectors.toList());
        }
        return departmentRepository.findAllActive().stream()
                .map(d -> new DropdownItem(d.getId(), d.getDepartmentCode(), d.getName(), d.getParentId()))
                .collect(Collectors.toList());
    }

    /**
     * Get all active shift schedules as dropdown items.
     */
    public List<DropdownItem> getShiftScheduleDropdown() {
        LOG.debug("Fetching shift schedule dropdown");
        return shiftScheduleRepository.findAllActive().stream()
                .map(s -> new DropdownItem(s.getId(), s.getShiftCode(), s.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Get all active material categories as dropdown items.
     * Supports tree structure with parentId for cascading display.
     */
    public List<DropdownItem> getMaterialCategoryDropdown() {
        LOG.debug("Fetching material category dropdown");
        return materialCategoryRepository.findAllActive().stream()
                .map(c -> new DropdownItem(c.getId(), c.getCategoryCode(), c.getCategoryName(), c.getParentId()))
                .collect(Collectors.toList());
    }

    /**
     * Get all active units of measure as dropdown items.
     */
    public List<DropdownItem> getUomDropdown() {
        LOG.debug("Fetching uom dropdown");
        return uomRepository.findAllActive().stream()
                .map(u -> new DropdownItem(u.getId(), u.getUomCode(), u.getUomName()))
                .collect(Collectors.toList());
    }

    /**
     * Get all active equipment types as dropdown items.
     */
    public List<DropdownItem> getEquipmentTypeDropdown() {
        LOG.debug("Fetching equipment type dropdown");
        return equipmentTypeRepository.findAllActive().stream()
                .map(t -> new DropdownItem(t.getId(), t.getTypeCode(), t.getTypeName()))
                .collect(Collectors.toList());
    }

    /**
     * Get active equipment models as dropdown items.
     * If equipmentTypeId is provided, only return models belonging to that type.
     */
    public List<DropdownItem> getEquipmentModelDropdown(Long equipmentTypeId) {
        LOG.debugf("Fetching equipment model dropdown, equipmentTypeId=%s", equipmentTypeId);
        if (equipmentTypeId != null) {
            return equipmentModelRepository.findByEquipmentTypeId(equipmentTypeId).stream()
                    .map(m -> new DropdownItem(m.getId(), m.getModelCode(), m.getModelName()))
                    .collect(Collectors.toList());
        }
        return equipmentModelRepository.findAllActive().stream()
                .map(m -> new DropdownItem(m.getId(), m.getModelCode(), m.getModelName()))
                .collect(Collectors.toList());
    }

    /**
     * Get all active materials as dropdown items.
     */
    public List<DropdownItem> getMaterialDropdown() {
        LOG.debug("Fetching material dropdown");
        return materialMasterRepository.findAllActive().stream()
                .map(m -> new DropdownItem(m.getId(), m.getMaterialCode(), m.getMaterialName()))
                .collect(Collectors.toList());
    }

    /**
     * Get all active customers as dropdown items.
     */
    public List<DropdownItem> getCustomerDropdown() {
        LOG.debug("Fetching customer dropdown");
        return customerRepository.findAllActive().stream()
                .map(c -> new DropdownItem(c.getId(), c.getCustomerCode(), c.getCustomerName()))
                .collect(Collectors.toList());
    }

    /**
     * Get all active suppliers as dropdown items.
     */
    public List<DropdownItem> getSupplierDropdown() {
        LOG.debug("Fetching supplier dropdown");
        return supplierRepository.findAllActive().stream()
                .map(s -> new DropdownItem(s.getId(), s.getSupplierCode(), s.getSupplierName()))
                .collect(Collectors.toList());
    }

    /**
     * Get active work centers as dropdown items.
     * If factoryId is provided, only return work centers belonging to that factory.
     */
    public List<DropdownItem> getWorkCenterDropdown(Long factoryId) {
        LOG.debugf("Fetching work center dropdown, factoryId=%s", factoryId);
        if (factoryId != null) {
            return workCenterRepository.findByFactoryIdAndStatus(factoryId, 1).stream()
                    .map(wc -> new DropdownItem(wc.getId(), wc.getWorkCenterCode(), wc.getName(), wc.getFactoryId()))
                    .collect(Collectors.toList());
        }
        return workCenterRepository.findAllActive().stream()
                .map(wc -> new DropdownItem(wc.getId(), wc.getWorkCenterCode(), wc.getName(), wc.getFactoryId()))
                .collect(Collectors.toList());
    }

    /**
     * Get all active processes as dropdown items.
     */
    public List<DropdownItem> getProcessDropdown() {
        LOG.debug("Fetching process dropdown");
        return processRepository.findAllActive().stream()
                .map(p -> new DropdownItem(p.getId(), p.getProcessCode(), p.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Get all active data permission groups as dropdown items.
     */
    public List<DropdownItem> getPermissionGroupDropdown() {
        LOG.debug("Fetching permission group dropdown");
        com.baomidou.mybatisplus.core.metadata.IPage<DataPermissionGroup> page =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 1000);
        return dataPermissionGroupRepository.findPage(page, null).getRecords().stream()
                .map(g -> new DropdownItem(g.getId(), g.getGroupName(), g.getGroupName()))
                .collect(Collectors.toList());
    }
}
