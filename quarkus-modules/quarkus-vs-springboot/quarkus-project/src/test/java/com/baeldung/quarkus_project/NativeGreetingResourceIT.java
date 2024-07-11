package com.baeldung.quarkus_project;

import static io.restassured.RestAssured.given;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import org.junit.jupiter.api.Test;

@QuarkusIntegrationTest
public class NativeGreetingResourceIT {

    @Test
    void testEndpoint() {
        given()
            .when().get("/hello")
            .then()
            .statusCode(200);
    }

}
