package sa.com.baj.transformers;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

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
                .when().get("/user")
                .then()
                .statusCode(200)
                .body(is("Hello!"));
    }

    @Test
    void testGreetingEndpoint() {
        String uuid = UUID.randomUUID().toString();
        given()
                .pathParam("name", uuid)
                .when().get("/user/greeting/{name}")
                .then()
                .statusCode(200)
                .body(is("Hello " + uuid + "!"));
    }

}
