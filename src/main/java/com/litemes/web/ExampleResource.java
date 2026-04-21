package com.litemes.web;

import com.litemes.application.service.ExampleService;
import com.litemes.web.dto.ExampleCreateDto;
import com.litemes.web.dto.ExampleDto;
import com.litemes.web.dto.ExampleUpdateDto;
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
 * REST API for ExampleEntity CRUD operations.
 * Validates the project skeleton end-to-end.
 */
@Path("/api/examples")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Example", description = "Example CRUD operations (skeleton validation)")
public class ExampleResource {

    @Inject
    ExampleService exampleService;

    @GET
    @Operation(summary = "List all examples", description = "Returns all example entities")
    public R<List<ExampleDto>> list() {
        return R.ok(exampleService.list());
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get example by ID", description = "Returns a single example entity")
    public R<ExampleDto> getById(@PathParam("id") Long id) {
        return R.ok(exampleService.getById(id));
    }

    @POST
    @Operation(summary = "Create example", description = "Creates a new example entity")
    public Response create(@Valid @NotNull ExampleCreateDto dto) {
        Long id = exampleService.create(dto);
        return Response.status(Response.Status.CREATED)
                .entity(R.ok(id))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update example", description = "Updates an existing example entity")
    public R<Void> update(@PathParam("id") Long id, @Valid @NotNull ExampleUpdateDto dto) {
        exampleService.update(id, dto);
        return R.ok();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete example", description = "Soft-deletes an example entity")
    public R<Void> delete(@PathParam("id") Long id) {
        exampleService.delete(id);
        return R.ok();
    }
}
