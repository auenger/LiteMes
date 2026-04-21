package com.litemes.web;

import com.litemes.application.service.AuditLogService;
import com.litemes.web.dto.AuditLogDto;
import com.litemes.web.dto.AuditLogQueryDto;
import com.litemes.web.dto.PagedResult;
import com.litemes.web.dto.R;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * REST API for AuditLog query operations.
 * Provides endpoints for viewing change history of master data.
 */
@Path("/api/audit-logs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "AuditLog", description = "变更日志 API")
public class AuditLogResource {

    @Inject
    AuditLogService auditLogService;

    @GET
    @Operation(summary = "分页查询变更日志", description = "支持按表名、记录ID、时间范围筛选")
    public R<PagedResult<AuditLogDto>> list(@BeanParam AuditLogQueryDto query) {
        return R.ok(auditLogService.list(query));
    }
}
