package com.litemes.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for updating an equipment model.
 * Model code cannot be modified.
 * Equipment type ID can be changed (redundant type_code/type_name will be updated).
 */
public class EquipmentModelUpdateDto {

    @Size(max = 50, message = "设备型号名称长度不能超过50个字符")
    private String modelName;

    @NotNull(message = "设备类型不能为空")
    private Long equipmentTypeId;

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Long getEquipmentTypeId() {
        return equipmentTypeId;
    }

    public void setEquipmentTypeId(Long equipmentTypeId) {
        this.equipmentTypeId = equipmentTypeId;
    }
}
