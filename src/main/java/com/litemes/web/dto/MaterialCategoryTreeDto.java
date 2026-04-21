package com.litemes.web.dto;

import java.util.List;

/**
 * Tree node DTO for material category hierarchy display.
 */
public class MaterialCategoryTreeDto {

    private Long id;
    private String categoryCode;
    private String categoryName;
    private Boolean isQualityCategory;
    private Long parentId;
    private Integer status;
    private List<MaterialCategoryTreeDto> children;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Boolean getIsQualityCategory() {
        return isQualityCategory;
    }

    public void setIsQualityCategory(Boolean isQualityCategory) {
        this.isQualityCategory = isQualityCategory;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<MaterialCategoryTreeDto> getChildren() {
        return children;
    }

    public void setChildren(List<MaterialCategoryTreeDto> children) {
        this.children = children;
    }
}
