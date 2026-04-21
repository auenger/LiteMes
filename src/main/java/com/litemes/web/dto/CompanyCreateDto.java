package com.litemes.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a company.
 * Company code is required and immutable after creation.
 */
public class CompanyCreateDto {

    @NotBlank(message = "公司编码不能为空")
    @Size(max = 50, message = "公司编码长度不能超过50个字符")
    private String companyCode;

    @NotBlank(message = "公司名称不能为空")
    @Size(max = 50, message = "公司名称长度不能超过50个字符")
    private String name;

    @Size(max = 50, message = "简码长度不能超过50个字符")
    private String shortCode;

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }
}
