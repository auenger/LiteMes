package com.litemes.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a work center.
 * Work center code is required and immutable after creation.
 * Factory ID is required to establish the factory-work center relationship.
 */
public class WorkCenterCreateDto {

    @NotBlank(message = "工作中心编码不能为空")
    @Size(max = 50, message = "工作中心编码长度不能超过50个字符")
    private String workCenterCode;

    @NotBlank(message = "工作中心名称不能为空")
    @Size(max = 50, message = "工作中心名称长度不能超过50个字符")
    private String name;

    @NotNull(message = "请选择所属工厂")
    private Long factoryId;

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
}
