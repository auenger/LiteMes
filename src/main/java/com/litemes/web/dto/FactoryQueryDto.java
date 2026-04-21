package com.litemes.web.dto;

import jakarta.ws.rs.QueryParam;

/**
 * DTO for factory query parameters.
 * Supports fuzzy search by factory code/name, exact filter by company and status.
 * Note: Quarkus RESTEasy Reactive requires @QueryParam on fields for @BeanParam.
 */
public class FactoryQueryDto {

    @QueryParam("factoryCode")
    private String factoryCode;

    @QueryParam("name")
    private String name;

    @QueryParam("companyId")
    private Long companyId;

    @QueryParam("status")
    private Integer status;

    @QueryParam("page")
    private Integer page = 1;

    @QueryParam("size")
    private Integer size = 10;

    public String getFactoryCode() {
        return factoryCode;
    }

    public void setFactoryCode(String factoryCode) {
        this.factoryCode = factoryCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
