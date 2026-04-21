package com.litemes.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * MaterialCategory entity.
 * Represents a category in the material classification hierarchy.
 * Supports multi-level tree structure via parentId self-reference.
 * Category code is immutable after creation.
 */
@TableName("material_category")
public class MaterialCategory extends SoftDeleteEntity {

    private String categoryCode;

    private String categoryName;

    private Boolean isQualityCategory;

    private Long parentId;

    private Integer status;

    public MaterialCategory() {
    }

    public MaterialCategory(String categoryCode, String categoryName, Boolean isQualityCategory, Long parentId) {
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
        this.isQualityCategory = isQualityCategory != null ? isQualityCategory : false;
        this.parentId = parentId;
        this.status = 1;
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
}
