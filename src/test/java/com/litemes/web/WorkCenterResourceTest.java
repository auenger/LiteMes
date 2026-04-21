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
 * Integration tests for WorkCenterResource.
 * Validates CRUD operations, uniqueness validation, pagination, and status management.
 */
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WorkCenterResourceTest {

    private static Long createdWorkCenterId;
    private static final Long FACTORY_ID = 1L; // Assumes factory with id=1 exists from prior seed data

    @Test
    @Order(1)
    void shouldCreateWorkCenter() {
        Object id = given()
            .contentType(ContentType.JSON)
            .body("{\"workCenterCode\": \"WC001\", \"name\": \"冲压工作中心\", \"factoryId\": " + FACTORY_ID + "}")
            .when()
            .post("/api/work-centers")
            .then()
            .statusCode(201)
            .body("code", equalTo(200))
            .body("data", notNullValue())
            .extract()
            .path("data");
        createdWorkCenterId = ((Number) id).longValue();
    }

    @Test
    @Order(2)
    void shouldRejectDuplicateCode() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"workCenterCode\": \"WC001\", \"name\": \"重复编码\", \"factoryId\": " + FACTORY_ID + "}")
            .when()
            .post("/api/work-centers")
            .then()
            .statusCode(400)
            .body("message", containsString("工作中心编码已存在"));
    }

    @Test
    @Order(3)
    void shouldGetWorkCenterById() {
        given()
            .when()
            .get("/api/work-centers/" + createdWorkCenterId)
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.workCenterCode", equalTo("WC001"))
            .body("data.name", equalTo("冲压工作中心"))
            .body("data.factoryId", comparesEqualTo((int) FACTORY_ID.longValue()))
            .body("data.factoryName", notNullValue())
            .body("data.status", equalTo(1));
    }

    @Test
    @Order(4)
    void shouldUpdateWorkCenter() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"name\": \"新冲压工作中心\"}")
            .when()
            .put("/api/work-centers/" + createdWorkCenterId)
            .then()
            .statusCode(200)
            .body("code", equalTo(200));

        // Verify update
        given()
            .when()
            .get("/api/work-centers/" + createdWorkCenterId)
            .then()
            .statusCode(200)
            .body("data.name", equalTo("新冲压工作中心"))
            .body("data.workCenterCode", equalTo("WC001")); // Code unchanged
    }

    @Test
    @Order(5)
    void shouldListWorkCenters() {
        given()
            .when()
            .get("/api/work-centers")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.records", not(empty()))
            .body("data.total", greaterThan(0));
    }

    @Test
    @Order(6)
    void shouldFilterByFactory() {
        given()
            .queryParam("factoryId", FACTORY_ID)
            .when()
            .get("/api/work-centers")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.records", not(empty()));
    }

    @Test
    @Order(7)
    void shouldFilterByName() {
        given()
            .queryParam("name", "冲压")
            .when()
            .get("/api/work-centers")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.records", not(empty()));
    }

    @Test
    @Order(8)
    void shouldToggleStatus() {
        // Disable
        given()
            .contentType(ContentType.JSON)
            .when()
            .put("/api/work-centers/" + createdWorkCenterId + "/status?status=0")
            .then()
            .statusCode(200)
            .body("code", equalTo(200));

        // Verify disabled
        given()
            .when()
            .get("/api/work-centers/" + createdWorkCenterId)
            .then()
            .statusCode(200)
            .body("data.status", equalTo(0));

        // Re-enable
        given()
            .contentType(ContentType.JSON)
            .when()
            .put("/api/work-centers/" + createdWorkCenterId + "/status?status=1")
            .then()
            .statusCode(200)
            .body("code", equalTo(200));
    }

    @Test
    @Order(9)
    void shouldFilterByStatus() {
        given()
            .queryParam("status", 0)
            .when()
            .get("/api/work-centers")
            .then()
            .statusCode(200)
            .body("code", equalTo(200));
    }

    @Test
    @Order(10)
    void shouldRejectCreateWithoutFactory() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"workCenterCode\": \"WC999\", \"name\": \"无工厂\"}")
            .when()
            .post("/api/work-centers")
            .then()
            .statusCode(422);
    }

    @Test
    @Order(11)
    void shouldRejectCreateWithBlankCode() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"workCenterCode\": \"\", \"name\": \"空编码\", \"factoryId\": " + FACTORY_ID + "}")
            .when()
            .post("/api/work-centers")
            .then()
            .statusCode(422);
    }

    @Test
    @Order(12)
    void shouldReturnNotFoundForMissingId() {
        given()
            .when()
            .get("/api/work-centers/99999")
            .then()
            .statusCode(400)
            .body("message", containsString("工作中心不存在"));
    }

    @Test
    @Order(13)
    void shouldDeleteWorkCenter() {
        // Create a second work center to delete
        Object id = given()
            .contentType(ContentType.JSON)
            .body("{\"workCenterCode\": \"WC002\", \"name\": \"待删除工作中心\", \"factoryId\": " + FACTORY_ID + "}")
            .when()
            .post("/api/work-centers")
            .then()
            .statusCode(201)
            .extract()
            .path("data");
        Long deleteId = ((Number) id).longValue();

        // Delete
        given()
            .when()
            .delete("/api/work-centers/" + deleteId)
            .then()
            .statusCode(200)
            .body("code", equalTo(200));

        // Verify deleted (should return 400 NOT_FOUND)
        given()
            .when()
            .get("/api/work-centers/" + deleteId)
            .then()
            .statusCode(400);
    }

    @Test
    @Order(14)
    void shouldRejectStatusUnchanged() {
        given()
            .contentType(ContentType.JSON)
            .when()
            .put("/api/work-centers/" + createdWorkCenterId + "/status?status=1")
            .then()
            .statusCode(400)
            .body("message", containsString("状态未发生变化"));
    }
}
