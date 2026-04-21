package com.litemes.application.service;

import com.litemes.domain.entity.Company;
import com.litemes.domain.entity.Department;
import com.litemes.domain.entity.Factory;
import com.litemes.domain.entity.ShiftSchedule;
import com.litemes.domain.repository.CompanyRepository;
import com.litemes.domain.repository.DepartmentRepository;
import com.litemes.domain.repository.FactoryRepository;
import com.litemes.domain.repository.ShiftScheduleRepository;
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
}
