package com.litemes.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * Factory entity.
 * Represents a factory in the enterprise organization hierarchy.
 * A factory belongs to a company. Factory code is immutable after creation.
 */
@TableName("factory")
public class Factory extends SoftDeleteEntity {

    private String factoryCode;

    private String name;

    private String shortName;

    private Long companyId;

    private Integer status;

    public Factory() {
    }

    public Factory(String factoryCode, String name, String shortName, Long companyId) {
        this.factoryCode = factoryCode;
        this.name = name;
        this.shortName = shortName;
        this.companyId = companyId;
        this.status = 1;
    }

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
