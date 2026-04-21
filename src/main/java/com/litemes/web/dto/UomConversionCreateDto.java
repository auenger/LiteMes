package com.litemes.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * DTO for creating a unit of measure conversion.
 */
public class UomConversionCreateDto {

    @NotNull(message = "原单位不能为空")
    private Long fromUomId;

    @NotNull(message = "目标单位不能为空")
    private Long toUomId;

    @NotNull(message = "换算率不能为空")
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
