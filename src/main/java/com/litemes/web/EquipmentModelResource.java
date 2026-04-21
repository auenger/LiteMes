package com.litemes.web;

import com.litemes.application.service.EquipmentModelService;
import com.litemes.web.dto.EquipmentModelCreateDto;
import com.litemes.web.dto.EquipmentModelDto;
import com.litemes.web.dto.EquipmentModelQueryDto;
import com.litemes.web.dto.EquipmentModelUpdateDto;
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
 * REST API for Equipment Model CRUD operations.
 * Provides endpoints for managing equipment model data within type classifications.
 */
@Path("/api/equipment-models")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "EquipmentModel", description = "设备型号管理 API")
public class EquipmentModelResource {

    @Inject
    EquipmentModelService equipmentModelService;

    @GET
    @Operation(summary = "分页查询设备型号列表", description = "支持按编码/名称模糊查询，按设备类型和状态筛选")
    public R<PagedResult<EquipmentModelDto>> list(@BeanParam EquipmentModelQueryDto query) {
        return R.ok(equipmentModelService.list(query));
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "根据ID查询设备型号", description = "返回单个设备型号详情")
    public R<EquipmentModelDto> getById(@PathParam("id") Long id) {
        return R.ok(equipmentModelService.getById(id));
    }

    @POST
    @Operation(summary = "创建设备型号", description = "填写型号编码、名称和设备类型，编码创建后不可修改")
    public Response create(@Valid @NotNull EquipmentModelCreateDto dto) {
        Long id = equipmentModelService.create(dto);
        return Response.status(Response.Status.CREATED)
                .entity(R.ok(id))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "编辑设备型号", description = "修改型号名称和设备类型，编码不可修改")
    public R<Void> update(@PathParam("id") Long id, @Valid @NotNull EquipmentModelUpdateDto dto) {
        equipmentModelService.update(id, dto);
        return R.ok();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "删除设备型号", description = "未被设备台账引用时可删除，被引用时不可删除")
    public R<Void> delete(@PathParam("id") Long id) {
        equipmentModelService.delete(id);
        return R.ok();
    }

    @PUT
    @Path("/{id}/status")
    @Operation(summary = "启用/禁用设备型号", description = "切换设备型号启用禁用状态")
    public R<Void> updateStatus(@PathParam("id") Long id, @QueryParam("status") Integer status) {
        equipmentModelService.updateStatus(id, status);
        return R.ok();
    }
}
