package com.litemes.web;

import com.litemes.application.service.DepartmentUserService;
import com.litemes.web.dto.AssignUserDto;
import com.litemes.web.dto.DepartmentUserDto;
import com.litemes.web.dto.PagedResult;
import com.litemes.web.dto.R;
import com.litemes.web.dto.UserDto;
import com.litemes.web.dto.UserQueryDto;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

/**
 * REST API for DepartmentUser relationship management.
 * Provides endpoints for assigning/removing users to/from departments.
 */
@Path("/api/departments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "DepartmentUser", description = "部门用户关系 API")
public class DepartmentUserResource {

    @Inject
    DepartmentUserService departmentUserService;

    @GET
    @Path("/{departmentId}/users")
    @Operation(summary = "查看部门用户列表", description = "获取某部门下所有已分配的用户")
    public R<List<DepartmentUserDto>> listDepartmentUsers(@PathParam("departmentId") Long departmentId) {
        return R.ok(departmentUserService.listByDepartment(departmentId));
    }

    @POST
    @Path("/{departmentId}/users")
    @Operation(summary = "分配用户到部门", description = "将选中用户分配到指定部门，重复分配会报错")
    public R<Void> assignUsers(
            @PathParam("departmentId") Long departmentId,
            @Valid @NotNull AssignUserDto dto) {
        departmentUserService.assignUsers(departmentId, dto);
        return R.ok();
    }

    @DELETE
    @Path("/{departmentId}/users/{userId}")
    @Operation(summary = "从部门移除用户", description = "将指定用户从部门移除，物理删除关联记录")
    public R<Void> removeUser(
            @PathParam("departmentId") Long departmentId,
            @PathParam("userId") Long userId) {
        departmentUserService.removeUser(departmentId, userId);
        return R.ok();
    }

    @GET
    @Path("/users/search")
    @Operation(summary = "查询用户列表", description = "用于用户选择弹窗，支持按用户名/姓名模糊查询")
    public R<PagedResult<UserDto>> searchUsers(@BeanParam UserQueryDto query) {
        return R.ok(departmentUserService.queryUsers(query));
    }
}
