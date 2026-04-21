package com.litemes.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDate;

/**
 * Equipment ledger entity.
 * Represents a physical equipment asset in PCB manufacturing.
 * Each equipment has a unique code, linked to an equipment model (with type),
 * belongs to a factory, and tracks running/management status.
 * Equipment code is immutable after creation.
 * Redundant model/type/factory fields avoid JOIN queries in list display.
 */
@TableName("equipment_ledger")
public class EquipmentLedger extends SoftDeleteEntity {

    private String equipmentCode;

    private String equipmentName;

    private Long equipmentModelId;

    private String modelCode;

    private String modelName;

    private Long equipmentTypeId;

    private String typeCode;

    private String typeName;

    private String runningStatus;

    private String manageStatus;

    private Long factoryId;

    private String factoryCode;

    private String factoryName;

    private String manufacturer;

    private LocalDate commissioningDate;

    private Integer status;

    public EquipmentLedger() {
    }

    public EquipmentLedger(String equipmentCode, String equipmentName, Long equipmentModelId,
                           String modelCode, String modelName, Long equipmentTypeId,
                           String typeCode, String typeName, String runningStatus,
                           String manageStatus, Long factoryId, String factoryCode,
                           String factoryName, LocalDate commissioningDate) {
        this.equipmentCode = equipmentCode;
        this.equipmentName = equipmentName;
        this.equipmentModelId = equipmentModelId;
        this.modelCode = modelCode;
        this.modelName = modelName;
        this.equipmentTypeId = equipmentTypeId;
        this.typeCode = typeCode;
        this.typeName = typeName;
        this.runningStatus = runningStatus;
        this.manageStatus = manageStatus;
        this.factoryId = factoryId;
        this.factoryCode = factoryCode;
        this.factoryName = factoryName;
        this.commissioningDate = commissioningDate;
        this.status = 1;
    }

    public String getEquipmentCode() {
        return equipmentCode;
    }

    public void setEquipmentCode(String equipmentCode) {
        this.equipmentCode = equipmentCode;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public Long getEquipmentModelId() {
        return equipmentModelId;
    }

    public void setEquipmentModelId(Long equipmentModelId) {
        this.equipmentModelId = equipmentModelId;
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

    public String getRunningStatus() {
        return runningStatus;
    }

    public void setRunningStatus(String runningStatus) {
        this.runningStatus = runningStatus;
    }

    public String getManageStatus() {
        return manageStatus;
    }

    public void setManageStatus(String manageStatus) {
        this.manageStatus = manageStatus;
    }

    public Long getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(Long factoryId) {
        this.factoryId = factoryId;
    }

    public String getFactoryCode() {
        return factoryCode;
    }

    public void setFactoryCode(String factoryCode) {
        this.factoryCode = factoryCode;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public LocalDate getCommissioningDate() {
        return commissioningDate;
    }

    public void setCommissioningDate(LocalDate commissioningDate) {
        this.commissioningDate = commissioningDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
