package com.litemes.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * DataPermissionGroup entity.
 * Represents a data permission group that bundles factory, work center, and process associations.
 * Group name is unique and required.
 */
@TableName("data_permission_group")
public class DataPermissionGroup extends SoftDeleteEntity {

    private String groupName;

    private String remark;

    public DataPermissionGroup() {
    }

    public DataPermissionGroup(String groupName, String remark) {
        this.groupName = groupName;
        this.remark = remark;
    }

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
