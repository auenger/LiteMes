package com.litemes.web.dto;

import jakarta.validation.constraints.Size;

/**
 * DTO for updating a shift schedule.
 * Shift code is immutable and cannot be changed.
 */
public class ShiftScheduleUpdateDto {

    @Size(max = 50, message = "班制名称不能超过50个字符")
    private String name;

    private Integer isDefault;

    private Integer status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
