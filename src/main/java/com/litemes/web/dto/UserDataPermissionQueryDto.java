package com.litemes.web.dto;

import jakarta.ws.rs.QueryParam;

/**
 * DTO for user data permission query parameters.
 * Supports fuzzy search by username and realName.
 */
public class UserDataPermissionQueryDto {

    @QueryParam("username")
    private String username;

    @QueryParam("realName")
    private String realName;

    @QueryParam("page")
    private Integer page = 1;

    @QueryParam("size")
    private Integer size = 10;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
