package com.litemes.web.dto;

import jakarta.ws.rs.QueryParam;

/**
 * DTO for unit of measure conversion query parameters.
 * Supports fuzzy search by from/to uom code/name and exact filter by status.
 */
public class UomConversionQueryDto {

    @QueryParam("fromUom")
    private String fromUom;

    @QueryParam("toUom")
    private String toUom;

    @QueryParam("status")
    private Integer status;

    @QueryParam("page")
    private Integer page = 1;

    @QueryParam("size")
    private Integer size = 10;

    public String getFromUom() {
        return fromUom;
    }

    public void setFromUom(String fromUom) {
        this.fromUom = fromUom;
    }

    public String getToUom() {
        return toUom;
    }

    public void setToUom(String toUom) {
        this.toUom = toUom;
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
