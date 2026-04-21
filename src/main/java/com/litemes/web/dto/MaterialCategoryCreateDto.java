package com.litemes.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a material category.
 * Category code is required and immutable after creation.
 * Parent ID is optional (null for top-level categories).
 */
public class MaterialCategoryCreateDto {

    @NotBlank(message = "分类编码不能为空")
    @Size(max = 50, message = "分类编码长度不能超过50个字符")
    private String categoryCode;

    @NotBlank(message = "分类名称不能为空")
    @Size(max = 50, message = "分类名称长度不能超过50个字符")
    private String categoryName;

    private Boolean isQualityCategory;

    private Long parentId;

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
}
