package com.litemes.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.litemes.domain.entity.AuditLog;
import com.litemes.domain.repository.AuditLogRepository;
import com.litemes.web.dto.AuditLogDto;
import com.litemes.web.dto.AuditLogQueryDto;
import com.litemes.web.dto.PagedResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Application service for AuditLog operations.
 * Handles audit log recording and querying.
 * Provides helper methods for CREATE, UPDATE, DELETE audit recording.
 */
@ApplicationScoped
public class AuditLogService {

    private static final Logger LOG = Logger.getLogger(AuditLogService.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Inject
    AuditLogRepository auditLogRepository;

    /**
     * Record a CREATE action audit log.
     */
    @Transactional
    public void logCreate(String tableName, Long recordId, Object newValue, String operatorName) {
        try {
            String newValueJson = OBJECT_MAPPER.writeValueAsString(newValue);
            AuditLog log = new AuditLog(tableName, recordId, "CREATE",
                    null, newValueJson, null, null, operatorName);
            auditLogRepository.save(log);
            LOG.debugf("Audit log CREATE: %s/%d", tableName, recordId);
        } catch (Exception e) {
            LOG.errorf("Failed to record CREATE audit log: %s", e.getMessage());
        }
    }

    /**
     * Record an UPDATE action audit log with field-level diff.
     */
    @Transactional
    public void logUpdate(String tableName, Long recordId, Object oldValue, Object newValue,
                          String operatorName) {
        try {
            String oldValueJson = OBJECT_MAPPER.writeValueAsString(oldValue);
            String newValueJson = OBJECT_MAPPER.writeValueAsString(newValue);
            String changedFields = computeChangedFields(oldValue, newValue);

            AuditLog log = new AuditLog(tableName, recordId, "UPDATE",
                    oldValueJson, newValueJson, changedFields, null, operatorName);
            auditLogRepository.save(log);
            LOG.debugf("Audit log UPDATE: %s/%d, changed: %s", tableName, recordId, changedFields);
        } catch (Exception e) {
            LOG.errorf("Failed to record UPDATE audit log: %s", e.getMessage());
        }
    }

    /**
     * Record a DELETE action audit log.
     */
    @Transactional
    public void logDelete(String tableName, Long recordId, Object oldValue, String operatorName) {
        try {
            String oldValueJson = OBJECT_MAPPER.writeValueAsString(oldValue);
            AuditLog log = new AuditLog(tableName, recordId, "DELETE",
                    oldValueJson, null, null, null, operatorName);
            auditLogRepository.save(log);
            LOG.debugf("Audit log DELETE: %s/%d", tableName, recordId);
        } catch (Exception e) {
            LOG.errorf("Failed to record DELETE audit log: %s", e.getMessage());
        }
    }

    /**
     * Query audit logs with pagination.
     */
    public PagedResult<AuditLogDto> list(AuditLogQueryDto query) {
        LOG.debugf("Listing audit logs: table=%s, recordId=%s, startTime=%s, endTime=%s",
                query.getTableName(), query.getRecordId(), query.getStartTime(), query.getEndTime());

        IPage<AuditLog> page = new Page<>(query.getPage(), query.getSize());
        IPage<AuditLog> result = auditLogRepository.findPage(
                page, query.getTableName(), query.getRecordId(),
                query.getStartTime(), query.getEndTime());

        List<AuditLogDto> records = result.getRecords().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new PagedResult<>(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    /**
     * Compute changed field names between old and new value maps.
     */
    @SuppressWarnings("unchecked")
    private String computeChangedFields(Object oldValue, Object newValue) {
        try {
            Map<String, Object> oldMap = OBJECT_MAPPER.convertValue(oldValue, Map.class);
            Map<String, Object> newMap = OBJECT_MAPPER.convertValue(newValue, Map.class);

            List<String> changed = new ArrayList<>();
            Set<String> allKeys = new HashSet<>();
            allKeys.addAll(oldMap.keySet());
            allKeys.addAll(newMap.keySet());

            for (String key : allKeys) {
                // Skip audit fields
                if (key.equals("updatedBy") || key.equals("updatedAt") || key.equals("deleted")) {
                    continue;
                }
                Object oldVal = oldMap.get(key);
                Object newVal = newMap.get(key);
                if (!Objects.equals(oldVal, newVal)) {
                    changed.add(key);
                }
            }
            return String.join(",", changed);
        } catch (Exception e) {
            return "";
        }
    }

    private AuditLogDto toDto(AuditLog entity) {
        AuditLogDto dto = new AuditLogDto();
        dto.setId(entity.getId());
        dto.setTableName(entity.getTableName());
        dto.setRecordId(entity.getRecordId());
        dto.setAction(entity.getAction());
        dto.setOldValue(entity.getOldValue());
        dto.setNewValue(entity.getNewValue());
        dto.setChangedFields(entity.getChangedFields());
        dto.setOperatorId(entity.getOperatorId());
        dto.setOperatorName(entity.getOperatorName());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
