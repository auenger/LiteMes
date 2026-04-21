package com.litemes.web.dto;

import jakarta.validation.constraints.Size;

/**
 * DTO for updating a data permission group.
 * Group name can be updated but must remain unique.
 */
public class DataPermissionGroupUpdateDto {

    @Size(max = 50, message = "权限组名称长度不能超过50个字符")
    private String groupName;

    @Size(max = 200, message = "备注长度不能超过200个字符")
    private String remark;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
