package com.litemes.web;

import com.litemes.application.service.UomConversionService;
import com.litemes.web.dto.PagedResult;
import com.litemes.web.dto.R;
import com.litemes.web.dto.UomConversionCreateDto;
import com.litemes.web.dto.UomConversionDto;
import com.litemes.web.dto.UomConversionQueryDto;
import com.litemes.web.dto.UomConversionUpdateDto;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * REST API for Unit of Measure Conversion CRUD operations.
 * Provides endpoints for managing measurement unit conversion rates.
 */
@Path("/api/uom-conversions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "UomConversion", description = "单位换算比例管理 API")
public class UomConversionResource {

    @Inject
    UomConversionService uomConversionService;

    @GET
    @Operation(summary = "分页查询换算比例列表", description = "支持按原单位/目标单位模糊查询，按状态筛选")
    public R<PagedResult<UomConversionDto>> list(@BeanParam UomConversionQueryDto query) {
        return R.ok(uomConversionService.list(query));
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "根据ID查询换算比例", description = "返回单条换算比例详情")
    public R<UomConversionDto> getById(@PathParam("id") Long id) {
        return R.ok(uomConversionService.getById(id));
    }

    @POST
    @Operation(summary = "创建换算比例", description = "选择原单位、目标单位，填写换算率，原单位+目标单位唯一")
    public Response create(@Valid @NotNull UomConversionCreateDto dto) {
        Long id = uomConversionService.create(dto);
        return Response.status(Response.Status.CREATED)
                .entity(R.ok(id))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "编辑换算比例", description = "修改原单位、目标单位和换算率")
    public R<Void> update(@PathParam("id") Long id, @Valid @NotNull UomConversionUpdateDto dto) {
        uomConversionService.update(id, dto);
        return R.ok();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "删除换算比例", description = "删除换算比例记录")
    public R<Void> delete(@PathParam("id") Long id) {
        uomConversionService.delete(id);
        return R.ok();
    }
}
