package com.litemes.web.dto;

import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * DTO for updating a unit of measure.
 * Uom code cannot be modified.
 */
public class UomUpdateDto {

    @Size(max = 50, message = "单位名称长度不能超过50个字符")
    private String uomName;

    private BigDecimal precision;

    public String getUomName() {
        return uomName;
    }

    public void setUomName(String uomName) {
        this.uomName = uomName;
    }

    public BigDecimal getPrecision() {
        return precision;
    }

    public void setPrecision(BigDecimal precision) {
        this.precision = precision;
    }
}
