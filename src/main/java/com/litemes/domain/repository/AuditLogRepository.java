package com.litemes.domain.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litemes.domain.entity.AuditLog;

import java.util.List;

/**
 * Domain repository interface for AuditLog entity.
 * Provides query capabilities for change history.
 */
public interface AuditLogRepository {

    AuditLog save(AuditLog entity);

    IPage<AuditLog> findByTableAndRecord(IPage<AuditLog> page, String tableName, Long recordId);

    IPage<AuditLog> findByTimeRange(IPage<AuditLog> page, String startTime, String endTime);

    IPage<AuditLog> findPage(IPage<AuditLog> page, String tableName, Long recordId,
                              String startTime, String endTime);
}
