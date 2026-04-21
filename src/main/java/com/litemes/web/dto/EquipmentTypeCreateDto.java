package com.litemes.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating an equipment type.
 * Type code is required and immutable after creation.
 */
public class EquipmentTypeCreateDto {

    @NotBlank(message = "设备类型编码不能为空")
    @Size(max = 50, message = "设备类型编码长度不能超过50个字符")
    private String typeCode;

    @NotBlank(message = "设备类型名称不能为空")
    @Size(max = 50, message = "设备类型名称长度不能超过50个字符")
    private String typeName;

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
