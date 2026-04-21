package com.litemes.web;

import com.litemes.application.service.MaterialCategoryService;
import com.litemes.web.dto.MaterialCategoryCreateDto;
import com.litemes.web.dto.MaterialCategoryDto;
import com.litemes.web.dto.MaterialCategoryQueryDto;
import com.litemes.web.dto.MaterialCategoryTreeDto;
import com.litemes.web.dto.MaterialCategoryUpdateDto;
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
 * REST API for MaterialCategory CRUD operations.
 * Provides endpoints for managing material category master data with tree hierarchy support.
 */
@Path("/api/material-categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Material Category", description = "物料分类管理 API")
public class MaterialCategoryResource {

    @Inject
    MaterialCategoryService materialCategoryService;

    @GET
    @Operation(summary = "分页查询物料分类列表", description = "支持按编码/名称模糊查询，按状态筛选")
    public R<PagedResult<MaterialCategoryDto>> list(@BeanParam MaterialCategoryQueryDto query) {
        return R.ok(materialCategoryService.list(query));
    }

    @GET
    @Path("/tree")
    @Operation(summary = "获取物料分类树形结构", description = "返回完整的分类树形结构，用于左侧树形展示")
    public R<List<MaterialCategoryTreeDto>> tree() {
        return R.ok(materialCategoryService.tree());
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "根据ID查询物料分类", description = "返回单个分类详情，包含上级分类名称")
    public R<MaterialCategoryDto> getById(@PathParam("id") Long id) {
        return R.ok(materialCategoryService.getById(id));
    }

    @POST
    @Operation(summary = "创建物料分类", description = "填写分类编码、名称、是否质量分类、上级分类（可选），编码创建后不可修改")
    public Response create(@Valid @NotNull MaterialCategoryCreateDto dto) {
        Long id = materialCategoryService.create(dto);
        return Response.status(Response.Status.CREATED)
                .entity(R.ok(id))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "编辑物料分类", description = "修改分类名称、是否质量分类、上级分类，分类编码不可修改")
    public R<Void> update(@PathParam("id") Long id, @Valid @NotNull MaterialCategoryUpdateDto dto) {
        materialCategoryService.update(id, dto);
        return R.ok();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "删除物料分类", description = "未被引用且无子分类时可删除")
    public R<Void> delete(@PathParam("id") Long id) {
        materialCategoryService.delete(id);
        return R.ok();
    }

    @PUT
    @Path("/{id}/status")
    @Operation(summary = "启用/禁用物料分类", description = "切换分类启用禁用状态")
    public R<Void> updateStatus(@PathParam("id") Long id, @QueryParam("status") Integer status) {
        materialCategoryService.updateStatus(id, status);
        return R.ok();
    }
}
