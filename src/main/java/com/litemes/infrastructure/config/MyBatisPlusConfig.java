package com.litemes.infrastructure.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

/**
 * MyBatis-Plus configuration.
 * Registers pagination plugin and data permission interceptor.
 */
@ApplicationScoped
public class MyBatisPlusConfig {

    private static final Logger LOG = Logger.getLogger(MyBatisPlusConfig.class);

    @Inject
    PermissionCacheService permissionCacheService;

    @Produces
    @ApplicationScoped
    @Unremovable
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        LOG.info("Initializing MyBatis-Plus interceptor with pagination and data permission plugins");
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // Pagination plugin
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        paginationInterceptor.setMaxLimit(500L); // Max page size
        paginationInterceptor.setOverflow(false); // Don't return first page when overflow
        interceptor.addInnerInterceptor(paginationInterceptor);

        // Data permission interceptor - automatically filters queries by user's permission scope
        DataPermissionInterceptor dataPermissionInterceptor = new DataPermissionInterceptor(permissionCacheService);
        interceptor.addInnerInterceptor(dataPermissionInterceptor);
        LOG.info("Data permission interceptor registered");

        return interceptor;
    }
}
