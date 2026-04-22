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
 * Integration tests for EquipmentTypeResource.
 * Validates CRUD operations, uniqueness validation, pagination, and status management.
 * Each test is self-contained and looks up the created equipment type by code.
 */
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EquipmentTypeResourceTest {

    private static final String TEST_TYPE_CODE = "DRILL-T";
    private static final String TEST_TYPE_NAME = "钻孔设备-T";

    /**
     * Helper: get the ID of the test equipment type by listing and filtering by code.
     */
    private Long getTestTypeId() {
        return ((Number) given()
            .queryParam("typeCode", TEST_TYPE_CODE)
            .when()
            .get("/api/equipment-types")
            .then()
            .statusCode(200)
            .extract()
            .path("data.records[0].id")).longValue();
    }

    @Test
    @Order(1)
    void shouldCreateEquipmentType() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"typeCode\": \"" + TEST_TYPE_CODE + "\", \"typeName\": \"" + TEST_TYPE_NAME + "\"}")
            .when()
            .post("/api/equipment-types")
            .then()
            .statusCode(201)
            .body("code", equalTo(200))
            .body("data", notNullValue());
    }

    @Test
    @Order(2)
    void shouldRejectDuplicateCode() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"typeCode\": \"" + TEST_TYPE_CODE + "\", \"typeName\": \"重复编码\"}")
            .when()
            .post("/api/equipment-types")
            .then()
            .statusCode(400)
            .body("message", containsString("设备类型编码已存在"));
    }

    @Test
    @Order(3)
    void shouldGetEquipmentTypeById() {
        Long id = getTestTypeId();
        given()
            .when()
            .get("/api/equipment-types/" + id)
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.typeCode", equalTo(TEST_TYPE_CODE))
            .body("data.typeName", equalTo(TEST_TYPE_NAME))
            .body("data.status", equalTo(1));
    }

    @Test
    @Order(4)
    void shouldUpdateEquipmentType() {
        Long id = getTestTypeId();
        given()
            .contentType(ContentType.JSON)
            .body("{\"typeName\": \"钻孔设备(更新)\"}")
            .when()
            .put("/api/equipment-types/" + id)
            .then()
            .statusCode(200)
            .body("code", equalTo(200));

        // Verify update - code unchanged, name changed
        given()
            .when()
            .get("/api/equipment-types/" + id)
            .then()
            .statusCode(200)
            .body("data.typeName", equalTo("钻孔设备(更新)"))
            .body("data.typeCode", equalTo(TEST_TYPE_CODE)); // Code unchanged
    }

    @Test
    @Order(5)
    void shouldListEquipmentTypes() {
        given()
            .when()
            .get("/api/equipment-types")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.records", not(empty()))
            .body("data.total", greaterThan(0));
    }

    @Test
    @Order(6)
    void shouldFilterByCode() {
        given()
            .queryParam("typeCode", TEST_TYPE_CODE)
            .when()
            .get("/api/equipment-types")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.records", not(empty()));
    }

    @Test
    @Order(7)
    void shouldFilterByName() {
        given()
            .queryParam("typeName", "钻孔")
            .when()
            .get("/api/equipment-types")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.records", not(empty()));
    }

    @Test
    @Order(8)
    void shouldToggleStatus() {
        Long id = getTestTypeId();

        // Disable
        given()
            .contentType(ContentType.JSON)
            .when()
            .put("/api/equipment-types/" + id + "/status?status=0")
            .then()
            .statusCode(200)
            .body("code", equalTo(200));

        // Verify disabled
        given()
            .when()
            .get("/api/equipment-types/" + id)
            .then()
            .statusCode(200)
            .body("data.status", equalTo(0));

        // Re-enable
        given()
            .contentType(ContentType.JSON)
            .when()
            .put("/api/equipment-types/" + id + "/status?status=1")
            .then()
            .statusCode(200)
            .body("code", equalTo(200));
    }

    @Test
    @Order(9)
    void shouldRejectStatusUnchanged() {
        Long id = getTestTypeId();
        given()
            .contentType(ContentType.JSON)
            .when()
            .put("/api/equipment-types/" + id + "/status?status=1")
            .then()
            .statusCode(400)
            .body("message", containsString("状态未发生变化"));
    }

    @Test
    @Order(10)
    void shouldReturnNotFoundForMissingId() {
        given()
            .when()
            .get("/api/equipment-types/99999")
            .then()
            .statusCode(400)
            .body("message", containsString("设备类型不存在"));
    }

    @Test
    @Order(11)
    void shouldRejectCreateWithBlankCode() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"typeCode\": \"\", \"typeName\": \"空编码\"}")
            .when()
            .post("/api/equipment-types")
            .then()
            .statusCode(422);
    }

    @Test
    @Order(12)
    void shouldRejectCreateWithoutName() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"typeCode\": \"NAMETEST\"}")
            .when()
            .post("/api/equipment-types")
            .then()
            .statusCode(422);
    }

    @Test
    @Order(13)
    void shouldDeleteEquipmentType() {
        // Create a new type to delete
        given()
            .contentType(ContentType.JSON)
            .body("{\"typeCode\": \"DELETE_TEST\", \"typeName\": \"待删除类型\"}")
            .when()
            .post("/api/equipment-types")
            .then()
            .statusCode(201);

        // Find it
        Long id = ((Number) given()
            .queryParam("typeCode", "DELETE_TEST")
            .when()
            .get("/api/equipment-types")
            .then()
            .statusCode(200)
            .extract()
            .path("data.records[0].id")).longValue();

        // Delete it
        given()
            .when()
            .delete("/api/equipment-types/" + id)
            .then()
            .statusCode(200)
            .body("code", equalTo(200));

        // Verify deleted - should return 400 NOT_FOUND
        given()
            .when()
            .get("/api/equipment-types/" + id)
            .then()
            .statusCode(400)
            .body("message", containsString("设备类型不存在"));
    }

    @Test
    @Order(14)
    void shouldFilterByStatus() {
        given()
            .queryParam("status", 1)
            .when()
            .get("/api/equipment-types")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.records", not(empty()));
    }
}
