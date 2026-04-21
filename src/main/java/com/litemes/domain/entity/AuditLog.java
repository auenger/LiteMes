package com.litemes.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * AuditLog entity.
 * Records data change history for all master data entities.
 * Supports CREATE, UPDATE, DELETE operations with before/after value tracking.
 */
@TableName("audit_log")
public class AuditLog extends BaseEntity {

    private String tableName;

    private Long recordId;

    private String action;

    private String oldValue;

    private String newValue;

    private String changedFields;

    private Long operatorId;

    private String operatorName;

    private LocalDateTime createdAt;

    public AuditLog() {
    }

    public AuditLog(String tableName, Long recordId, String action,
                     String oldValue, String newValue, String changedFields,
                     Long operatorId, String operatorName) {
        this.tableName = tableName;
        this.recordId = recordId;
        this.action = action;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.changedFields = changedFields;
        this.operatorId = operatorId;
        this.operatorName = operatorName;
        this.createdAt = LocalDateTime.now();
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getChangedFields() {
        return changedFields;
    }

    public void setChangedFields(String changedFields) {
        this.changedFields = changedFields;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
