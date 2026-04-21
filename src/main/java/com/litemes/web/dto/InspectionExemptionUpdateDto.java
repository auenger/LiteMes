package com.litemes.web.dto;

import java.time.LocalDate;

/**
 * DTO for updating an inspection exemption rule.
 * Material, supplier, and validity period can all be modified.
 */
public class InspectionExemptionUpdateDto {

    private Long materialId;

    private Long supplierId;

    private LocalDate validFrom;

    private LocalDate validTo;

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
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
}
