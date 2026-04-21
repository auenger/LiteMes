package com.litemes.infrastructure.config;

import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.servers.Server;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

/**
 * OpenAPI / Swagger UI configuration.
 * Configures API documentation metadata and security scheme.
 */
@ApplicationScoped
@OpenAPIDefinition(
    info = @Info(
        title = "LiteMes API",
        version = "1.0.0",
        description = "PCB Lightweight MES System REST API - Master Data Module",
        contact = @Contact(name = "LiteMes Team"),
        license = @License(name = "Proprietary")
    ),
    servers = @Server(url = "http://localhost:8080", description = "Development"),
    security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
    securitySchemeName = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
public class OpenApiConfig {
    // Configuration is done via annotations above
}
