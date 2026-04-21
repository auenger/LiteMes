package com.litemes.web.dto;

import jakarta.validation.constraints.Size;

/**
 * DTO for updating a material category.
 * Category code cannot be modified.
 * Only categoryName and isQualityCategory can be changed.
 */
public class MaterialCategoryUpdateDto {

    @Size(max = 50, message = "分类名称长度不能超过50个字符")
    private String categoryName;

    private Boolean isQualityCategory;

    private Long parentId;

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
}
