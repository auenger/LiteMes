package com.litemes.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * Equipment type entity.
 * Represents a category classification for equipment in PCB manufacturing.
 * Type code is immutable after creation.
 */
@TableName("equipment_type")
public class EquipmentType extends SoftDeleteEntity {

    private String typeCode;

    private String typeName;

    private Integer status;

    public EquipmentType() {
    }

    public EquipmentType(String typeCode, String typeName) {
        this.typeCode = typeCode;
        this.typeName = typeName;
        this.status = 1;
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
