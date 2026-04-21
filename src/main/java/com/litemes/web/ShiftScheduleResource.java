package com.litemes.web;

import com.litemes.application.service.ShiftScheduleService;
import com.litemes.web.dto.PageDto;
import com.litemes.web.dto.R;
import com.litemes.web.dto.ShiftScheduleCreateDto;
import com.litemes.web.dto.ShiftScheduleDto;
import com.litemes.web.dto.ShiftScheduleUpdateDto;
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
 * REST API for ShiftSchedule CRUD operations.
 */
@Path("/api/shift-schedules")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "班制管理", description = "Shift Schedule management APIs")
public class ShiftScheduleResource {

    @Inject
    ShiftScheduleService shiftScheduleService;

    @GET
    @Operation(summary = "查询班制列表", description = "支持按编码/名称模糊查询，按状态筛选，分页返回")
    public R<PageDto<ShiftScheduleDto>> list(
            @QueryParam("shiftCode") String shiftCode,
            @QueryParam("name") String name,
            @QueryParam("status") Integer status,
            @QueryParam("pageNum") @DefaultValue("1") int pageNum,
            @QueryParam("pageSize") @DefaultValue("10") int pageSize) {
        return R.ok(shiftScheduleService.list(shiftCode, name, status, pageNum, pageSize));
    }

    @GET
    @Path("/all")
    @Operation(summary = "查询所有班制", description = "返回所有班制列表（不分页）")
    public R<List<ShiftScheduleDto>> listAll() {
        return R.ok(shiftScheduleService.listAll());
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "查询班制详情", description = "根据ID查询班制信息")
    public R<ShiftScheduleDto> getById(@PathParam("id") Long id) {
        return R.ok(shiftScheduleService.getById(id));
    }

    @POST
    @Operation(summary = "创建班制", description = "创建新班制，班制编码不可重复")
    public Response create(@Valid @NotNull ShiftScheduleCreateDto dto) {
        Long id = shiftScheduleService.create(dto);
        return Response.status(Response.Status.CREATED)
                .entity(R.ok(id))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "编辑班制", description = "修改班制名称、是否默认等（编码不可修改）")
    public R<Void> update(@PathParam("id") Long id, @Valid @NotNull ShiftScheduleUpdateDto dto) {
        shiftScheduleService.update(id, dto);
        return R.ok();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "删除班制", description = "软删除班制（有班次引用时不可删除）")
    public R<Void> delete(@PathParam("id") Long id) {
        shiftScheduleService.delete(id);
        return R.ok();
    }

    @PUT
    @Path("/{id}/status")
    @Operation(summary = "启用/禁用班制", description = "切换班制状态")
    public R<Void> updateStatus(@PathParam("id") Long id, @QueryParam("status") @NotNull Integer status) {
        shiftScheduleService.updateStatus(id, status);
        return R.ok();
    }
}
