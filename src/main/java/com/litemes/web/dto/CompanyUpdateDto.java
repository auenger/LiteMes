package com.litemes.web.dto;

import jakarta.validation.constraints.Size;

/**
 * DTO for updating a company.
 * Company code cannot be modified.
 */
public class CompanyUpdateDto {

    @Size(max = 50, message = "公司名称长度不能超过50个字符")
    private String name;

    @Size(max = 50, message = "简码长度不能超过50个字符")
    private String shortCode;

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
