package com.litemes.web.dto;

import jakarta.validation.constraints.Size;

/**
 * DTO for updating an equipment type.
 * Type code cannot be modified.
 */
public class EquipmentTypeUpdateDto {

    @Size(max = 50, message = "设备类型名称长度不能超过50个字符")
    private String typeName;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
