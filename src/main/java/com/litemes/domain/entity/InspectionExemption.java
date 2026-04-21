package com.litemes.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDate;

/**
 * InspectionExemption entity.
 * Represents an inspection exemption rule for materials.
 * Combines supplier + material + validity period to define exemption rules.
 */
@TableName("inspection_exemption")
public class InspectionExemption extends SoftDeleteEntity {

    private Long materialId;

    private String materialCode;

    private String materialName;

    private Long supplierId;

    private String supplierCode;

    private String supplierName;

    private Integer status;

    private LocalDate validFrom;

    private LocalDate validTo;

    public InspectionExemption() {
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDate getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDate validTo) {
        this.validTo = validTo;
    }

    /**
     * Check if this exemption rule has expired based on validTo date.
     * A rule is expired if validTo is before today.
     * Rules with no validTo (null) never expire.
     */
    public boolean isExpired() {
        return validTo != null && validTo.isBefore(LocalDate.now());
    }
}
