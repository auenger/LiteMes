package com.litemes.web.dto;

/**
 * DTO for supplier-material association.
 * Used for both requests (link material to supplier) and responses (show linked materials).
 */
public class SupplierMaterialDto {

    private Long id;
    private Long supplierId;
    private Long materialId;
    private String materialCode;
    private String materialName;

    public SupplierMaterialDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
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
}
