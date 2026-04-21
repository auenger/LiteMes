package com.litemes.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a customer.
 * Customer code is required and immutable after creation.
 */
public class CustomerCreateDto {

    @NotBlank(message = "客户编码不能为空")
    @Size(max = 50, message = "客户编码长度不能超过50个字符")
    private String customerCode;

    @NotBlank(message = "客户名称不能为空")
    @Size(max = 50, message = "客户名称长度不能超过50个字符")
    private String customerName;

    @Size(max = 50, message = "客户类型长度不能超过50个字符")
    private String type;

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

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}
