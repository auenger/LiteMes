package com.litemes.web;

import com.litemes.application.service.DataPermissionGroupService;
import com.litemes.web.dto.AssociatedEntityDto;
import com.litemes.web.dto.AssociatedItemDto;
import com.litemes.web.dto.DataPermissionGroupCreateDto;
import com.litemes.web.dto.DataPermissionGroupDto;
import com.litemes.web.dto.DataPermissionGroupQueryDto;
import com.litemes.web.dto.DataPermissionGroupUpdateDto;
import com.litemes.web.dto.PagedResult;
import com.litemes.web.dto.R;
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
 * REST API for DataPermissionGroup CRUD and association operations.
 * Provides endpoints for managing data permission groups and their
 * associations with factories, work centers, and processes.
 */
@Path("/api/data-permission-groups")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "DataPermissionGroup", description = "数据权限组管理 API")
public class DataPermissionGroupResource {

    @Inject
    DataPermissionGroupService service;

    @GET
    @Operation(summary = "分页查询权限组列表", description = "支持按名称模糊查询")
    public R<PagedResult<DataPermissionGroupDto>> list(@BeanParam DataPermissionGroupQueryDto query) {
        return R.ok(service.list(query));
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "根据ID查询权限组", description = "返回权限组详情，含关联数量")
    public R<DataPermissionGroupDto> getById(@PathParam("id") Long id) {
        return R.ok(service.getById(id));
    }

    @POST
    @Operation(summary = "创建权限组", description = "填写权限组名称（唯一）和备注")
    public Response create(@Valid @NotNull DataPermissionGroupCreateDto dto) {
        Long id = service.create(dto);
        return Response.status(Response.Status.CREATED)
                .entity(R.ok(id))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "编辑权限组", description = "修改权限组名称和备注")
    public R<Void> update(@PathParam("id") Long id, @Valid @NotNull DataPermissionGroupUpdateDto dto) {
        service.update(id, dto);
        return R.ok();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "删除权限组", description = "未被用户权限引用时可删除，删除时级联删除所有关联")
    public R<Void> delete(@PathParam("id") Long id) {
        service.delete(id);
        return R.ok();
    }

    // Factory association endpoints

    @GET
    @Path("/{id}/factories")
    @Operation(summary = "获取权限组已关联的工厂列表", description = "返回权限组关联的工厂ID、编码、名称")
    public R<List<AssociatedEntityDto>> getFactories(@PathParam("id") Long id) {
        return R.ok(service.getFactories(id));
    }

    @POST
    @Path("/{id}/factories")
    @Operation(summary = "保存权限组工厂关联", description = "全量替换策略：提交所有需要关联的工厂ID列表")
    public R<Void> saveFactories(@PathParam("id") Long id, @Valid @NotNull AssociatedItemDto dto) {
        service.saveFactories(id, dto);
        return R.ok();
    }

    // Work Center association endpoints

    @GET
    @Path("/{id}/work-centers")
    @Operation(summary = "获取权限组已关联的工作中心列表", description = "返回权限组关联的工作中心ID、编码、名称")
    public R<List<AssociatedEntityDto>> getWorkCenters(@PathParam("id") Long id) {
        return R.ok(service.getWorkCenters(id));
    }

    @POST
    @Path("/{id}/work-centers")
    @Operation(summary = "保存权限组工作中心关联", description = "全量替换策略：提交所有需要关联的工作中心ID列表")
    public R<Void> saveWorkCenters(@PathParam("id") Long id, @Valid @NotNull AssociatedItemDto dto) {
        service.saveWorkCenters(id, dto);
        return R.ok();
    }

    // Process association endpoints

    @GET
    @Path("/{id}/processes")
    @Operation(summary = "获取权限组已关联的工序列表", description = "返回权限组关联的工序ID、编码、名称")
    public R<List<AssociatedEntityDto>> getProcesses(@PathParam("id") Long id) {
        return R.ok(service.getProcesses(id));
    }

    @POST
    @Path("/{id}/processes")
    @Operation(summary = "保存权限组工序关联", description = "全量替换策略：提交所有需要关联的工序ID列表")
    public R<Void> saveProcesses(@PathParam("id") Long id, @Valid @NotNull AssociatedItemDto dto) {
        service.saveProcesses(id, dto);
        return R.ok();
    }
}
