package com.litemes.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * UserDataPermissionFactory entity.
 * Association between a user's data permission and a factory.
 * source indicates whether the association came from a permission group (GROUP)
 * or was directly assigned (DIRECT).
 */
@TableName("user_data_permission_factory")
public class UserDataPermissionFactory extends BaseEntity {

    private Long userPermissionId;

    private Long factoryId;

    private String source;

    public UserDataPermissionFactory() {
    }

    public UserDataPermissionFactory(Long userPermissionId, Long factoryId, String source) {
        this.userPermissionId = userPermissionId;
        this.factoryId = factoryId;
        this.source = source;
    }

    public Long getUserPermissionId() {
        return userPermissionId;
    }

    public void setUserPermissionId(Long userPermissionId) {
        this.userPermissionId = userPermissionId;
    }

    public Long getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(Long factoryId) {
        this.factoryId = factoryId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
