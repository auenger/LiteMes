package com.litemes.web.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO for updating a unit of measure conversion.
 */
public class UomConversionUpdateDto {

    private Long fromUomId;

    private Long toUomId;

    private BigDecimal conversionRate;

    public Long getFromUomId() {
        return fromUomId;
    }

    public void setFromUomId(Long fromUomId) {
        this.fromUomId = fromUomId;
    }

    public Long getToUomId() {
        return toUomId;
    }

    public void setToUomId(Long toUomId) {
        this.toUomId = toUomId;
    }

    public BigDecimal getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(BigDecimal conversionRate) {
        this.conversionRate = conversionRate;
    }
}
