package com.litemes.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * Supplier entity.
 * Represents a supplier in PCB manufacturing supply chain.
 * Supplier code is immutable after creation.
 */
@TableName("supplier")
public class Supplier extends SoftDeleteEntity {

    private String supplierCode;

    private String supplierName;

    private Integer status;

    private String shortName;

    private String contactPerson;

    private String phone;

    private String address;

    private String email;

    private String description;

    public Supplier() {
    }

    public Supplier(String supplierCode, String supplierName) {
        this.supplierCode = supplierCode;
        this.supplierName = supplierName;
        this.status = 1;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
