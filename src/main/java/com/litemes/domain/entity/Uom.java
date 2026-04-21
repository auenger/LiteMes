package com.litemes.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;

/**
 * Unit of Measure entity.
 * Represents a measurement unit used in PCB manufacturing.
 * Unit code is immutable after creation.
 */
@TableName("uom")
public class Uom extends SoftDeleteEntity {

    private String uomCode;

    private String uomName;

    private Integer status;

    private BigDecimal uomPrecision;

    public Uom() {
    }

    public Uom(String uomCode, String uomName, BigDecimal uomPrecision) {
        this.uomCode = uomCode;
        this.uomName = uomName;
        this.uomPrecision = uomPrecision;
        this.status = 1;
    }

    public String getUomCode() {
        return uomCode;
    }

    public void setUomCode(String uomCode) {
        this.uomCode = uomCode;
    }

    public String getUomName() {
        return uomName;
    }

    public void setUomName(String uomName) {
        this.uomName = uomName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public BigDecimal getUomPrecision() {
        return uomPrecision;
    }

    public void setUomPrecision(BigDecimal uomPrecision) {
        this.uomPrecision = uomPrecision;
    }
}
