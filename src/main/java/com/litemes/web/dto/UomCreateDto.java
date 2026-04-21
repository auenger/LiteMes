package com.litemes.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * DTO for creating a unit of measure.
 * Uom code is required and immutable after creation.
 */
public class UomCreateDto {

    @NotBlank(message = "单位编码不能为空")
    @Size(max = 50, message = "单位编码长度不能超过50个字符")
    private String uomCode;

    @NotBlank(message = "单位名称不能为空")
    @Size(max = 50, message = "单位名称长度不能超过50个字符")
    private String uomName;

    private BigDecimal precision;

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

    public BigDecimal getPrecision() {
        return precision;
    }

    public void setPrecision(BigDecimal precision) {
        this.precision = precision;
    }
}
