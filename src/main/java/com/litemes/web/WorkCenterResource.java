package com.litemes.web;

import com.litemes.application.service.WorkCenterService;
import com.litemes.web.dto.PagedResult;
import com.litemes.web.dto.R;
import com.litemes.web.dto.WorkCenterCreateDto;
import com.litemes.web.dto.WorkCenterDto;
import com.litemes.web.dto.WorkCenterQueryDto;
import com.litemes.web.dto.WorkCenterUpdateDto;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * REST API for WorkCenter CRUD operations.
 * Provides endpoints for managing work center master data.
 */
@Path("/api/work-centers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "WorkCenter", description = "工作中心管理 API")
public class WorkCenterResource {

    @Inject
    WorkCenterService workCenterService;

    @GET
    @Operation(summary = "分页查询工作中心列表", description = "支持按编码/名称模糊查询，按工厂、状态筛选")
    public R<PagedResult<WorkCenterDto>> list(@BeanParam WorkCenterQueryDto query) {
        return R.ok(workCenterService.list(query));
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "根据ID查询工作中心", description = "返回单个工作中心详情，包含所属工厂名称")
    public R<WorkCenterDto> getById(@PathParam("id") Long id) {
        return R.ok(workCenterService.getById(id));
    }

    @POST
    @Operation(summary = "创建工作中心", description = "填写工作中心编码、名称、所属工厂，编码创建后不可修改")
    public Response create(@Valid @NotNull WorkCenterCreateDto dto) {
        Long id = workCenterService.create(dto);
        return Response.status(Response.Status.CREATED)
                .entity(R.ok(id))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "编辑工作中心", description = "修改工作中心名称，编码和所属工厂不可修改")
    public R<Void> update(@PathParam("id") Long id, @Valid @NotNull WorkCenterUpdateDto dto) {
        workCenterService.update(id, dto);
        return R.ok();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "删除工作中心", description = "未被引用时可删除，被引用时不可删除")
    public R<Void> delete(@PathParam("id") Long id) {
        workCenterService.delete(id);
        return R.ok();
    }

    @PUT
    @Path("/{id}/status")
    @Operation(summary = "启用/禁用工作中心", description = "切换工作中心启用禁用状态")
    public R<Void> updateStatus(@PathParam("id") Long id, @QueryParam("status") Integer status) {
        workCenterService.updateStatus(id, status);
        return R.ok();
    }
}
