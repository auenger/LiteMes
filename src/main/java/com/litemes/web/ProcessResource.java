package com.litemes.web;

import com.litemes.application.service.ProcessService;
import com.litemes.web.dto.PagedResult;
import com.litemes.web.dto.ProcessCreateDto;
import com.litemes.web.dto.ProcessDto;
import com.litemes.web.dto.ProcessQueryDto;
import com.litemes.web.dto.ProcessUpdateDto;
import com.litemes.web.dto.R;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * REST API for Process CRUD operations.
 * Provides endpoints for managing process master data.
 */
@Path("/api/processes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Process", description = "工序管理 API")
public class ProcessResource {

    @Inject
    ProcessService processService;

    @GET
    @Operation(summary = "分页查询工序列表", description = "支持按编码/名称模糊查询，按工作中心、工厂级联、状态筛选")
    public R<PagedResult<ProcessDto>> list(@BeanParam ProcessQueryDto query) {
        return R.ok(processService.list(query));
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "根据ID查询工序", description = "返回单个工序详情，包含工作中心名称和工厂名称")
    public R<ProcessDto> getById(@PathParam("id") Long id) {
        return R.ok(processService.getById(id));
    }

    @POST
    @Operation(summary = "创建工序", description = "填写工序编码、名称、所属工作中心，编码创建后不可修改")
    public Response create(@Valid @NotNull ProcessCreateDto dto) {
        Long id = processService.create(dto);
        return Response.status(Response.Status.CREATED)
                .entity(R.ok(id))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "编辑工序", description = "修改工序名称，编码和所属工作中心不可修改")
    public R<Void> update(@PathParam("id") Long id, @Valid @NotNull ProcessUpdateDto dto) {
        processService.update(id, dto);
        return R.ok();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "删除工序", description = "未被引用时可删除，被引用时不可删除")
    public R<Void> delete(@PathParam("id") Long id) {
        processService.delete(id);
        return R.ok();
    }

    @PUT
    @Path("/{id}/status")
    @Operation(summary = "启用/禁用工序", description = "切换工序启用禁用状态")
    public R<Void> updateStatus(@PathParam("id") Long id, @QueryParam("status") Integer status) {
        processService.updateStatus(id, status);
        return R.ok();
    }
}
