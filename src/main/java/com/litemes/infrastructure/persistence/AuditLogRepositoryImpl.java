package com.litemes.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litemes.domain.entity.AuditLog;
import com.litemes.domain.repository.AuditLogRepository;
import com.litemes.infrastructure.persistence.mapper.AuditLogMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * MyBatis-Plus implementation of AuditLogRepository.
 */
@ApplicationScoped
public class AuditLogRepositoryImpl implements AuditLogRepository {

    private static final DateTimeFormatter DT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Inject
    AuditLogMapper auditLogMapper;

    @Override
    public AuditLog save(AuditLog entity) {
        auditLogMapper.insert(entity);
        return entity;
    }

    @Override
    public IPage<AuditLog> findByTableAndRecord(IPage<AuditLog> page, String tableName, Long recordId) {
        LambdaQueryWrapper<AuditLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuditLog::getTableName, tableName)
               .eq(AuditLog::getRecordId, recordId)
               .orderByDesc(AuditLog::getCreatedAt);
        return auditLogMapper.selectPage(page, wrapper);
    }

    @Override
    public IPage<AuditLog> findByTimeRange(IPage<AuditLog> page, String startTime, String endTime) {
        LambdaQueryWrapper<AuditLog> wrapper = new LambdaQueryWrapper<>();
        if (startTime != null && !startTime.isBlank()) {
            wrapper.ge(AuditLog::getCreatedAt, LocalDateTime.parse(startTime, DT_FORMATTER));
        }
        if (endTime != null && !endTime.isBlank()) {
            wrapper.le(AuditLog::getCreatedAt, LocalDateTime.parse(endTime, DT_FORMATTER));
        }
        wrapper.orderByDesc(AuditLog::getCreatedAt);
        return auditLogMapper.selectPage(page, wrapper);
    }

    @Override
    public IPage<AuditLog> findPage(IPage<AuditLog> page, String tableName, Long recordId,
                                     String startTime, String endTime) {
        LambdaQueryWrapper<AuditLog> wrapper = new LambdaQueryWrapper<>();
        if (tableName != null && !tableName.isBlank()) {
            wrapper.eq(AuditLog::getTableName, tableName);
        }
        if (recordId != null) {
            wrapper.eq(AuditLog::getRecordId, recordId);
        }
        if (startTime != null && !startTime.isBlank()) {
            wrapper.ge(AuditLog::getCreatedAt, LocalDateTime.parse(startTime, DT_FORMATTER));
        }
        if (endTime != null && !endTime.isBlank()) {
            wrapper.le(AuditLog::getCreatedAt, LocalDateTime.parse(endTime, DT_FORMATTER));
        }
        wrapper.orderByDesc(AuditLog::getCreatedAt);
        return auditLogMapper.selectPage(page, wrapper);
    }
}
