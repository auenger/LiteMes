package com.litemes.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO for updating an equipment ledger entry.
 * Equipment code cannot be modified.
 * Equipment model, factory, running/management status can be changed.
 * Redundant type/model/factory fields are auto-synced on change.
 */
public class EquipmentLedgerUpdateDto {

    @Size(max = 50, message = "设备名称长度不能超过50个字符")
    private String equipmentName;

    @NotNull(message = "设备型号不能为空")
    private Long equipmentModelId;

    @NotNull(message = "运行状态不能为空")
    private String runningStatus;

    @NotNull(message = "管理状态不能为空")
    private String manageStatus;

    @NotNull(message = "工厂不能为空")
    private Long factoryId;

    @Size(max = 100, message = "生产厂家长度不能超过100个字符")
    private String manufacturer;

    @NotNull(message = "入场时间不能为空")
    private LocalDate commissioningDate;

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
}
