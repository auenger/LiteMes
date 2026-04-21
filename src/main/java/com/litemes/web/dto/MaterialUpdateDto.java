package com.litemes.web.dto;

import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * DTO for updating a material master.
 * Material code cannot be modified.
 */
public class MaterialUpdateDto {

    @Size(max = 255, message = "物料名称长度不能超过255个字符")
    private String materialName;

    private String basicCategory;
    private Long categoryId;
    private String attributeCategory;
    private Long uomId;
    private BigDecimal size;
    private BigDecimal length;
    private BigDecimal width;

    @Size(max = 50, message = "型号长度不能超过50个字符")
    private String model;

    @Size(max = 50, message = "规格长度不能超过50个字符")
    private String specification;

    private BigDecimal thickness;

    @Size(max = 50, message = "颜色长度不能超过50个字符")
    private String color;

    @Size(max = 50, message = "TG值长度不能超过50个字符")
    private String tgValue;

    @Size(max = 50, message = "铜厚长度不能超过50个字符")
    private String copperThickness;

    private Boolean isCopperContained;
    private BigDecimal diameter;
    private BigDecimal bladeLength;
    private BigDecimal totalLength;

    @Size(max = 50, message = "扩展字段长度不能超过50个字符")
    private String ext1;
    @Size(max = 50, message = "扩展字段长度不能超过50个字符")
    private String ext2;
    @Size(max = 50, message = "扩展字段长度不能超过50个字符")
    private String ext3;
    @Size(max = 50, message = "扩展字段长度不能超过50个字符")
    private String ext4;
    @Size(max = 50, message = "扩展字段长度不能超过50个字符")
    private String ext5;

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getBasicCategory() {
        return basicCategory;
    }

    public void setBasicCategory(String basicCategory) {
        this.basicCategory = basicCategory;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getAttributeCategory() {
        return attributeCategory;
    }

    public void setAttributeCategory(String attributeCategory) {
        this.attributeCategory = attributeCategory;
    }

    public Long getUomId() {
        return uomId;
    }

    public void setUomId(Long uomId) {
        this.uomId = uomId;
    }

    public BigDecimal getSize() {
        return size;
    }

    public void setSize(BigDecimal size) {
        this.size = size;
    }

    public BigDecimal getLength() {
        return length;
    }

    public void setLength(BigDecimal length) {
        this.length = length;
    }

    public BigDecimal getWidth() {
        return width;
    }

    public void setWidth(BigDecimal width) {
        this.width = width;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public BigDecimal getThickness() {
        return thickness;
    }

    public void setThickness(BigDecimal thickness) {
        this.thickness = thickness;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTgValue() {
        return tgValue;
    }

    public void setTgValue(String tgValue) {
        this.tgValue = tgValue;
    }

    public String getCopperThickness() {
        return copperThickness;
    }

    public void setCopperThickness(String copperThickness) {
        this.copperThickness = copperThickness;
    }

    public Boolean getIsCopperContained() {
        return isCopperContained;
    }

    public void setIsCopperContained(Boolean isCopperContained) {
        this.isCopperContained = isCopperContained;
    }

    public BigDecimal getDiameter() {
        return diameter;
    }

    public void setDiameter(BigDecimal diameter) {
        this.diameter = diameter;
    }

    public BigDecimal getBladeLength() {
        return bladeLength;
    }

    public void setBladeLength(BigDecimal bladeLength) {
        this.bladeLength = bladeLength;
    }

    public BigDecimal getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(BigDecimal totalLength) {
        this.totalLength = totalLength;
    }

    public String getExt1() {
        return ext1;
    }

    public void setExt1(String ext1) {
        this.ext1 = ext1;
    }

    public String getExt2() {
        return ext2;
    }

    public void setExt2(String ext2) {
        this.ext2 = ext2;
    }

    public String getExt3() {
        return ext3;
    }

    public void setExt3(String ext3) {
        this.ext3 = ext3;
    }

    public String getExt4() {
        return ext4;
    }

    public void setExt4(String ext4) {
        this.ext4 = ext4;
    }

    public String getExt5() {
        return ext5;
    }

    public void setExt5(String ext5) {
        this.ext5 = ext5;
    }
}
