package com.litemes.web;

import com.litemes.application.service.UomService;
import com.litemes.web.dto.PagedResult;
import com.litemes.web.dto.R;
import com.litemes.web.dto.UomCreateDto;
import com.litemes.web.dto.UomDto;
import com.litemes.web.dto.UomQueryDto;
import com.litemes.web.dto.UomUpdateDto;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * REST API for Unit of Measure CRUD operations.
 * Provides endpoints for managing measurement units.
 */
@Path("/api/uoms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Uom", description = "计量单位管理 API")
public class UomResource {

    @Inject
    UomService uomService;

    @GET
    @Operation(summary = "分页查询单位列表", description = "支持按编码/名称模糊查询，按状态筛选")
    public R<PagedResult<UomDto>> list(@BeanParam UomQueryDto query) {
        return R.ok(uomService.list(query));
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "根据ID查询单位", description = "返回单个单位详情")
    public R<UomDto> getById(@PathParam("id") Long id) {
        return R.ok(uomService.getById(id));
    }

    @POST
    @Operation(summary = "创建单位", description = "填写单位编码、名称、精度，编码创建后不可修改")
    public Response create(@Valid @NotNull UomCreateDto dto) {
        Long id = uomService.create(dto);
        return Response.status(Response.Status.CREATED)
                .entity(R.ok(id))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "编辑单位", description = "修改单位名称和精度，单位编码不可修改")
    public R<Void> update(@PathParam("id") Long id, @Valid @NotNull UomUpdateDto dto) {
        uomService.update(id, dto);
        return R.ok();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "删除单位", description = "未被引用时可删除，被换算比例引用时不可删除")
    public R<Void> delete(@PathParam("id") Long id) {
        uomService.delete(id);
        return R.ok();
    }

    @PUT
    @Path("/{id}/status")
    @Operation(summary = "启用/禁用单位", description = "切换单位启用禁用状态")
    public R<Void> updateStatus(@PathParam("id") Long id, @QueryParam("status") Integer status) {
        uomService.updateStatus(id, status);
        return R.ok();
    }
}
