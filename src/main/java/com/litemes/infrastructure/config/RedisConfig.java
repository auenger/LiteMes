package com.litemes.infrastructure.config;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.keys.KeyCommands;
import io.quarkus.redis.datasource.value.SetArgs;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.time.Duration;

/**
 * Redis distributed lock utility.
 * Provides a simple acquire/release lock mechanism using Redis SET NX EX.
 */
@ApplicationScoped
public class RedisConfig {

    private static final Logger LOG = Logger.getLogger(RedisConfig.class);

    @Inject
    RedisDataSource redisDataSource;

    /**
     * Try to acquire a distributed lock.
     *
     * @param lockKey  the lock key
     * @param value    the lock value (should be unique per request)
     * @param duration lock expiration time
     * @return true if lock acquired, false otherwise
     */
    public boolean tryLock(String lockKey, String value, Duration duration) {
        try {
            ValueCommands<String, String> valueCommands = redisDataSource.value(String.class);
            // setnx returns true if key was set (lock acquired)
            Boolean acquired = valueCommands.setnx(lockKey, value);
            if (acquired != null && acquired) {
                // Set expiration
                KeyCommands<String> keyCommands = redisDataSource.key();
                keyCommands.expire(lockKey, duration.getSeconds());
                LOG.debugf("Lock acquired: %s", lockKey);
                return true;
            }
            LOG.debugf("Lock not acquired: %s (already held)", lockKey);
            return false;
        } catch (Exception e) {
            LOG.errorf("Redis lock error: %s", e.getMessage());
            return false;
        }
    }

    /**
     * Release a distributed lock.
     *
     * @param lockKey the lock key
     */
    public void releaseLock(String lockKey) {
        try {
            KeyCommands<String> keyCommands = redisDataSource.key();
            keyCommands.del(lockKey);
            LOG.debugf("Lock released: %s", lockKey);
        } catch (Exception e) {
            LOG.errorf("Redis unlock error: %s", e.getMessage());
        }
    }
}
