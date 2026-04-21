package com.litemes.web;

import com.litemes.application.service.FactoryService;
import com.litemes.web.dto.FactoryCreateDto;
import com.litemes.web.dto.FactoryDto;
import com.litemes.web.dto.FactoryQueryDto;
import com.litemes.web.dto.FactoryUpdateDto;
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

/**
 * REST API for Factory CRUD operations.
 * Provides endpoints for managing factory master data.
 */
@Path("/api/factories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Factory", description = "工厂管理 API")
public class FactoryResource {

    @Inject
    FactoryService factoryService;

    @GET
    @Operation(summary = "分页查询工厂列表", description = "支持按编码/名称模糊查询，按公司、状态筛选")
    public R<PagedResult<FactoryDto>> list(@BeanParam FactoryQueryDto query) {
        return R.ok(factoryService.list(query));
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "根据ID查询工厂", description = "返回单个工厂详情，包含所属公司名称")
    public R<FactoryDto> getById(@PathParam("id") Long id) {
        return R.ok(factoryService.getById(id));
    }

    @POST
    @Operation(summary = "创建工厂", description = "填写工厂编码、名称、简称、所属公司，编码创建后不可修改")
    public Response create(@Valid @NotNull FactoryCreateDto dto) {
        Long id = factoryService.create(dto);
        return Response.status(Response.Status.CREATED)
                .entity(R.ok(id))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "编辑工厂", description = "修改工厂名称、简称、公司，工厂编码不可修改")
    public R<Void> update(@PathParam("id") Long id, @Valid @NotNull FactoryUpdateDto dto) {
        factoryService.update(id, dto);
        return R.ok();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "删除工厂", description = "未被引用时可删除，被引用时不可删除")
    public R<Void> delete(@PathParam("id") Long id) {
        factoryService.delete(id);
        return R.ok();
    }

    @PUT
    @Path("/{id}/status")
    @Operation(summary = "启用/禁用工厂", description = "切换工厂启用禁用状态")
    public R<Void> updateStatus(@PathParam("id") Long id, @QueryParam("status") Integer status) {
        factoryService.updateStatus(id, status);
        return R.ok();
    }
}
