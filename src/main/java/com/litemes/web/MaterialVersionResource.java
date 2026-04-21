package com.litemes.web;

import com.litemes.application.service.MaterialVersionService;
import com.litemes.web.dto.MaterialVersionCreateDto;
import com.litemes.web.dto.MaterialVersionDto;
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
 * REST API for Material Version operations.
 * Provides endpoints for managing material versions (A.1, A.2, etc.).
 */
@Path("/api/materials/{materialId}/versions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "MaterialVersion", description = "物料版本管理 API")
public class MaterialVersionResource {

    @Inject
    MaterialVersionService materialVersionService;

    @GET
    @Operation(summary = "查询物料版本列表", description = "返回指定物料的所有版本")
    public R<List<MaterialVersionDto>> listByMaterialId(@PathParam("materialId") Long materialId) {
        return R.ok(materialVersionService.listByMaterialId(materialId));
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "根据ID查询物料版本", description = "返回单个版本详情")
    public R<MaterialVersionDto> getById(@PathParam("materialId") Long materialId,
                                          @PathParam("id") Long id) {
        return R.ok(materialVersionService.getById(id));
    }

    @POST
    @Operation(summary = "创建物料版本", description = "为指定物料创建新版本，版本号在物料内唯一")
    public Response create(@PathParam("materialId") Long materialId,
                           @Valid @NotNull MaterialVersionCreateDto dto) {
        dto.setMaterialId(materialId);
        Long id = materialVersionService.create(dto);
        return Response.status(Response.Status.CREATED)
                .entity(R.ok(id))
                .build();
    }
}
