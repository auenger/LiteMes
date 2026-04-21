package com.litemes.web.dto;

import jakarta.ws.rs.QueryParam;

/**
 * DTO for material category query parameters.
 * Supports fuzzy search by category code/name, exact filter by status.
 */
public class MaterialCategoryQueryDto {

    @QueryParam("categoryCode")
    private String categoryCode;

    @QueryParam("categoryName")
    private String categoryName;

    @QueryParam("status")
    private Integer status;

    @QueryParam("page")
    private Integer page = 1;

    @QueryParam("size")
    private Integer size = 10;

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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
