package com.litemes.infrastructure.security;

import io.smallrye.jwt.auth.principal.JWTAuthContextInfo;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@Alternative
@Priority(1)
@ApplicationScoped
public class JwtConfigProducer {

    private static final String JWT_SECRET = "litemes-jwt-secret-key-must-be-at-least-256-bits-long-for-hmac-sha-256";

    @Produces
    @ApplicationScoped
    JWTAuthContextInfo produceJwtContextInfo(
            @ConfigProperty(name = "mp.jwt.verify.issuer", defaultValue = "https://litemes.com") String issuer) {
        JWTAuthContextInfo info = new JWTAuthContextInfo();
        info.setIssuedBy(issuer);
        info.setSignatureAlgorithm(Set.of(io.smallrye.jwt.algorithm.SignatureAlgorithm.HS256));
        info.setSecretVerificationKey(
                new SecretKeySpec(JWT_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return info;
    }
}
