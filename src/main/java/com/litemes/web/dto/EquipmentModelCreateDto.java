package com.litemes.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating an equipment model.
 * Model code is required and immutable after creation.
 * Equipment type ID is required for linking the model to a type.
 */
public class EquipmentModelCreateDto {

    @NotBlank(message = "设备型号编码不能为空")
    @Size(max = 50, message = "设备型号编码长度不能超过50个字符")
    private String modelCode;

    @NotBlank(message = "设备型号名称不能为空")
    @Size(max = 50, message = "设备型号名称长度不能超过50个字符")
    private String modelName;

    @NotNull(message = "设备类型不能为空")
    private Long equipmentTypeId;

    public String getModelCode() {
        return modelCode;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

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
