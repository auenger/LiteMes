package com.litemes.web.dto;

import jakarta.ws.rs.QueryParam;

/**
 * DTO for data permission group query parameters.
 * Supports fuzzy search by group name.
 */
public class DataPermissionGroupQueryDto {

    @QueryParam("groupName")
    private String groupName;

    @QueryParam("page")
    private Integer page = 1;

    @QueryParam("size")
    private Integer size = 10;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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
