package com.litemes.web.dto;

import jakarta.validation.constraints.Size;

/**
 * DTO for updating a factory.
 * Factory code cannot be modified.
 */
public class FactoryUpdateDto {

    @Size(max = 50, message = "工厂名称长度不能超过50个字符")
    private String name;

    @Size(max = 50, message = "简称长度不能超过50个字符")
    private String shortName;

    private Long companyId;

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
