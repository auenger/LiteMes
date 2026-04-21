package com.litemes.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * WorkCenter entity.
 * Represents a work center in the manufacturing organization hierarchy.
 * A work center belongs to a factory. Work center code is immutable after creation.
 */
@TableName("work_center")
public class WorkCenter extends SoftDeleteEntity {

    private String workCenterCode;

    private String name;

    private Long factoryId;

    private Integer status;

    public WorkCenter() {
    }

    public WorkCenter(String workCenterCode, String name, Long factoryId) {
        this.workCenterCode = workCenterCode;
        this.name = name;
        this.factoryId = factoryId;
        this.status = 1;
    }

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
}
