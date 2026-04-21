package com.litemes.web;

import com.litemes.application.service.SupplierService;
import com.litemes.web.dto.PagedResult;
import com.litemes.web.dto.R;
import com.litemes.web.dto.SupplierCreateDto;
import com.litemes.web.dto.SupplierDto;
import com.litemes.web.dto.SupplierMaterialDto;
import com.litemes.web.dto.SupplierQueryDto;
import com.litemes.web.dto.SupplierUpdateDto;
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
 * REST API for Supplier CRUD operations.
 * Provides endpoints for managing supplier master data and material associations.
 */
@Path("/api/suppliers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Supplier", description = "供应商管理 API")
public class SupplierResource {

    @Inject
    SupplierService supplierService;

    @GET
    @Operation(summary = "分页查询供应商列表", description = "支持按编码/名称模糊查询，按状态筛选")
    public R<PagedResult<SupplierDto>> list(@BeanParam SupplierQueryDto query) {
        return R.ok(supplierService.list(query));
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "根据ID查询供应商", description = "返回单个供应商详情，含关联物料列表")
    public R<SupplierDto> getById(@PathParam("id") Long id) {
        return R.ok(supplierService.getById(id));
    }

    @POST
    @Operation(summary = "创建供应商", description = "填写供应商信息，编码和名称必填，编码创建后不可修改")
    public Response create(@Valid @NotNull SupplierCreateDto dto) {
        Long id = supplierService.create(dto);
        return Response.status(Response.Status.CREATED)
                .entity(R.ok(id))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "编辑供应商", description = "修改供应商信息（编码不可修改）")
    public R<Void> update(@PathParam("id") Long id, @Valid @NotNull SupplierUpdateDto dto) {
        supplierService.update(id, dto);
        return R.ok();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "删除供应商", description = "未被引用时可删除，被引用时不可删除")
    public R<Void> delete(@PathParam("id") Long id) {
        supplierService.delete(id);
        return R.ok();
    }

    @PUT
    @Path("/{id}/status")
    @Operation(summary = "启用/禁用供应商", description = "切换供应商启用禁用状态")
    public R<Void> updateStatus(@PathParam("id") Long id, @QueryParam("status") Integer status) {
        supplierService.updateStatus(id, status);
        return R.ok();
    }

    @POST
    @Path("/{id}/materials")
    @Operation(summary = "关联物料", description = "为供应商关联供货物料，支持多选，去重校验")
    public R<Void> linkMaterials(@PathParam("id") Long id, @Valid @NotNull List<Long> materialIds) {
        supplierService.linkMaterials(id, materialIds);
        return R.ok();
    }

    @DELETE
    @Path("/{id}/materials/{materialId}")
    @Operation(summary = "取消物料关联", description = "取消供应商与物料的关联")
    public R<Void> unlinkMaterial(@PathParam("id") Long id, @PathParam("materialId") Long materialId) {
        supplierService.unlinkMaterial(id, materialId);
        return R.ok();
    }

    @GET
    @Path("/{id}/materials")
    @Operation(summary = "查询供应商关联的物料列表", description = "返回供应商关联的所有物料信息")
    public R<List<SupplierMaterialDto>> getSupplierMaterials(@PathParam("id") Long id) {
        return R.ok(supplierService.getSupplierMaterials(id));
    }
}
