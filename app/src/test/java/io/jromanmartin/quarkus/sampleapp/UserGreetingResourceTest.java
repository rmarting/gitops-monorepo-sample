package io.jromanmartin.quarkus.sampleapp;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import io.jromanmartin.quarkus.sampleapp.model.Message;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

/**
 * Unit test for GreetingResource.
 *
 * References: https://quarkus.io/guides/getting-started-testing
 */
@QuarkusTest
class UserGreetingResourceTest {

    @Test
    void testHelloEndpoint() {
        given()
        .when()
            .get("/user")
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .body("message", is("Hello!"))
            .body("environment", is("local"));
    }

    @Test
    void testGreetingGetEndpoint() {
        String uuid = UUID.randomUUID().toString();
        given()
            .pathParam("name", uuid)
        .when()
            .get("/user/greeting/{name}")
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .body("message", is("Hello " + uuid + "!"))
            .body("environment", is("local"));
    }

    @Test
    void testGreetingPostEndpoint() {
        String uuid = UUID.randomUUID().toString();
        Message message = new Message();
        message.setName(uuid);

        given()
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .body(message)
        .when()

            .post("/user/greeting")
        .then()
            .statusCode(Response.Status.CREATED.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .body("message", is("Hello " + uuid + "!"))
            .body("messageType", is(Message.MessageType.GREETING.toString()))
            .body("environment", is("local"));
    }

    @Test
    void testFarewellPostEndpoint() {
        String uuid = UUID.randomUUID().toString();
        Message message = new Message();
        message.setName(uuid);

        given()
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .body(message)
        .when()
            .post("/user/farewell")
        .then()
            .statusCode(Response.Status.CREATED.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .body("message", is("Bye " + uuid + "!"))
            .body("messageType", is(Message.MessageType.FAREWELL.toString()))
            .body("environment", is("local"));
    }

}
