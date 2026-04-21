package com.litemes.web;

import com.litemes.application.service.CompanyService;
import com.litemes.web.dto.CompanyCreateDto;
import com.litemes.web.dto.CompanyDto;
import com.litemes.web.dto.CompanyQueryDto;
import com.litemes.web.dto.CompanyUpdateDto;
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
 * REST API for Company CRUD operations.
 * Provides endpoints for managing company master data.
 */
@Path("/api/companies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Company", description = "公司管理 API")
public class CompanyResource {

    @Inject
    CompanyService companyService;

    @GET
    @Operation(summary = "分页查询公司列表", description = "支持按编码/名称模糊查询，按状态筛选")
    public R<PagedResult<CompanyDto>> list(@BeanParam CompanyQueryDto query) {
        return R.ok(companyService.list(query));
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "根据ID查询公司", description = "返回单个公司详情")
    public R<CompanyDto> getById(@PathParam("id") Long id) {
        return R.ok(companyService.getById(id));
    }

    @POST
    @Operation(summary = "创建公司", description = "填写公司编码、名称、简码，编码创建后不可修改")
    public Response create(@Valid @NotNull CompanyCreateDto dto) {
        Long id = companyService.create(dto);
        return Response.status(Response.Status.CREATED)
                .entity(R.ok(id))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "编辑公司", description = "修改公司名称和简码，公司编码不可修改")
    public R<Void> update(@PathParam("id") Long id, @Valid @NotNull CompanyUpdateDto dto) {
        companyService.update(id, dto);
        return R.ok();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "删除公司", description = "未被引用时可删除，被引用时不可删除")
    public R<Void> delete(@PathParam("id") Long id) {
        companyService.delete(id);
        return R.ok();
    }

    @PUT
    @Path("/{id}/status")
    @Operation(summary = "启用/禁用公司", description = "切换公司启用禁用状态")
    public R<Void> updateStatus(@PathParam("id") Long id, @QueryParam("status") Integer status) {
        companyService.updateStatus(id, status);
        return R.ok();
    }
}
