package com.litemes.web;

import com.litemes.application.service.DepartmentService;
import com.litemes.web.dto.DepartmentCreateDto;
import com.litemes.web.dto.DepartmentDto;
import com.litemes.web.dto.DepartmentQueryDto;
import com.litemes.web.dto.DepartmentUpdateDto;
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
 * REST API for Department CRUD operations.
 * Provides endpoints for managing department master data.
 */
@Path("/api/departments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Department", description = "部门管理 API")
public class DepartmentResource {

    @Inject
    DepartmentService departmentService;

    @GET
    @Operation(summary = "分页查询部门列表", description = "支持按编码/名称模糊查询，按工厂、状态筛选")
    public R<PagedResult<DepartmentDto>> list(@BeanParam DepartmentQueryDto query) {
        return R.ok(departmentService.list(query));
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "根据ID查询部门", description = "返回单个部门详情，包含所属工厂名称、上级部门名称")
    public R<DepartmentDto> getById(@PathParam("id") Long id) {
        return R.ok(departmentService.getById(id));
    }

    @POST
    @Operation(summary = "创建部门", description = "填写部门编码、名称、所属工厂、上级部门（可选），编码创建后不可修改")
    public Response create(@Valid @NotNull DepartmentCreateDto dto) {
        Long id = departmentService.create(dto);
        return Response.status(Response.Status.CREATED)
                .entity(R.ok(id))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "编辑部门", description = "修改部门名称、工厂、上级部门、排序号，部门编码不可修改")
    public R<Void> update(@PathParam("id") Long id, @Valid @NotNull DepartmentUpdateDto dto) {
        departmentService.update(id, dto);
        return R.ok();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "删除部门", description = "未被引用且无子部门时可删除")
    public R<Void> delete(@PathParam("id") Long id) {
        departmentService.delete(id);
        return R.ok();
    }

    @PUT
    @Path("/{id}/status")
    @Operation(summary = "启用/禁用部门", description = "切换部门启用禁用状态")
    public R<Void> updateStatus(@PathParam("id") Long id, @QueryParam("status") Integer status) {
        departmentService.updateStatus(id, status);
        return R.ok();
    }
}
