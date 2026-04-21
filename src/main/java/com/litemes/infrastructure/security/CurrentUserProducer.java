package com.litemes.infrastructure.security;

import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import java.util.Set;

/**
 * Produces the current authenticated user information from JWT claims.
 * Injectable in any layer to get current user context.
 */
@RequestScoped
@Unremovable
public class CurrentUserProducer {

    private static final Logger LOG = Logger.getLogger(CurrentUserProducer.class);

    @Inject
    JsonWebToken jwt;

    /**
     * Get the current user's subject (user ID) from JWT.
     */
    public String getUserId() {
        if (jwt == null || jwt.getSubject() == null) {
            return "system";
        }
        return jwt.getSubject();
    }

    /**
     * Get the current user's username from JWT 'preferred_username' claim.
     */
    public String getUsername() {
        if (jwt == null) {
            return "system";
        }
        return jwt.getClaim("preferred_username") != null
                ? jwt.getClaim("preferred_username")
                : jwt.getSubject();
    }

    /**
     * Get the current user's roles from JWT groups.
     */
    public Set<String> getRoles() {
        if (jwt == null) {
            return Set.of();
        }
        return jwt.getGroups();
    }

    /**
     * Check if a user is currently authenticated.
     */
    public boolean isAuthenticated() {
        return jwt != null && jwt.getSubject() != null;
    }
}
