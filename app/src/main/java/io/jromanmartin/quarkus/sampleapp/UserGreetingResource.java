package io.jromanmartin.quarkus.sampleapp;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import io.jromanmartin.quarkus.sampleapp.model.Message;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Sample REST Resource to demonstrate the use of the JAX-RS 2.0 API.
 * <p>
 * Quarkus Guides: https://quarkus.io/guides/rest-json
 */
@Path("/user")
@Tag(name = "User Greetings", description = "Operations to get greeting messages")
public class UserGreetingResource {

    @ConfigProperty(name = "app.environment")
    String environment;

    @Operation(summary = "Get a greeting")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Greeting",
                    content = @Content(schema = @Schema(implementation = Message.class))),
            @APIResponse(responseCode = "500", description = "Internal Server Error")})
    @Path("/")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Message hello() {
        Message message = new Message();
        message.setMessage("Hello!");
        message.setEnvironment(environment);

        return message;
    }

    @Operation(summary = "Get a greeting for a user")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Greeting message for the user",
                    content = @Content(schema = @Schema(implementation = Message.class))),
            @APIResponse(responseCode = "500", description = "Internal Server Error")})
    @Path("/greeting/{name}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Message greeting(@PathParam("name") String name) {
        Message message = new Message();
        message.setMessage("Hello " + name + "!");
        message.setEnvironment(environment);

        return message;
    }

    @Operation(summary = "Get a greeting for a user")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "201",
                    description = "Greeting message for the user",
                    content = @Content(schema = @Schema(implementation = Message.class))),
            @APIResponse(responseCode = "500", description = "Internal Server Error")})
    @Path("/greeting")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response greeting(Message message) {
        message.setMessageType(Message.MessageType.GREETING);
        message.setMessage("Hello " + message.getName() + "!");
        message.setEnvironment(environment);

        return Response
                .status(Response.Status.CREATED)
                .entity(message)
                .build();
    }

    @Operation(summary = "Get a farewell for a user")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "201",
                    description = "Farewell message for the user",
                    content = @Content(schema = @Schema(implementation = Message.class))),
            @APIResponse(responseCode = "500", description = "Internal Server Error")})
    @Path("/farewell")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response farewell(Message message) {
        message.setMessageType(Message.MessageType.FAREWELL);
        message.setMessage("Bye " + message.getName() + "!");
        message.setEnvironment(environment);

        return Response
                .status(Response.Status.CREATED)
                .entity(message)
                .build();
    }

}
