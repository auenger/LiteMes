package com.litemes.web.dto;

import jakarta.ws.rs.QueryParam;

/**
 * DTO for process query parameters.
 * Supports fuzzy search by process code/name, exact filter by work center, factory (cascading), and status.
 */
public class ProcessQueryDto {

    @QueryParam("processCode")
    private String processCode;

    @QueryParam("name")
    private String name;

    @QueryParam("workCenterId")
    private Long workCenterId;

    @QueryParam("factoryId")
    private Long factoryId;

    @QueryParam("status")
    private Integer status;

    @QueryParam("page")
    private Integer page = 1;

    @QueryParam("size")
    private Integer size = 10;

    public String getProcessCode() {
        return processCode;
    }

    public void setProcessCode(String processCode) {
        this.processCode = processCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getWorkCenterId() {
        return workCenterId;
    }

    public void setWorkCenterId(Long workCenterId) {
        this.workCenterId = workCenterId;
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
