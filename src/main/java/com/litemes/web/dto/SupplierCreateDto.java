package com.litemes.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a supplier.
 * Supplier code is required and immutable after creation.
 */
public class SupplierCreateDto {

    @NotBlank(message = "供应商编码不能为空")
    @Size(max = 50, message = "供应商编码长度不能超过50个字符")
    private String supplierCode;

    @NotBlank(message = "供应商名称不能为空")
    @Size(max = 255, message = "供应商名称长度不能超过255个字符")
    private String supplierName;

    @Size(max = 50, message = "简称长度不能超过50个字符")
    private String shortName;

    @Size(max = 50, message = "联系人长度不能超过50个字符")
    private String contactPerson;

    @Size(max = 50, message = "电话长度不能超过50个字符")
    private String phone;

    @Size(max = 50, message = "地址长度不能超过50个字符")
    private String address;

    @Size(max = 50, message = "邮箱长度不能超过50个字符")
    private String email;

    @Size(max = 50, message = "描述长度不能超过50个字符")
    private String description;

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
