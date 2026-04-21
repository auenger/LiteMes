package com.litemes.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;

/**
 * Unit of Measure Conversion entity.
 * Represents the conversion rate between two units of measure.
 * fromUomId + toUomId must be unique.
 */
@TableName("uom_conversion")
public class UomConversion extends SoftDeleteEntity {

    private Long fromUomId;

    private String fromUomCode;

    private String fromUomName;

    private Long toUomId;

    private String toUomCode;

    private String toUomName;

    private BigDecimal conversionRate;

    private Integer status;

    public UomConversion() {
    }

    public UomConversion(Long fromUomId, String fromUomCode, String fromUomName,
                         Long toUomId, String toUomCode, String toUomName,
                         BigDecimal conversionRate) {
        this.fromUomId = fromUomId;
        this.fromUomCode = fromUomCode;
        this.fromUomName = fromUomName;
        this.toUomId = toUomId;
        this.toUomCode = toUomCode;
        this.toUomName = toUomName;
        this.conversionRate = conversionRate;
        this.status = 1;
    }

    public Long getFromUomId() {
        return fromUomId;
    }

    public void setFromUomId(Long fromUomId) {
        this.fromUomId = fromUomId;
    }

    public String getFromUomCode() {
        return fromUomCode;
    }

    public void setFromUomCode(String fromUomCode) {
        this.fromUomCode = fromUomCode;
    }

    public String getFromUomName() {
        return fromUomName;
    }

    public void setFromUomName(String fromUomName) {
        this.fromUomName = fromUomName;
    }

    public Long getToUomId() {
        return toUomId;
    }

    public void setToUomId(Long toUomId) {
        this.toUomId = toUomId;
    }

    public String getToUomCode() {
        return toUomCode;
    }

    public void setToUomCode(String toUomCode) {
        this.toUomCode = toUomCode;
    }

    public String getToUomName() {
        return toUomName;
    }

    public void setToUomName(String toUomName) {
        this.toUomName = toUomName;
    }

    public BigDecimal getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(BigDecimal conversionRate) {
        this.conversionRate = conversionRate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
