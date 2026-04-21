package com.litemes.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a shift schedule.
 */
public class ShiftScheduleCreateDto {

    @NotBlank(message = "班制编码不能为空")
    @Size(max = 50, message = "班制编码不能超过50个字符")
    private String shiftCode;

    @NotBlank(message = "班制名称不能为空")
    @Size(max = 50, message = "班制名称不能超过50个字符")
    private String name;

    @NotNull(message = "是否默认不能为空")
    private Integer isDefault;

    public String getShiftCode() {
        return shiftCode;
    }

    public void setShiftCode(String shiftCode) {
        this.shiftCode = shiftCode;
    }

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
}
