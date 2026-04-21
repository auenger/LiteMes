package com.litemes.web.exception;

import com.litemes.domain.event.BusinessException;
import com.litemes.web.dto.ErrorResponse;
import io.quarkus.security.AuthenticationFailedException;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

/**
 * Global exception mapper that converts all exceptions to structured JSON responses.
 *
 * - BusinessException -> 400 (Bad Request)
 * - AuthenticationFailedException -> 401 (Unauthorized)
 * - AuthorizationDeniedException -> 403 (Forbidden)
 * - Other exceptions -> 500 (Internal Server Error) with structured logging
 */
@Provider
@Priority(Priorities.USER + 1)
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class);

    @Override
    public Response toResponse(Exception exception) {
        if (exception instanceof BusinessException be) {
            LOG.debugf("Business exception: [%s] %s", be.getCode(), be.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(new ErrorResponse(be.getCode(), be.getMessage()))
                    .build();
        }

        if (exception instanceof AuthenticationFailedException) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(new ErrorResponse("UNAUTHORIZED", "Authentication required"))
                    .build();
        }

        if (exception.getClass().getName().contains("AuthorizationDeniedException")) {
            return Response.status(Response.Status.FORBIDDEN)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(new ErrorResponse("FORBIDDEN", "Access denied"))
                    .build();
        }

        // Unexpected exceptions - log full stack trace but don't expose to client
        LOG.errorf("Unexpected error: %s", exception.getMessage());
        LOG.debug("Stack trace:", exception);

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ErrorResponse("INTERNAL_ERROR", "Server internal error"))
                .build();
    }
}
