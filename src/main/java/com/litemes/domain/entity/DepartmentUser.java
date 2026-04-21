package com.litemes.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * DepartmentUser entity.
 * Represents the many-to-many relationship between departments and users.
 * Uses physical delete (no soft delete) as removing a user from a department
 * simply deletes the association record.
 */
@TableName("department_user")
public class DepartmentUser extends BaseEntity {

    private Long departmentId;

    private Long userId;

    public DepartmentUser() {
    }

    public DepartmentUser(Long departmentId, Long userId) {
        this.departmentId = departmentId;
        this.userId = userId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
