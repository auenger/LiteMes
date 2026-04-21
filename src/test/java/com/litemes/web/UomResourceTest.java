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
 * Integration tests for UomResource.
 * Validates CRUD operations, uniqueness validation, pagination, and status management.
 * Each test is self-contained and looks up the created uom by code.
 */
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UomResourceTest {

    private static final String TEST_UOM_CODE = "PCS";
    private static final String TEST_UOM_NAME = "个";

    /**
     * Helper: get the ID of the test uom by listing and filtering by code.
     */
    private Long getTestUomId() {
        return ((Number) given()
            .queryParam("uomCode", TEST_UOM_CODE)
            .when()
            .get("/api/uoms")
            .then()
            .statusCode(200)
            .extract()
            .path("data.records[0].id")).longValue();
    }

    @Test
    @Order(1)
    void shouldCreateUom() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"uomCode\": \"" + TEST_UOM_CODE + "\", \"uomName\": \"" + TEST_UOM_NAME + "\", \"precision\": 0}")
            .when()
            .post("/api/uoms")
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
            .body("{\"uomCode\": \"" + TEST_UOM_CODE + "\", \"uomName\": \"重复编码\"}")
            .when()
            .post("/api/uoms")
            .then()
            .statusCode(400)
            .body("message", containsString("单位编码已存在"));
    }

    @Test
    @Order(3)
    void shouldRejectDuplicateName() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"uomCode\": \"PCS2\", \"uomName\": \"" + TEST_UOM_NAME + "\"}")
            .when()
            .post("/api/uoms")
            .then()
            .statusCode(400)
            .body("message", containsString("单位名称已存在"));
    }

    @Test
    @Order(4)
    void shouldGetUomById() {
        Long id = getTestUomId();
        given()
            .when()
            .get("/api/uoms/" + id)
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.uomCode", equalTo(TEST_UOM_CODE))
            .body("data.uomName", equalTo(TEST_UOM_NAME))
            .body("data.status", equalTo(1));
    }

    @Test
    @Order(5)
    void shouldUpdateUom() {
        Long id = getTestUomId();
        given()
            .contentType(ContentType.JSON)
            .body("{\"uomName\": \"件\", \"precision\": 1}")
            .when()
            .put("/api/uoms/" + id)
            .then()
            .statusCode(200)
            .body("code", equalTo(200));

        // Verify update - code unchanged, name changed
        given()
            .when()
            .get("/api/uoms/" + id)
            .then()
            .statusCode(200)
            .body("data.uomName", equalTo("件"))
            .body("data.uomCode", equalTo(TEST_UOM_CODE)); // Code unchanged
    }

    @Test
    @Order(6)
    void shouldListUoms() {
        given()
            .when()
            .get("/api/uoms")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.records", not(empty()))
            .body("data.total", greaterThan(0));
    }

    @Test
    @Order(7)
    void shouldFilterByCode() {
        given()
            .queryParam("uomCode", TEST_UOM_CODE)
            .when()
            .get("/api/uoms")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.records", not(empty()));
    }

    @Test
    @Order(8)
    void shouldFilterByName() {
        given()
            .queryParam("uomName", "件")
            .when()
            .get("/api/uoms")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.records", not(empty()));
    }

    @Test
    @Order(9)
    void shouldToggleStatus() {
        Long id = getTestUomId();

        // Disable
        given()
            .contentType(ContentType.JSON)
            .when()
            .put("/api/uoms/" + id + "/status?status=0")
            .then()
            .statusCode(200)
            .body("code", equalTo(200));

        // Verify disabled
        given()
            .when()
            .get("/api/uoms/" + id)
            .then()
            .statusCode(200)
            .body("data.status", equalTo(0));

        // Re-enable
        given()
            .contentType(ContentType.JSON)
            .when()
            .put("/api/uoms/" + id + "/status?status=1")
            .then()
            .statusCode(200)
            .body("code", equalTo(200));
    }

    @Test
    @Order(10)
    void shouldRejectStatusUnchanged() {
        Long id = getTestUomId();
        given()
            .contentType(ContentType.JSON)
            .when()
            .put("/api/uoms/" + id + "/status?status=1")
            .then()
            .statusCode(400)
            .body("message", containsString("状态未发生变化"));
    }

    @Test
    @Order(11)
    void shouldReturnNotFoundForMissingId() {
        given()
            .when()
            .get("/api/uoms/99999")
            .then()
            .statusCode(400)
            .body("message", containsString("单位不存在"));
    }

    @Test
    @Order(12)
    void shouldRejectCreateWithBlankCode() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"uomCode\": \"\", \"uomName\": \"空编码\"}")
            .when()
            .post("/api/uoms")
            .then()
            .statusCode(422);
    }

    @Test
    @Order(13)
    void shouldRejectCreateWithoutName() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"uomCode\": \"NAMETEST\"}")
            .when()
            .post("/api/uoms")
            .then()
            .statusCode(422);
    }
}
