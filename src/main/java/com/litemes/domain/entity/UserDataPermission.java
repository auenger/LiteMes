package com.litemes.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * UserDataPermission entity.
 * Represents a user's data permission configuration.
 * One record per user. group_id can be null if the user only has direct assignments.
 */
@TableName("user_data_permission")
public class UserDataPermission extends BaseEntity {

    private Long userId;

    private Long groupId;

    public UserDataPermission() {
    }

    public UserDataPermission(Long userId, Long groupId) {
        this.userId = userId;
        this.groupId = groupId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
}
