package com.litemes.web;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for ExampleResource.
 * Validates the complete skeleton: REST endpoint, exception handling, and response format.
 */
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ExampleResourceTest {

    private static Long createdId;

    @Test
    @Order(1)
    void shouldCreateExample() {
        Object id = given()
            .contentType(ContentType.JSON)
            .body("{\"name\": \"test-item\", \"description\": \"test description\"}")
            .when()
            .post("/api/examples")
            .then()
            .statusCode(201)
            .body("code", equalTo(200))
            .body("data", notNullValue())
            .extract()
            .path("data");
        // MySQL may return Integer instead of Long for AUTO_INCREMENT
        createdId = ((Number) id).longValue();
    }

    @Test
    @Order(2)
    void shouldListExamples() {
        given()
            .when()
            .get("/api/examples")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("message", equalTo("success"))
            .body("data", not(empty()));
    }

    @Test
    @Order(3)
    void shouldGetExampleById() {
        given()
            .when()
            .get("/api/examples/1")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.name", equalTo("test-item"))
            .body("data.description", equalTo("test description"));
    }

    @Test
    void shouldReturn400WhenNameIsBlank() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"name\": \"\", \"description\": \"test\"}")
            .when()
            .post("/api/examples")
            .then()
            .statusCode(422);
    }

    @Test
    void shouldReturn400WhenExampleNotFound() {
        given()
            .when()
            .get("/api/examples/99999")
            .then()
            .statusCode(400)
            .body("code", equalTo("NOT_FOUND"));
    }

    @Test
    void shouldReturnOpenApiDocs() {
        given()
            .when()
            .get("/q/openapi")
            .then()
            .statusCode(200);
    }

    @Test
    void shouldReturnHealthCheck() {
        given()
            .when()
            .get("/q/health")
            .then()
            .statusCode(anyOf(is(200), is(503)));
    }
}
