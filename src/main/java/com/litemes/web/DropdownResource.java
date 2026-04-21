package com.litemes.web;

import com.litemes.application.service.DropdownService;
import com.litemes.web.dto.DropdownItem;
import com.litemes.web.dto.R;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

/**
 * REST API for common dropdown data.
 * Provides dropdown options for company, factory, department, shift-schedule.
 * Supports cascade filtering via query parameters.
 */
@Path("/api/dropdown")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Dropdown", description = "通用下拉接口 API")
public class DropdownResource {

    @Inject
    DropdownService dropdownService;

    @GET
    @Path("/companies")
    @Operation(summary = "获取公司下拉列表", description = "返回所有启用状态的公司列表")
    public R<List<DropdownItem>> companyDropdown() {
        return R.ok(dropdownService.getCompanyDropdown());
    }

    @GET
    @Path("/factories")
    @Operation(summary = "获取工厂下拉列表", description = "返回启用状态的工厂列表，支持按公司ID筛选")
    public R<List<DropdownItem>> factoryDropdown(@QueryParam("companyId") Long companyId) {
        return R.ok(dropdownService.getFactoryDropdown(companyId));
    }

    @GET
    @Path("/departments")
    @Operation(summary = "获取部门下拉列表", description = "返回启用状态的部门列表，支持按工厂ID筛选")
    public R<List<DropdownItem>> departmentDropdown(@QueryParam("factoryId") Long factoryId) {
        return R.ok(dropdownService.getDepartmentDropdown(factoryId));
    }

    @GET
    @Path("/shift-schedules")
    @Operation(summary = "获取班制下拉列表", description = "返回所有启用状态的班制列表")
    public R<List<DropdownItem>> shiftScheduleDropdown() {
        return R.ok(dropdownService.getShiftScheduleDropdown());
    }
}
