package com.litemes.application.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.litemes.domain.entity.User;
import com.litemes.infrastructure.persistence.mapper.UserMapper;
import com.litemes.web.dto.UserInfoDto;
import io.smallrye.jwt.build.Jwt;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jboss.logging.Logger;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@Singleton
public class AuthService {

    private static final Logger LOG = Logger.getLogger(AuthService.class);

    private static final String JWT_SECRET = "litemes-jwt-secret-key-must-be-at-least-256-bits-long-for-hmac-sha-256";
    private static final SecretKey SIGNING_KEY = new SecretKeySpec(
            JWT_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

    @Inject
    UserMapper userMapper;

    public String login(String username, String password) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, username)
        );

        if (user == null) {
            LOG.warnf("Login failed: user not found - %s", username);
            return null;
        }

        if (user.getStatus() != 1) {
            LOG.warnf("Login failed: user disabled - %s", username);
            return null;
        }

        if (!user.getPassword().equals(password)) {
            LOG.warnf("Login failed: wrong password - %s", username);
            return null;
        }

        String token = Jwt.issuer("https://litemes.com")
                .subject(String.valueOf(user.getId()))
                .upn(user.getUsername())
                .claim("realName", user.getRealName())
                .groups(Set.of("user"))
                .audience(Set.of("litemes-api"))
                .expiresIn(86400)
                .jws()
                .sign(SIGNING_KEY);

        LOG.infof("User logged in: %s", username);
        return token;
    }

    public UserInfoDto getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return null;
        }
        return new UserInfoDto(user.getId(), user.getUsername(), user.getRealName(), new String[]{"user"});
    }
}
