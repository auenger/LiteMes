package com.litemes.web.dto;

import jakarta.ws.rs.QueryParam;

/**
 * DTO for company query parameters.
 * Supports fuzzy search by company code/name and exact filter by status.
 * Note: Quarkus RESTEasy Reactive requires @QueryParam on fields for @BeanParam.
 */
public class CompanyQueryDto {

    @QueryParam("companyCode")
    private String companyCode;

    @QueryParam("name")
    private String name;

    @QueryParam("status")
    private Integer status;

    @QueryParam("page")
    private Integer page = 1;

    @QueryParam("size")
    private Integer size = 10;

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
