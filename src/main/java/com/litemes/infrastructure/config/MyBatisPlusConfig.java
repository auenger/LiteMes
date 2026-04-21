package com.litemes.infrastructure.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.jboss.logging.Logger;

/**
 * MyBatis-Plus configuration.
 * Registers pagination plugin and other global interceptors.
 */
@ApplicationScoped
public class MyBatisPlusConfig {

    private static final Logger LOG = Logger.getLogger(MyBatisPlusConfig.class);

    @Produces
    @ApplicationScoped
    @Unremovable
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        LOG.info("Initializing MyBatis-Plus interceptor with pagination plugin");
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // Pagination plugin
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        paginationInterceptor.setMaxLimit(500L); // Max page size
        paginationInterceptor.setOverflow(false); // Don't return first page when overflow
        interceptor.addInnerInterceptor(paginationInterceptor);

        // Data scope interceptor placeholder - will be added in feat-data-permission
        // interceptor.addInnerInterceptor(new DataScopeInnerInterceptor(...));

        return interceptor;
    }
}
