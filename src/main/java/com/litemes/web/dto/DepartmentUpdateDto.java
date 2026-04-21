package com.litemes.web.dto;

import jakarta.validation.constraints.Size;

/**
 * DTO for updating a department.
 * Department code cannot be modified.
 */
public class DepartmentUpdateDto {

    @Size(max = 50, message = "部门名称长度不能超过50个字符")
    private String name;

    private Long factoryId;

    private Long parentId;

    private Integer sortOrder;

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
}
