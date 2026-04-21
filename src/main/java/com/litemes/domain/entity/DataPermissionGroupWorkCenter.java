package com.litemes.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * DataPermissionGroupWorkCenter entity.
 * Association between a data permission group and a work center.
 */
@TableName("data_permission_group_work_center")
public class DataPermissionGroupWorkCenter extends BaseEntity {

    private Long groupId;

    private Long workCenterId;

    public DataPermissionGroupWorkCenter() {
    }

    public DataPermissionGroupWorkCenter(Long groupId, Long workCenterId) {
        this.groupId = groupId;
        this.workCenterId = workCenterId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getWorkCenterId() {
        return workCenterId;
    }

    public void setWorkCenterId(Long workCenterId) {
        this.workCenterId = workCenterId;
    }
}
