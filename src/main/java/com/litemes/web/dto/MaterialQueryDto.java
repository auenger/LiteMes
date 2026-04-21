package com.litemes.web.dto;

import jakarta.ws.rs.QueryParam;

/**
 * DTO for material master query parameters.
 * Supports fuzzy search by code/name and exact filter by category/basicCategory/status.
 */
public class MaterialQueryDto {

    @QueryParam("materialCode")
    private String materialCode;

    @QueryParam("materialName")
    private String materialName;

    @QueryParam("categoryId")
    private Long categoryId;

    @QueryParam("basicCategory")
    private String basicCategory;

    @QueryParam("status")
    private Integer status;

    @QueryParam("page")
    private Integer page = 1;

    @QueryParam("size")
    private Integer size = 10;

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getBasicCategory() {
        return basicCategory;
    }

    public void setBasicCategory(String basicCategory) {
        this.basicCategory = basicCategory;
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
