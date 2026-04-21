package com.litemes.web.dto;

import jakarta.ws.rs.QueryParam;

/**
 * Query DTO for AuditLog search.
 * Supports filtering by table name, record ID, and time range.
 * Note: Quarkus RESTEasy Reactive requires @QueryParam on fields for @BeanParam.
 */
public class AuditLogQueryDto {

    @QueryParam("tableName")
    private String tableName;

    @QueryParam("recordId")
    private Long recordId;

    @QueryParam("startTime")
    private String startTime;

    @QueryParam("endTime")
    private String endTime;

    @QueryParam("page")
    private Integer page = 1;

    @QueryParam("size")
    private Integer size = 10;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
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
