package com.litemes.infrastructure.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.ibatis.reflection.MetaObject;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;

/**
 * Auto-fills audit fields (createdBy, createdAt, updatedBy, updatedAt) for all entities.
 * This handler is triggered by MyBatis-Plus @TableField(fill = ...) annotations.
 */
@ApplicationScoped
@Unremovable
public class AuditMetaObjectHandler implements MetaObjectHandler {

    private static final Logger LOG = Logger.getLogger(AuditMetaObjectHandler.class);

    private static final String SYSTEM_USER = "system";

    @Override
    public void insertFill(MetaObject metaObject) {
        String currentUser = getCurrentUser();
        LocalDateTime now = LocalDateTime.now();

        this.strictInsertFill(metaObject, "createdBy", String.class, currentUser);
        this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "updatedBy", String.class, currentUser);
        this.strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, now);

        LOG.tracef("Auto-filled audit fields for insert: createdBy=%s", currentUser);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        String currentUser = getCurrentUser();
        LocalDateTime now = LocalDateTime.now();

        this.strictUpdateFill(metaObject, "updatedBy", String.class, currentUser);
        this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, now);

        LOG.tracef("Auto-filled audit fields for update: updatedBy=%s", currentUser);
    }

    /**
     * Get current user from security context.
     * Falls back to 'system' if no authenticated user is available.
     * Will be enhanced in JWT security integration to use JsonWebToken.
     */
    private String getCurrentUser() {
        try {
            // TODO: Inject JsonWebToken and get preferred_username claim
            // Will be fully implemented in feat-enterprise-org
            return SYSTEM_USER;
        } catch (Exception e) {
            return SYSTEM_USER;
        }
    }
}
