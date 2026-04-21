package com.litemes.web.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * DTO for assigning users to a department.
 * Accepts a list of user IDs to be assigned.
 */
public class AssignUserDto {

    @NotEmpty(message = "用户列表不能为空")
    private List<@NotNull Long> userIds;

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }
}
