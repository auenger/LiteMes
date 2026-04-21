package com.litemes.infrastructure.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.litemes.application.service.UserDataPermissionService;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.keys.KeyCommands;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.SecurityContext;
import org.jboss.logging.Logger;

import java.security.Principal;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for caching and retrieving user data permissions.
 *
 * Cache structure in Redis:
 *   Key: "user:permission:{userId}"
 *   Value: JSON map with keys: "factoryIds", "workCenterIds", "processIds"
 *
 * Cache is loaded:
 *   - On user login
 *   - On first access (lazy load)
 *   - Refreshed on permission change
 *
 * Cache TTL: 24 hours (aligns with JWT token lifetime)
 */
@ApplicationScoped
public class PermissionCacheService {

    private static final Logger LOG = Logger.getLogger(PermissionCacheService.class);
    private static final String CACHE_KEY_PREFIX = "user:permission:";
    private static final Duration CACHE_TTL = Duration.ofHours(24);

    @Inject
    RedisDataSource redisDataSource;

    @Inject
    UserDataPermissionService permissionService;

    @Inject
    ObjectMapper objectMapper;

    // Thread-local or CDI request-scoped would be better, but for simplicity use a thread-local
    private static final ThreadLocal<Long> CURRENT_USER_ID = new ThreadLocal<>();

    /**
     * Get the current user ID from the security context.
     * This should be set by the JWT authentication filter.
     */
    public Long getCurrentUserId() {
        return CURRENT_USER_ID.get();
    }

    /**
     * Set the current user ID for this thread/request.
     * Called by authentication filter or resource layer.
     */
    public void setCurrentUserId(Long userId) {
        CURRENT_USER_ID.set(userId);
    }

    /**
     * Clear the current user ID (end of request).
     */
    public void clearCurrentUserId() {
        CURRENT_USER_ID.remove();
    }

    /**
     * Get permission scope for a user, loading from cache or database.
     */
    public PermissionScope getPermissionScope(Long userId) {
        // Try cache first
        PermissionScope cached = loadFromCache(userId);
        if (cached != null) {
            LOG.debugf("Permission cache hit for user %d", userId);
            return cached;
        }

        // Load from database
        LOG.debugf("Permission cache miss for user %d, loading from database", userId);
        UserDataPermissionService.UserPermissionScope dbScope = permissionService.getEffectivePermissionScope(userId);
        PermissionScope scope = new PermissionScope(
                dbScope.getFactoryIds(),
                dbScope.getWorkCenterIds(),
                dbScope.getProcessIds()
        );

        // Save to cache
        saveToCache(userId, scope);

        return scope;
    }

    /**
     * Refresh the permission cache for a user after permission changes.
     */
    public void refreshCache(Long userId) {
        LOG.infof("Refreshing permission cache for user %d", userId);

        UserDataPermissionService.UserPermissionScope dbScope = permissionService.getEffectivePermissionScope(userId);
        PermissionScope scope = new PermissionScope(
                dbScope.getFactoryIds(),
                dbScope.getWorkCenterIds(),
                dbScope.getProcessIds()
        );

        saveToCache(userId, scope);
    }

    /**
     * Invalidate the permission cache for a user.
     */
    public void invalidateCache(Long userId) {
        try {
            KeyCommands<String> keyCommands = redisDataSource.key();
            keyCommands.del(getCacheKey(userId));
            LOG.debugf("Invalidated permission cache for user %d", userId);
        } catch (Exception e) {
            LOG.warnf("Failed to invalidate permission cache: %s", e.getMessage());
        }
    }

    private PermissionScope loadFromCache(Long userId) {
        try {
            ValueCommands<String, String> valueCommands = redisDataSource.value(String.class);
            String json = valueCommands.get(getCacheKey(userId));
            if (json != null) {
                return parseScope(json);
            }
        } catch (Exception e) {
            LOG.warnf("Failed to load permission cache: %s", e.getMessage());
        }
        return null;
    }

    private void saveToCache(Long userId, PermissionScope scope) {
        try {
            ValueCommands<String, String> valueCommands = redisDataSource.value(String.class);
            String json = serializeScope(scope);
            valueCommands.setex(getCacheKey(userId), CACHE_TTL.getSeconds(), json);
            LOG.debugf("Saved permission cache for user %d", userId);
        } catch (Exception e) {
            LOG.warnf("Failed to save permission cache: %s", e.getMessage());
        }
    }

    private String getCacheKey(Long userId) {
        return CACHE_KEY_PREFIX + userId;
    }

    private String serializeScope(PermissionScope scope) throws JsonProcessingException {
        Map<String, List<Long>> map = new ConcurrentHashMap<>();
        map.put("factoryIds", scope.getFactoryIds());
        map.put("workCenterIds", scope.getWorkCenterIds());
        map.put("processIds", scope.getProcessIds());
        return objectMapper.writeValueAsString(map);
    }

    private PermissionScope parseScope(String json) {
        try {
            Map<String, List<Long>> map = objectMapper.readValue(json, new TypeReference<>() {});
            return new PermissionScope(
                    map.getOrDefault("factoryIds", Collections.emptyList()),
                    map.getOrDefault("workCenterIds", Collections.emptyList()),
                    map.getOrDefault("processIds", Collections.emptyList())
            );
        } catch (JsonProcessingException e) {
            LOG.warnf("Failed to parse permission cache: %s", e.getMessage());
            return null;
        }
    }

    /**
     * Data class representing a user's cached permission scope.
     */
    public static class PermissionScope {
        private final List<Long> factoryIds;
        private final List<Long> workCenterIds;
        private final List<Long> processIds;

        public PermissionScope(List<Long> factoryIds, List<Long> workCenterIds, List<Long> processIds) {
            this.factoryIds = factoryIds != null ? factoryIds : Collections.emptyList();
            this.workCenterIds = workCenterIds != null ? workCenterIds : Collections.emptyList();
            this.processIds = processIds != null ? processIds : Collections.emptyList();
        }

        public List<Long> getFactoryIds() {
            return factoryIds;
        }

        public List<Long> getWorkCenterIds() {
            return workCenterIds;
        }

        public List<Long> getProcessIds() {
            return processIds;
        }

        public boolean isEmpty() {
            return factoryIds.isEmpty() && workCenterIds.isEmpty() && processIds.isEmpty();
        }
    }
}
