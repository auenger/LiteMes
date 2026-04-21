package com.litemes.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a factory.
 * Factory code is required and immutable after creation.
 * Company ID is required to establish the company-factory relationship.
 */
public class FactoryCreateDto {

    @NotBlank(message = "工厂编码不能为空")
    @Size(max = 50, message = "工厂编码长度不能超过50个字符")
    private String factoryCode;

    @NotBlank(message = "工厂名称不能为空")
    @Size(max = 50, message = "工厂名称长度不能超过50个字符")
    private String name;

    @Size(max = 50, message = "简称长度不能超过50个字符")
    private String shortName;

    @NotNull(message = "请选择所属公司")
    private Long companyId;

    public String getFactoryCode() {
        return factoryCode;
    }

    public void setFactoryCode(String factoryCode) {
        this.factoryCode = factoryCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }
}
