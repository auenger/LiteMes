package com.litemes.web.dto;

import jakarta.ws.rs.QueryParam;

/**
 * DTO for equipment ledger query parameters.
 * Supports fuzzy search by equipment code/name, exact filter by type, model,
 * running status, management status, factory, and status.
 */
public class EquipmentLedgerQueryDto {

    @QueryParam("equipmentCode")
    private String equipmentCode;

    @QueryParam("equipmentName")
    private String equipmentName;

    @QueryParam("equipmentTypeId")
    private Long equipmentTypeId;

    @QueryParam("equipmentModelId")
    private Long equipmentModelId;

    @QueryParam("runningStatus")
    private String runningStatus;

    @QueryParam("manageStatus")
    private String manageStatus;

    @QueryParam("factoryId")
    private Long factoryId;

    @QueryParam("status")
    private Integer status;

    @QueryParam("page")
    private Integer page = 1;

    @QueryParam("size")
    private Integer size = 10;

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

    public Long getEquipmentTypeId() {
        return equipmentTypeId;
    }

    public void setEquipmentTypeId(Long equipmentTypeId) {
        this.equipmentTypeId = equipmentTypeId;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
