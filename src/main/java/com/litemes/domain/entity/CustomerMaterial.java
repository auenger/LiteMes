package com.litemes.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * CustomerMaterial entity.
 * Represents the association between a customer and a material.
 * Used to link customers with their finished product materials for outbound confirmation.
 */
@TableName("customer_material")
public class CustomerMaterial extends SoftDeleteEntity {

    private Long customerId;

    private Long materialId;

    public CustomerMaterial() {
    }

    public CustomerMaterial(Long customerId, Long materialId) {
        this.customerId = customerId;
        this.materialId = materialId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }
}
