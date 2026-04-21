package com.litemes.web.dto;

import jakarta.ws.rs.QueryParam;

/**
 * Query parameters for searching users in the user selection dialog.
 * Note: Quarkus RESTEasy Reactive requires @QueryParam on fields for @BeanParam.
 */
public class UserQueryDto {

    @QueryParam("username")
    private String username;

    @QueryParam("realName")
    private String realName;

    @QueryParam("page")
    private int page = 1;

    @QueryParam("size")
    private int size = 10;

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

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
