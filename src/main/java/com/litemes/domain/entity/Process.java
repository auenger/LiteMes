package com.litemes.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * Process entity.
 * Represents a manufacturing process step in the MES system.
 * A process belongs to a work center. Process code is immutable after creation.
 */
@TableName("process")
public class Process extends SoftDeleteEntity {

    private String processCode;

    private String name;

    private Long workCenterId;

    private Integer status;

    public Process() {
    }

    public Process(String processCode, String name, Long workCenterId) {
        this.processCode = processCode;
        this.name = name;
        this.workCenterId = workCenterId;
        this.status = 1;
    }

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
