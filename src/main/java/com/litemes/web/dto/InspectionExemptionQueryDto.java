package com.litemes.web.dto;

import jakarta.ws.rs.QueryParam;

/**
 * DTO for inspection exemption query parameters.
 * Supports filtering by material, supplier, and status.
 */
public class InspectionExemptionQueryDto {

    @QueryParam("materialId")
    private Long materialId;

    @QueryParam("supplierId")
    private Long supplierId;

    @QueryParam("status")
    private Integer status;

    @QueryParam("page")
    private Integer page = 1;

    @QueryParam("size")
    private Integer size = 10;

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
