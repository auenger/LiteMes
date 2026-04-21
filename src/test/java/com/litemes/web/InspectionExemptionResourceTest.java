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
 * Integration tests for InspectionExemptionResource.
 * Validates CRUD operations, material validation, date range validation,
 * pagination, status management, and expiry logic.
 */
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InspectionExemptionResourceTest {

    private static final Long TEST_MATERIAL_ID = 1L;
    private static final Long TEST_MATERIAL_2_ID = 2L;

    /**
     * Helper: get the ID of the test exemption by listing and filtering by materialId.
     */
    private Long getTestExemptionId(Long materialId) {
        return ((Number) given()
            .queryParam("materialId", materialId)
            .when()
            .get("/api/inspection-exemptions")
            .then()
            .statusCode(200)
            .extract()
            .path("data.records[0].id")).longValue();
    }

    @Test
    @Order(1)
    void shouldCreateExemptionWithPermanentGlobal() {
        // Scenario 2: Permanent global exemption (no supplier, no validity)
        given()
            .contentType(ContentType.JSON)
            .body("{\"materialId\": " + TEST_MATERIAL_ID + "}")
            .when()
            .post("/api/inspection-exemptions")
            .then()
            .statusCode(201)
            .body("code", equalTo(200))
            .body("data", notNullValue());
    }

    @Test
    @Order(2)
    void shouldAutoFillMaterialInfo() {
        Long id = getTestExemptionId(TEST_MATERIAL_ID);
        given()
            .when()
            .get("/api/inspection-exemptions/" + id)
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.materialId", equalTo(1))
            .body("data.materialCode", equalTo("MAT-001"))
            .body("data.materialName", equalTo("测试物料001"))
            .body("data.status", equalTo(1))
            .body("data.expired", equalTo(false));
    }

    @Test
    @Order(3)
    void shouldCreateExemptionWithValidity() {
        // Scenario 1: Exemption with validity period
        given()
            .contentType(ContentType.JSON)
            .body("{\"materialId\": " + TEST_MATERIAL_2_ID + ", \"validFrom\": \"2026-01-01\", \"validTo\": \"2026-12-31\"}")
            .when()
            .post("/api/inspection-exemptions")
            .then()
            .statusCode(201)
            .body("code", equalTo(200))
            .body("data", notNullValue());
    }

    @Test
    @Order(4)
    void shouldRejectInvalidDateRange() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"materialId\": " + TEST_MATERIAL_ID + ", \"validFrom\": \"2026-12-31\", \"validTo\": \"2026-01-01\"}")
            .when()
            .post("/api/inspection-exemptions")
            .then()
            .statusCode(400)
            .body("message", containsString("有效开始日期不能晚于有效结束日期"));
    }

    @Test
    @Order(5)
    void shouldRejectMissingMaterialId() {
        given()
            .contentType(ContentType.JSON)
            .body("{}")
            .when()
            .post("/api/inspection-exemptions")
            .then()
            .statusCode(422);
    }

    @Test
    @Order(6)
    void shouldRejectNonExistentMaterial() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"materialId\": 99999}")
            .when()
            .post("/api/inspection-exemptions")
            .then()
            .statusCode(400)
            .body("message", containsString("物料不存在"));
    }

    @Test
    @Order(7)
    void shouldListExemptions() {
        given()
            .when()
            .get("/api/inspection-exemptions")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.records", not(empty()))
            .body("data.total", greaterThanOrEqualTo(2));
    }

    @Test
    @Order(8)
    void shouldFilterByMaterial() {
        given()
            .queryParam("materialId", TEST_MATERIAL_ID)
            .when()
            .get("/api/inspection-exemptions")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.records", not(empty()))
            .body("data.records[0].materialId", equalTo(1));
    }

    @Test
    @Order(9)
    void shouldFilterByStatus() {
        given()
            .queryParam("status", 1)
            .when()
            .get("/api/inspection-exemptions")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.records", not(empty()));
    }

    @Test
    @Order(10)
    void shouldUpdateExemption() {
        Long id = getTestExemptionId(TEST_MATERIAL_ID);
        given()
            .contentType(ContentType.JSON)
            .body("{\"validFrom\": \"2026-06-01\", \"validTo\": \"2026-12-31\"}")
            .when()
            .put("/api/inspection-exemptions/" + id)
            .then()
            .statusCode(200)
            .body("code", equalTo(200));

        // Verify update
        given()
            .when()
            .get("/api/inspection-exemptions/" + id)
            .then()
            .statusCode(200)
            .body("data.validFrom", equalTo("2026-06-01"))
            .body("data.validTo", equalTo("2026-12-31"));
    }

    @Test
    @Order(11)
    void shouldToggleStatus() {
        Long id = getTestExemptionId(TEST_MATERIAL_ID);

        // Disable
        given()
            .contentType(ContentType.JSON)
            .when()
            .put("/api/inspection-exemptions/" + id + "/status?status=0")
            .then()
            .statusCode(200)
            .body("code", equalTo(200));

        // Verify disabled
        given()
            .when()
            .get("/api/inspection-exemptions/" + id)
            .then()
            .statusCode(200)
            .body("data.status", equalTo(0));

        // Re-enable
        given()
            .contentType(ContentType.JSON)
            .when()
            .put("/api/inspection-exemptions/" + id + "/status?status=1")
            .then()
            .statusCode(200)
            .body("code", equalTo(200));
    }

    @Test
    @Order(12)
    void shouldRejectStatusUnchanged() {
        Long id = getTestExemptionId(TEST_MATERIAL_ID);
        given()
            .contentType(ContentType.JSON)
            .when()
            .put("/api/inspection-exemptions/" + id + "/status?status=1")
            .then()
            .statusCode(400)
            .body("message", containsString("状态未发生变化"));
    }

    @Test
    @Order(13)
    void shouldReturnNotFoundForMissingId() {
        given()
            .when()
            .get("/api/inspection-exemptions/99999")
            .then()
            .statusCode(400)
            .body("message", containsString("免检规则不存在"));
    }

    @Test
    @Order(14)
    void shouldDeleteExemption() {
        // Create a new exemption to delete
        given()
            .contentType(ContentType.JSON)
            .body("{\"materialId\": " + TEST_MATERIAL_2_ID + "}")
            .when()
            .post("/api/inspection-exemptions")
            .then()
            .statusCode(201);

        // Find it
        Long id = getTestExemptionId(TEST_MATERIAL_2_ID);

        // Delete it
        given()
            .when()
            .delete("/api/inspection-exemptions/" + id)
            .then()
            .statusCode(200)
            .body("code", equalTo(200));

        // Verify deleted
        given()
            .when()
            .get("/api/inspection-exemptions/" + id)
            .then()
            .statusCode(400)
            .body("message", containsString("免检规则不存在"));
    }

    @Test
    @Order(15)
    void shouldScanExpiredRules() {
        given()
            .contentType(ContentType.JSON)
            .when()
            .post("/api/inspection-exemptions/scan-expired")
            .then()
            .statusCode(200)
            .body("code", equalTo(200));
    }

    @Test
    @Order(16)
    void shouldGetMaterialDropdown() {
        given()
            .when()
            .get("/api/dropdown/materials")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data", not(empty()))
            .body("data[0].code", notNullValue());
    }
}
