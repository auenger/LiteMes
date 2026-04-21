package com.litemes.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * UserDataPermissionWorkCenter entity.
 * Association between a user's data permission and a work center.
 * source: GROUP (from permission group) or DIRECT (directly assigned).
 */
@TableName("user_data_permission_work_center")
public class UserDataPermissionWorkCenter extends BaseEntity {

    private Long userPermissionId;

    private Long workCenterId;

    private String source;

    public UserDataPermissionWorkCenter() {
    }

    public UserDataPermissionWorkCenter(Long userPermissionId, Long workCenterId, String source) {
        this.userPermissionId = userPermissionId;
        this.workCenterId = workCenterId;
        this.source = source;
    }

    public Long getUserPermissionId() {
        return userPermissionId;
    }

    public void setUserPermissionId(Long userPermissionId) {
        this.userPermissionId = userPermissionId;
    }

    public Long getWorkCenterId() {
        return workCenterId;
    }

    public void setWorkCenterId(Long workCenterId) {
        this.workCenterId = workCenterId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
