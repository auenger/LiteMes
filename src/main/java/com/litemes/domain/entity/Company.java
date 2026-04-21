package com.litemes.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * Company entity.
 * Represents a company in the enterprise organization hierarchy.
 * Company code is immutable after creation.
 */
@TableName("company")
public class Company extends SoftDeleteEntity {

    private String companyCode;

    private String name;

    private String shortCode;

    private Integer status;

    public Company() {
    }

    public Company(String companyCode, String name, String shortCode) {
        this.companyCode = companyCode;
        this.name = name;
        this.shortCode = shortCode;
        this.status = 1;
    }

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
