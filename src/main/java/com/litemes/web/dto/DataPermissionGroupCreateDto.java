package com.litemes.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a data permission group.
 * Group name is required and must be unique.
 */
public class DataPermissionGroupCreateDto {

    @NotBlank(message = "权限组名称不能为空")
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
