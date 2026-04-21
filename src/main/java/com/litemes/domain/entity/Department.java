package com.litemes.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * Department entity.
 * Represents a department in the enterprise organization hierarchy.
 * A department belongs to a factory and can have a parent department (tree structure).
 * Department code is immutable after creation.
 */
@TableName("department")
public class Department extends SoftDeleteEntity {

    private String departmentCode;

    private String name;

    private Long factoryId;

    private Long parentId;

    private Integer sortOrder;

    private Integer status;

    public Department() {
    }

    public Department(String departmentCode, String name, Long factoryId, Long parentId, Integer sortOrder) {
        this.departmentCode = departmentCode;
        this.name = name;
        this.factoryId = factoryId;
        this.parentId = parentId;
        this.sortOrder = sortOrder != null ? sortOrder : 0;
        this.status = 1;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
