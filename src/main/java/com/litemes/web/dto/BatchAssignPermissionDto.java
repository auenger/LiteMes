package com.litemes.web.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * DTO for batch assigning data permission to multiple users via a permission group.
 */
public class BatchAssignPermissionDto {

    @NotNull(message = "用户ID列表不能为空")
    @NotEmpty(message = "用户ID列表不能为空")
    private List<Long> userIds;

    @NotNull(message = "权限组ID不能为空")
    private Long groupId;

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
}
