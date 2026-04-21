package com.litemes.web;

import com.litemes.application.service.EquipmentTypeService;
import com.litemes.web.dto.EquipmentTypeCreateDto;
import com.litemes.web.dto.EquipmentTypeDto;
import com.litemes.web.dto.EquipmentTypeQueryDto;
import com.litemes.web.dto.EquipmentTypeUpdateDto;
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
 * REST API for Equipment Type CRUD operations.
 * Provides endpoints for managing equipment type classifications.
 */
@Path("/api/equipment-types")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "EquipmentType", description = "设备类型管理 API")
public class EquipmentTypeResource {

    @Inject
    EquipmentTypeService equipmentTypeService;

    @GET
    @Operation(summary = "分页查询设备类型列表", description = "支持按编码/名称模糊查询，按状态筛选")
    public R<PagedResult<EquipmentTypeDto>> list(@BeanParam EquipmentTypeQueryDto query) {
        return R.ok(equipmentTypeService.list(query));
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "根据ID查询设备类型", description = "返回单个设备类型详情")
    public R<EquipmentTypeDto> getById(@PathParam("id") Long id) {
        return R.ok(equipmentTypeService.getById(id));
    }

    @POST
    @Operation(summary = "创建设备类型", description = "填写设备类型编码和名称，编码创建后不可修改")
    public Response create(@Valid @NotNull EquipmentTypeCreateDto dto) {
        Long id = equipmentTypeService.create(dto);
        return Response.status(Response.Status.CREATED)
                .entity(R.ok(id))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "编辑设备类型", description = "修改设备类型名称，编码不可修改")
    public R<Void> update(@PathParam("id") Long id, @Valid @NotNull EquipmentTypeUpdateDto dto) {
        equipmentTypeService.update(id, dto);
        return R.ok();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "删除设备类型", description = "未被引用时可删除，被设备型号引用时不可删除")
    public R<Void> delete(@PathParam("id") Long id) {
        equipmentTypeService.delete(id);
        return R.ok();
    }

    @PUT
    @Path("/{id}/status")
    @Operation(summary = "启用/禁用设备类型", description = "切换设备类型启用禁用状态")
    public R<Void> updateStatus(@PathParam("id") Long id, @QueryParam("status") Integer status) {
        equipmentTypeService.updateStatus(id, status);
        return R.ok();
    }
}
