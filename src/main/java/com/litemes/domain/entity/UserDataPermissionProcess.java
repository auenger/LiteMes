package com.litemes.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * UserDataPermissionProcess entity.
 * Association between a user's data permission and a process.
 * source: GROUP (from permission group) or DIRECT (directly assigned).
 */
@TableName("user_data_permission_process")
public class UserDataPermissionProcess extends BaseEntity {

    private Long userPermissionId;

    private Long processId;

    private String source;

    public UserDataPermissionProcess() {
    }

    public UserDataPermissionProcess(Long userPermissionId, Long processId, String source) {
        this.userPermissionId = userPermissionId;
        this.processId = processId;
        this.source = source;
    }

    public Long getUserPermissionId() {
        return userPermissionId;
    }

    public void setUserPermissionId(Long userPermissionId) {
        this.userPermissionId = userPermissionId;
    }

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
