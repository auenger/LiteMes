package com.litemes.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * User entity.
 * Represents a system user that can be assigned to departments.
 * This is a minimal entity to support the department-user relationship feature.
 */
@TableName("user")
public class User extends BaseEntity {

    private String username;

    private String password;

    private String realName;

    private Integer status;

    public User() {
    }

    public User(String username, String realName) {
        this.username = username;
        this.realName = realName;
        this.status = 1;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
