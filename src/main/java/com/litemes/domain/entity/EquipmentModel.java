package com.litemes.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * Equipment model entity.
 * Represents a specific model within an equipment type classification.
 * Model code is immutable after creation.
 * Redundant type_code/type_name fields avoid JOIN queries in list display.
 */
@TableName("equipment_model")
public class EquipmentModel extends SoftDeleteEntity {

    private String modelCode;

    private String modelName;

    private Long equipmentTypeId;

    private String typeCode;

    private String typeName;

    private Integer status;

    public EquipmentModel() {
    }

    public EquipmentModel(String modelCode, String modelName, Long equipmentTypeId, String typeCode, String typeName) {
        this.modelCode = modelCode;
        this.modelName = modelName;
        this.equipmentTypeId = equipmentTypeId;
        this.typeCode = typeCode;
        this.typeName = typeName;
        this.status = 1;
    }

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
