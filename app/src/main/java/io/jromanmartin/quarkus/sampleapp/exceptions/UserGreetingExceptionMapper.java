package io.jromanmartin.quarkus.sampleapp.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.UUID;

/**
 * Exception handler for UserTransactionException
 *
 * Reference: https://developers.redhat.com/articles/2022/03/03/rest-api-error-modeling-quarkus-20
 */
@Provider
public class UserGreetingExceptionMapper implements ExceptionMapper<UserGreetingException> {

    @Override
    public Response toResponse(UserGreetingException exception) {
        String errorId = UUID.randomUUID().toString();

        ErrorResponse.ErrorMessage errorMessage = new ErrorResponse.ErrorMessage(exception.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(errorId, errorMessage);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
    }

}
