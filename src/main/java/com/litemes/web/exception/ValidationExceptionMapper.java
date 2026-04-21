package com.litemes.web.exception;

import com.litemes.web.dto.ErrorResponse;
import jakarta.annotation.Priority;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.util.stream.Collectors;

/**
 * Maps validation constraint violations to 422 Unprocessable Entity responses.
 * Returns a structured error with all validation messages combined.
 */
@Provider
@Priority(Priorities.USER)
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    private static final Logger LOG = Logger.getLogger(ValidationExceptionMapper.class);

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        String message = exception.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));

        LOG.debugf("Validation error: %s", message);

        return Response.status(422)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ErrorResponse("VALIDATION_ERROR", message))
                .build();
    }
}
