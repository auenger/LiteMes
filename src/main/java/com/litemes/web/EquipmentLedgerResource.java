package com.litemes.web;

import com.litemes.application.service.EquipmentLedgerService;
import com.litemes.web.dto.EquipmentLedgerCreateDto;
import com.litemes.web.dto.EquipmentLedgerDto;
import com.litemes.web.dto.EquipmentLedgerQueryDto;
import com.litemes.web.dto.EquipmentLedgerUpdateDto;
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
 * REST API for Equipment Ledger CRUD operations.
 * Provides endpoints for managing equipment asset ledger data.
 */
@Path("/api/equipment-ledger")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "EquipmentLedger", description = "设备台账管理 API")
public class EquipmentLedgerResource {

    @Inject
    EquipmentLedgerService equipmentLedgerService;

    @GET
    @Operation(summary = "分页查询设备台账列表", description = "支持按编码/名称模糊查询，按设备类型/型号/运行状态/管理状态/工厂/状态筛选")
    public R<PagedResult<EquipmentLedgerDto>> list(@BeanParam EquipmentLedgerQueryDto query) {
        return R.ok(equipmentLedgerService.list(query));
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "根据ID查询设备台账", description = "返回单个设备台账详情")
    public R<EquipmentLedgerDto> getById(@PathParam("id") Long id) {
        return R.ok(equipmentLedgerService.getById(id));
    }

    @POST
    @Operation(summary = "创建设备台账", description = "填写设备编码、名称、型号、运行/管理状态、工厂、入场日期，编码创建后不可修改")
    public Response create(@Valid @NotNull EquipmentLedgerCreateDto dto) {
        Long id = equipmentLedgerService.create(dto);
        return Response.status(Response.Status.CREATED)
                .entity(R.ok(id))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "编辑设备台账", description = "修改设备名称、型号、运行/管理状态、工厂等，编码和类型名称不可修改")
    public R<Void> update(@PathParam("id") Long id, @Valid @NotNull EquipmentLedgerUpdateDto dto) {
        equipmentLedgerService.update(id, dto);
        return R.ok();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "删除设备台账", description = "软删除设备台账记录")
    public R<Void> delete(@PathParam("id") Long id) {
        equipmentLedgerService.delete(id);
        return R.ok();
    }

    @PUT
    @Path("/{id}/status")
    @Operation(summary = "启用/禁用设备台账", description = "切换设备台账启用禁用状态")
    public R<Void> updateStatus(@PathParam("id") Long id, @QueryParam("status") Integer status) {
        equipmentLedgerService.updateStatus(id, status);
        return R.ok();
    }
}
