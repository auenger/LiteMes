package com.litemes.web;

import com.litemes.application.service.InspectionExemptionService;
import com.litemes.web.dto.InspectionExemptionCreateDto;
import com.litemes.web.dto.InspectionExemptionDto;
import com.litemes.web.dto.InspectionExemptionQueryDto;
import com.litemes.web.dto.InspectionExemptionUpdateDto;
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
 * REST API for InspectionExemption CRUD operations.
 * Provides endpoints for managing material inspection exemption rules.
 */
@Path("/api/inspection-exemptions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "InspectionExemption", description = "免检清单管理 API")
public class InspectionExemptionResource {

    @Inject
    InspectionExemptionService inspectionExemptionService;

    @GET
    @Operation(summary = "分页查询免检清单", description = "支持按物料、供应商筛选，按状态筛选")
    public R<PagedResult<InspectionExemptionDto>> list(@BeanParam InspectionExemptionQueryDto query) {
        return R.ok(inspectionExemptionService.list(query));
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "根据ID查询免检规则", description = "返回单个免检规则详情")
    public R<InspectionExemptionDto> getById(@PathParam("id") Long id) {
        return R.ok(inspectionExemptionService.getById(id));
    }

    @POST
    @Operation(summary = "创建免检规则", description = "物料必填，供应商和有效期可选。支持4种组合：全局永久/指定供应商永久/指定有效期/供应商+有效期")
    public Response create(@Valid @NotNull InspectionExemptionCreateDto dto) {
        Long id = inspectionExemptionService.create(dto);
        return Response.status(Response.Status.CREATED)
                .entity(R.ok(id))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "编辑免检规则", description = "可修改物料、供应商和有效期")
    public R<Void> update(@PathParam("id") Long id, @Valid @NotNull InspectionExemptionUpdateDto dto) {
        inspectionExemptionService.update(id, dto);
        return R.ok();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "删除免检规则", description = "已被引用的免检规则不可删除")
    public R<Void> delete(@PathParam("id") Long id) {
        inspectionExemptionService.delete(id);
        return R.ok();
    }

    @PUT
    @Path("/{id}/status")
    @Operation(summary = "启用/禁用免检规则", description = "切换免检规则启用禁用状态")
    public R<Void> updateStatus(@PathParam("id") Long id, @QueryParam("status") Integer status) {
        inspectionExemptionService.updateStatus(id, status);
        return R.ok();
    }

    @POST
    @Path("/scan-expired")
    @Operation(summary = "扫描并禁用过期规则", description = "手动触发过期免检规则的自动禁用")
    public R<Integer> scanExpired() {
        int count = inspectionExemptionService.disableExpiredRules();
        return R.ok(count);
    }
}
