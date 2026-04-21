package com.litemes.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * DataPermissionGroupFactory entity.
 * Association between a data permission group and a factory.
 */
@TableName("data_permission_group_factory")
public class DataPermissionGroupFactory extends BaseEntity {

    private Long groupId;

    private Long factoryId;

    public DataPermissionGroupFactory() {
    }

    public DataPermissionGroupFactory(Long groupId, Long factoryId) {
        this.groupId = groupId;
        this.factoryId = factoryId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(Long factoryId) {
        this.factoryId = factoryId;
    }
}
