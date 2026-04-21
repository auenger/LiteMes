package com.litemes.web;

import com.litemes.application.service.ShiftService;
import com.litemes.web.dto.R;
import com.litemes.web.dto.ShiftCreateDto;
import com.litemes.web.dto.ShiftDto;
import com.litemes.web.dto.ShiftUpdateDto;
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
 * REST API for Shift CRUD operations.
 * Shifts are managed under a ShiftSchedule parent.
 */
@Path("/api/shift-schedules/{scheduleId}/shifts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "班次管理", description = "Shift management APIs (nested under shift schedule)")
public class ShiftResource {

    @Inject
    ShiftService shiftService;

    @GET
    @Operation(summary = "查询班次列表", description = "查询指定班制下的所有班次")
    public R<List<ShiftDto>> list(@PathParam("scheduleId") Long scheduleId) {
        return R.ok(shiftService.listByScheduleId(scheduleId));
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "查询班次详情", description = "根据ID查询班次信息")
    public R<ShiftDto> getById(@PathParam("scheduleId") Long scheduleId, @PathParam("id") Long id) {
        return R.ok(shiftService.getById(id));
    }

    @POST
    @Operation(summary = "创建班次", description = "在指定班制下创建新班次，班次编码不可重复")
    public Response create(@PathParam("scheduleId") Long scheduleId, @Valid @NotNull ShiftCreateDto dto) {
        dto.setShiftScheduleId(scheduleId);
        Long id = shiftService.create(dto);
        return Response.status(Response.Status.CREATED)
                .entity(R.ok(id))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "编辑班次", description = "修改班次名称、时间等（编码不可修改）")
    public R<Void> update(@PathParam("scheduleId") Long scheduleId,
                          @PathParam("id") Long id,
                          @Valid @NotNull ShiftUpdateDto dto) {
        shiftService.update(id, dto);
        return R.ok();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "删除班次", description = "软删除班次")
    public R<Void> delete(@PathParam("scheduleId") Long scheduleId, @PathParam("id") Long id) {
        shiftService.delete(id);
        return R.ok();
    }
}
