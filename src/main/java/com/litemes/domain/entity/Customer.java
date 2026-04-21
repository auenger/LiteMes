package com.litemes.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * Customer entity.
 * Represents a customer in PCB manufacturing supply chain.
 * Customer code is immutable after creation.
 */
@TableName("customer")
public class Customer extends SoftDeleteEntity {

    private String customerCode;

    private String customerName;

    private Integer status;

    private String type;

    private String shortName;

    private String contactPerson;

    private String phone;

    private String address;

    private String email;

    public Customer() {
    }

    public Customer(String customerCode, String customerName) {
        this.customerCode = customerCode;
        this.customerName = customerName;
        this.status = 1;
    }

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
