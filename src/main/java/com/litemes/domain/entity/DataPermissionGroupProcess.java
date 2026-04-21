package com.litemes.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * DataPermissionGroupProcess entity.
 * Association between a data permission group and a process.
 */
@TableName("data_permission_group_process")
public class DataPermissionGroupProcess extends BaseEntity {

    private Long groupId;

    private Long processId;

    public DataPermissionGroupProcess() {
    }

    public DataPermissionGroupProcess(Long groupId, Long processId) {
        this.groupId = groupId;
        this.processId = processId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }
}
