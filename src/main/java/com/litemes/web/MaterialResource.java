package com.litemes.web;

import com.litemes.application.service.MaterialService;
import com.litemes.web.dto.MaterialCreateDto;
import com.litemes.web.dto.MaterialDto;
import com.litemes.web.dto.MaterialQueryDto;
import com.litemes.web.dto.MaterialUpdateDto;
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
 * REST API for Material Master CRUD operations.
 * Provides endpoints for managing materials in PCB manufacturing.
 */
@Path("/api/materials")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Material", description = "物料信息管理 API")
public class MaterialResource {

    @Inject
    MaterialService materialService;

    @GET
    @Operation(summary = "分页查询物料列表", description = "支持按编码/名称模糊查询，按物料分类/基本分类/状态筛选")
    public R<PagedResult<MaterialDto>> list(@BeanParam MaterialQueryDto query) {
        return R.ok(materialService.list(query));
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "根据ID查询物料", description = "返回单个物料详情")
    public R<MaterialDto> getById(@PathParam("id") Long id) {
        return R.ok(materialService.getById(id));
    }

    @POST
    @Operation(summary = "创建物料", description = "填写物料编码、名称、分类、单位等，编码创建后不可修改")
    public Response create(@Valid @NotNull MaterialCreateDto dto) {
        Long id = materialService.create(dto);
        return Response.status(Response.Status.CREATED)
                .entity(R.ok(id))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "编辑物料", description = "修改物料名称、分类、单位等属性，物料编码不可修改")
    public R<Void> update(@PathParam("id") Long id, @Valid @NotNull MaterialUpdateDto dto) {
        materialService.update(id, dto);
        return R.ok();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "删除物料", description = "未被引用时可删除，被免检清单引用时不可删除")
    public R<Void> delete(@PathParam("id") Long id) {
        materialService.delete(id);
        return R.ok();
    }

    @PUT
    @Path("/{id}/status")
    @Operation(summary = "启用/禁用物料", description = "切换物料启用禁用状态")
    public R<Void> updateStatus(@PathParam("id") Long id, @QueryParam("status") Integer status) {
        materialService.updateStatus(id, status);
        return R.ok();
    }
}
