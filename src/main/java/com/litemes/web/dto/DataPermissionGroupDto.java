package com.litemes.web.dto;

import java.time.LocalDateTime;

/**
 * DTO for data permission group response.
 */
public class DataPermissionGroupDto {

    private Long id;
    private String groupName;
    private String remark;
    private Integer factoryCount;
    private Integer workCenterCount;
    private Integer processCount;
    private Boolean referenced;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getFactoryCount() {
        return factoryCount;
    }

    public void setFactoryCount(Integer factoryCount) {
        this.factoryCount = factoryCount;
    }

    public Integer getWorkCenterCount() {
        return workCenterCount;
    }

    public void setWorkCenterCount(Integer workCenterCount) {
        this.workCenterCount = workCenterCount;
    }

    public Integer getProcessCount() {
        return processCount;
    }

    public void setProcessCount(Integer processCount) {
        this.processCount = processCount;
    }

    public Boolean getReferenced() {
        return referenced;
    }

    public void setReferenced(Boolean referenced) {
        this.referenced = referenced;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
