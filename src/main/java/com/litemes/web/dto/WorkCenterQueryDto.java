package com.litemes.web.dto;

import jakarta.ws.rs.QueryParam;

/**
 * DTO for work center query parameters.
 * Supports fuzzy search by work center code/name, exact filter by factory and status.
 * Note: Quarkus RESTEasy Reactive requires @QueryParam on fields for @BeanParam.
 */
public class WorkCenterQueryDto {

    @QueryParam("workCenterCode")
    private String workCenterCode;

    @QueryParam("name")
    private String name;

    @QueryParam("factoryId")
    private Long factoryId;

    @QueryParam("status")
    private Integer status;

    @QueryParam("page")
    private Integer page = 1;

    @QueryParam("size")
    private Integer size = 10;

    public String getWorkCenterCode() {
        return workCenterCode;
    }

    public void setWorkCenterCode(String workCenterCode) {
        this.workCenterCode = workCenterCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(Long factoryId) {
        this.factoryId = factoryId;
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
