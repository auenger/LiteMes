package com.litemes.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * SupplierMaterial entity.
 * Represents the association between a supplier and a material.
 * Used to link suppliers with their supply materials for procurement and quality tracing.
 */
@TableName("supplier_material")
public class SupplierMaterial extends SoftDeleteEntity {

    private Long supplierId;

    private Long materialId;

    public SupplierMaterial() {
    }

    public SupplierMaterial(Long supplierId, Long materialId) {
        this.supplierId = supplierId;
        this.materialId = materialId;
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
}
