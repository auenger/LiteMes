package com.litemes.web;

import com.litemes.application.service.AuthService;
import com.litemes.infrastructure.security.CurrentUserProducer;
import com.litemes.web.dto.LoginDto;
import com.litemes.web.dto.R;
import com.litemes.web.dto.UserInfoDto;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.Map;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Auth", description = "认证 API")
public class AuthResource {

    @Inject
    AuthService authService;

    @Inject
    CurrentUserProducer currentUserProducer;

    @POST
    @Path("/login")
    @Operation(summary = "用户登录", description = "用户名密码登录，返回 JWT Token")
    public R<Map<String, Object>> login(@Valid LoginDto loginDto) {
        String token = authService.login(loginDto.getUsername(), loginDto.getPassword());
        if (token == null) {
            return R.fail(401, "用户名或密码错误");
        }
        return R.ok(Map.of("token", token));
    }

    @GET
    @Path("/userinfo")
    @Operation(summary = "获取当前用户信息", description = "根据 JWT Token 获取当前登录用户信息")
    public R<UserInfoDto> getUserInfo() {
        String userId = currentUserProducer.getUserId();
        if ("system".equals(userId)) {
            return R.fail(401, "未登录");
        }
        UserInfoDto userInfo = authService.getUserInfo(Long.parseLong(userId));
        if (userInfo == null) {
            return R.fail(404, "用户不存在");
        }
        return R.ok(userInfo);
    }
}
