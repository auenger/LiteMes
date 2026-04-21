package com.litemes.infrastructure.config;

import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.jboss.logging.Logger;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * MyBatis-Plus InnerInterceptor that appends data permission filters to SELECT queries.
 * Uses simple string-based SQL modification.
 */
public class DataPermissionInterceptor implements InnerInterceptor {

    private static final Logger LOG = Logger.getLogger(DataPermissionInterceptor.class);

    private static final Map<String, String> FILTER_CONFIG = new HashMap<>();

    static {
        FILTER_CONFIG.put("factory", "id");
        FILTER_CONFIG.put("work_center", "id");
        FILTER_CONFIG.put("process", "id");
        FILTER_CONFIG.put("equipment_ledger", "factoryId");
    }

    private final PermissionCacheService permissionCacheService;

    public DataPermissionInterceptor(PermissionCacheService permissionCacheService) {
        this.permissionCacheService = permissionCacheService;
    }

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter,
                            RowBounds rowBounds, ResultHandler resultHandler, org.apache.ibatis.mapping.BoundSql boundSql) throws SQLException {
        Long userId = permissionCacheService.getCurrentUserId();
        if (userId == null) {
            return;
        }

        PermissionCacheService.PermissionScope scope = permissionCacheService.getPermissionScope(userId);
        if (scope == null || scope.isEmpty()) {
            return;
        }

        String originalSql = boundSql.getSql().trim();
        String lowerSql = originalSql.toLowerCase();

        for (Map.Entry<String, String> entry : FILTER_CONFIG.entrySet()) {
            String tableName = entry.getKey();
            String filterColumn = entry.getValue();

            if (!lowerSql.contains(tableName)) {
                continue;
            }

            List<Long> allowedIds = getAllowedIds(tableName, scope);
            if (allowedIds == null || allowedIds.isEmpty()) {
                continue;
            }

            String inClause = allowedIds.stream().map(String::valueOf).collect(Collectors.joining(",", "(", ")"));
            String condition = tableName + "." + filterColumn + " IN " + inClause;

            String modifiedSql;
            if (lowerSql.contains(" where ")) {
                modifiedSql = originalSql + " AND " + condition;
            } else {
                modifiedSql = originalSql + " WHERE " + condition;
            }

            try {
                java.lang.reflect.Field field = boundSql.getClass().getDeclaredField("sql");
                field.setAccessible(true);
                field.set(boundSql, modifiedSql);
            } catch (Exception e) {
                LOG.warnf("Failed to modify SQL for data permission: %s", e.getMessage());
                return;
            }

            LOG.debugf("Applied data permission filter on %s.%s, ids: %s", tableName, filterColumn, allowedIds);
            return;
        }
    }

    private List<Long> getAllowedIds(String tableName, PermissionCacheService.PermissionScope scope) {
        return switch (tableName) {
            case "factory" -> scope.getFactoryIds();
            case "work_center" -> scope.getWorkCenterIds();
            case "process" -> scope.getProcessIds();
            case "equipment_ledger" -> scope.getFactoryIds();
            default -> null;
        };
    }
}
