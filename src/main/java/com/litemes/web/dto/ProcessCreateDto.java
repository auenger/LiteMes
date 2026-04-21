package com.litemes.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a process.
 * Process code is required and immutable after creation.
 * Work center ID is required to establish the work center-process relationship.
 */
public class ProcessCreateDto {

    @NotBlank(message = "工序编码不能为空")
    @Size(max = 50, message = "工序编码长度不能超过50个字符")
    private String processCode;

    @NotBlank(message = "工序名称不能为空")
    @Size(max = 50, message = "工序名称长度不能超过50个字符")
    private String name;

    @NotNull(message = "请选择所属工作中心")
    private Long workCenterId;

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
}
