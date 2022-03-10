package sa.com.baj.transformers;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Sample REST Resource to demonstrate the use of the JAX-RS 2.0 API.
 *
 * Quarkus Guides: https://quarkus.io/guides/rest-json
 */
@Path("/user")
@Tag(name = "User Greetings", description = "Operations to get greeting messages")
public class UserGreetingResource {

    @Operation(summary = "Get a greeting")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Greeting",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @APIResponse(responseCode = "500", description = "Internal Server Error")})
    @Path("/")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello!";
    }

    @Operation(summary = "Get a greeting for a user")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Greeting message for the user",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @APIResponse(responseCode = "500", description = "Internal Server Error")})
    @Path("/greeting/{name}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String greeting(@PathParam("name") String name) {
        return "Hello " + name + "!";
    }

}
