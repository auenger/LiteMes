package com.litemes.web;

import com.litemes.application.service.UserDataPermissionService;
import com.litemes.web.dto.BatchAssignPermissionDto;
import com.litemes.web.dto.DirectAssignDto;
import com.litemes.web.dto.PagedResult;
import com.litemes.web.dto.R;
import com.litemes.web.dto.UserDataPermissionQueryDto;
import com.litemes.web.dto.UserDataPermissionVo;
import com.litemes.web.dto.UserPermissionAssociatedEntityDto;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

/**
 * REST API for UserDataPermission management.
 * Provides endpoints for batch assign, direct assign, list, delete,
 * and association operations (factory/work-center/process).
 */
@Path("/api/user-data-permissions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "UserDataPermission", description = "用户数据权限管理 API")
public class UserDataPermissionResource {

    @Inject
    UserDataPermissionService service;

    @GET
    @Operation(summary = "分页查询用户数据权限列表", description = "支持按用户名/姓名模糊查询")
    public R<PagedResult<UserDataPermissionVo>> list(@BeanParam UserDataPermissionQueryDto query) {
        return R.ok(service.list(query));
    }

    @POST
    @Path("/batch-assign")
    @Operation(summary = "批量添加权限", description = "选择多个用户和权限组，批量赋值数据权限")
    public R<Void> batchAssign(@Valid @NotNull BatchAssignPermissionDto dto) {
        service.batchAssign(dto);
        return R.ok();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "删除用户数据权限", description = "删除用户的数据权限记录及所有关联")
    public R<Void> delete(@PathParam("id") Long id) {
        service.delete(id);
        return R.ok();
    }

    // Factory association endpoints

    @GET
    @Path("/{id}/factories")
    @Operation(summary = "获取用户已关联的工厂列表", description = "返回用户权限关联的工厂，含来源标识")
    public R<List<UserPermissionAssociatedEntityDto>> getFactories(@PathParam("id") Long id) {
        return R.ok(service.getFactories(id));
    }

    @POST
    @Path("/{id}/factories")
    @Operation(summary = "直接授权工厂", description = "为用户直接添加工厂权限（source=DIRECT），保留权限组继承的记录")
    public R<Void> directAssignFactories(@PathParam("id") Long id, @Valid @NotNull DirectAssignDto dto) {
        service.directAssignFactories(id, dto);
        return R.ok();
    }

    // Work Center association endpoints

    @GET
    @Path("/{id}/work-centers")
    @Operation(summary = "获取用户已关联的工作中心列表", description = "返回用户权限关联的工作中心，含来源标识")
    public R<List<UserPermissionAssociatedEntityDto>> getWorkCenters(@PathParam("id") Long id) {
        return R.ok(service.getWorkCenters(id));
    }

    @POST
    @Path("/{id}/work-centers")
    @Operation(summary = "直接授权工作中心", description = "为用户直接添加工作中心权限（source=DIRECT）")
    public R<Void> directAssignWorkCenters(@PathParam("id") Long id, @Valid @NotNull DirectAssignDto dto) {
        service.directAssignWorkCenters(id, dto);
        return R.ok();
    }

    // Process association endpoints

    @GET
    @Path("/{id}/processes")
    @Operation(summary = "获取用户已关联的工序列表", description = "返回用户权限关联的工序，含来源标识")
    public R<List<UserPermissionAssociatedEntityDto>> getProcesses(@PathParam("id") Long id) {
        return R.ok(service.getProcesses(id));
    }

    @POST
    @Path("/{id}/processes")
    @Operation(summary = "直接授权工序", description = "为用户直接添加工序权限（source=DIRECT）")
    public R<Void> directAssignProcesses(@PathParam("id") Long id, @Valid @NotNull DirectAssignDto dto) {
        service.directAssignProcesses(id, dto);
        return R.ok();
    }
}
