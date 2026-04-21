package com.litemes.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for unit of measure conversion entity response.
 */
public class UomConversionDto {

    private Long id;
    private Long fromUomId;
    private String fromUomCode;
    private String fromUomName;
    private Long toUomId;
    private String toUomCode;
    private String toUomName;
    private BigDecimal conversionRate;
    private Integer status;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
