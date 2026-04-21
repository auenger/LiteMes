package com.litemes.web;

import com.litemes.application.service.CustomerService;
import com.litemes.web.dto.CustomerCreateDto;
import com.litemes.web.dto.CustomerDto;
import com.litemes.web.dto.CustomerMaterialDto;
import com.litemes.web.dto.CustomerQueryDto;
import com.litemes.web.dto.CustomerUpdateDto;
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
 * REST API for Customer CRUD operations.
 * Provides endpoints for managing customer master data and material associations.
 */
@Path("/api/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Customer", description = "客户管理 API")
public class CustomerResource {

    @Inject
    CustomerService customerService;

    @GET
    @Operation(summary = "分页查询客户列表", description = "支持按编码/名称模糊查询，按类型/状态筛选")
    public R<PagedResult<CustomerDto>> list(@BeanParam CustomerQueryDto query) {
        return R.ok(customerService.list(query));
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "根据ID查询客户", description = "返回单个客户详情，含关联物料列表")
    public R<CustomerDto> getById(@PathParam("id") Long id) {
        return R.ok(customerService.getById(id));
    }

    @POST
    @Operation(summary = "创建客户", description = "填写客户信息，编码和名称必填，编码创建后不可修改")
    public Response create(@Valid @NotNull CustomerCreateDto dto) {
        Long id = customerService.create(dto);
        return Response.status(Response.Status.CREATED)
                .entity(R.ok(id))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "编辑客户", description = "修改客户信息（编码不可修改）")
    public R<Void> update(@PathParam("id") Long id, @Valid @NotNull CustomerUpdateDto dto) {
        customerService.update(id, dto);
        return R.ok();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "删除客户", description = "未被引用时可删除，被引用时不可删除")
    public R<Void> delete(@PathParam("id") Long id) {
        customerService.delete(id);
        return R.ok();
    }

    @PUT
    @Path("/{id}/status")
    @Operation(summary = "启用/禁用客户", description = "切换客户启用禁用状态")
    public R<Void> updateStatus(@PathParam("id") Long id, @QueryParam("status") Integer status) {
        customerService.updateStatus(id, status);
        return R.ok();
    }

    @POST
    @Path("/{id}/materials")
    @Operation(summary = "关联物料", description = "为客户关联成品物料，支持多选，去重校验")
    public R<Void> linkMaterials(@PathParam("id") Long id, @Valid @NotNull List<Long> materialIds) {
        customerService.linkMaterials(id, materialIds);
        return R.ok();
    }

    @DELETE
    @Path("/{id}/materials/{materialId}")
    @Operation(summary = "取消物料关联", description = "取消客户与物料的关联")
    public R<Void> unlinkMaterial(@PathParam("id") Long id, @PathParam("materialId") Long materialId) {
        customerService.unlinkMaterial(id, materialId);
        return R.ok();
    }

    @GET
    @Path("/{id}/materials")
    @Operation(summary = "查询客户关联的物料列表", description = "返回客户关联的所有物料信息")
    public R<List<CustomerMaterialDto>> getCustomerMaterials(@PathParam("id") Long id) {
        return R.ok(customerService.getCustomerMaterials(id));
    }
}
