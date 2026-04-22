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
 * Integration tests for ProcessResource.
 * Validates CRUD operations, uniqueness validation, pagination, cascading queries, and status management.
 */
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProcessResourceTest {

    private static Long createdProcessId;
    private static final Long WORK_CENTER_ID = 1L; // Assumes work center with id=1 exists from prior seed data

    @Test
    @Order(1)
    void shouldCreateProcess() {
        Object id = given()
            .contentType(ContentType.JSON)
            .body("{\"processCode\": \"PS-TEST-001\", \"name\": \"开料\", \"workCenterId\": " + WORK_CENTER_ID + "}")
            .when()
            .post("/api/processes")
            .then()
            .statusCode(201)
            .body("code", equalTo(200))
            .body("data", notNullValue())
            .extract()
            .path("data");
        createdProcessId = ((Number) id).longValue();
    }

    @Test
    @Order(2)
    void shouldRejectDuplicateCode() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"processCode\": \"PS-TEST-001\", \"name\": \"重复编码\", \"workCenterId\": " + WORK_CENTER_ID + "}")
            .when()
            .post("/api/processes")
            .then()
            .statusCode(400)
            .body("message", containsString("工序编码已存在"));
    }

    @Test
    @Order(3)
    void shouldGetProcessById() {
        given()
            .when()
            .get("/api/processes/" + createdProcessId)
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.processCode", equalTo("PS-TEST-001"))
            .body("data.name", equalTo("开料"))
            .body("data.workCenterId", comparesEqualTo((int) WORK_CENTER_ID.longValue()))
            .body("data.workCenterName", notNullValue())
            .body("data.status", equalTo(1));
    }

    @Test
    @Order(4)
    void shouldUpdateProcess() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"name\": \"激光开料\"}")
            .when()
            .put("/api/processes/" + createdProcessId)
            .then()
            .statusCode(200)
            .body("code", equalTo(200));

        // Verify update
        given()
            .when()
            .get("/api/processes/" + createdProcessId)
            .then()
            .statusCode(200)
            .body("data.name", equalTo("激光开料"))
            .body("data.processCode", equalTo("PS-TEST-001")); // Code unchanged
    }

    @Test
    @Order(5)
    void shouldListProcesses() {
        given()
            .when()
            .get("/api/processes")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.records", not(empty()))
            .body("data.total", greaterThan(0));
    }

    @Test
    @Order(6)
    void shouldFilterByWorkCenter() {
        given()
            .queryParam("workCenterId", WORK_CENTER_ID)
            .when()
            .get("/api/processes")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.records", not(empty()));
    }

    @Test
    @Order(7)
    void shouldFilterByName() {
        given()
            .queryParam("name", "开料")
            .when()
            .get("/api/processes")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.records", not(empty()));
    }

    @Test
    @Order(8)
    void shouldFilterByProcessCode() {
        given()
            .queryParam("processCode", "PS-TEST")
            .when()
            .get("/api/processes")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.records", not(empty()));
    }

    @Test
    @Order(9)
    void shouldToggleStatus() {
        // Disable
        given()
            .contentType(ContentType.JSON)
            .when()
            .put("/api/processes/" + createdProcessId + "/status?status=0")
            .then()
            .statusCode(200)
            .body("code", equalTo(200));

        // Verify disabled
        given()
            .when()
            .get("/api/processes/" + createdProcessId)
            .then()
            .statusCode(200)
            .body("data.status", equalTo(0));

        // Re-enable
        given()
            .contentType(ContentType.JSON)
            .when()
            .put("/api/processes/" + createdProcessId + "/status?status=1")
            .then()
            .statusCode(200)
            .body("code", equalTo(200));
    }

    @Test
    @Order(10)
    void shouldFilterByStatus() {
        given()
            .queryParam("status", 0)
            .when()
            .get("/api/processes")
            .then()
            .statusCode(200)
            .body("code", equalTo(200));
    }

    @Test
    @Order(11)
    void shouldRejectCreateWithoutWorkCenter() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"processCode\": \"PS999\", \"name\": \"无工作中心\"}")
            .when()
            .post("/api/processes")
            .then()
            .statusCode(422);
    }

    @Test
    @Order(12)
    void shouldRejectCreateWithBlankCode() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"processCode\": \"\", \"name\": \"空编码\", \"workCenterId\": " + WORK_CENTER_ID + "}")
            .when()
            .post("/api/processes")
            .then()
            .statusCode(422);
    }

    @Test
    @Order(13)
    void shouldReturnNotFoundForMissingId() {
        given()
            .when()
            .get("/api/processes/99999")
            .then()
            .statusCode(400)
            .body("message", containsString("工序不存在"));
    }

    @Test
    @Order(14)
    void shouldDeleteProcess() {
        // Create a second process to delete
        Object id = given()
            .contentType(ContentType.JSON)
            .body("{\"processCode\": \"PS-TEST-002\", \"name\": \"待删除工序\", \"workCenterId\": " + WORK_CENTER_ID + "}")
            .when()
            .post("/api/processes")
            .then()
            .statusCode(201)
            .extract()
            .path("data");
        Long deleteId = ((Number) id).longValue();

        // Delete
        given()
            .when()
            .delete("/api/processes/" + deleteId)
            .then()
            .statusCode(200)
            .body("code", equalTo(200));

        // Verify deleted (should return 400 NOT_FOUND)
        given()
            .when()
            .get("/api/processes/" + deleteId)
            .then()
            .statusCode(400);
    }

    @Test
    @Order(15)
    void shouldRejectStatusUnchanged() {
        given()
            .contentType(ContentType.JSON)
            .when()
            .put("/api/processes/" + createdProcessId + "/status?status=1")
            .then()
            .statusCode(400)
            .body("message", containsString("状态未发生变化"));
    }

    @Test
    @Order(16)
    void shouldRejectNonExistentWorkCenter() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"processCode\": \"PS-TEST-998\", \"name\": \"不存在的工作中心\", \"workCenterId\": 99999}")
            .when()
            .post("/api/processes")
            .then()
            .statusCode(400)
            .body("message", containsString("所属工作中心不存在"));
    }
}
