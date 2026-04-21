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
 * Integration tests for UomConversionResource.
 * Validates CRUD operations, uniqueness validation, and pagination.
 * Each test looks up uom IDs dynamically to avoid static state issues.
 */
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UomConversionResourceTest {

    private static final String FROM_UOM_CODE = "UC_KG";
    private static final String TO_UOM_CODE = "UC_GRAM";

    /**
     * Helper: find uom ID by exact code using getById after list.
     * Uses both code and name filters to ensure exact match.
     */
    private Long findUomIdByCode(String code, String name) {
        return ((Number) given()
            .queryParam("uomCode", code)
            .queryParam("uomName", name)
            .when()
            .get("/api/uoms")
            .then()
            .statusCode(200)
            .extract()
            .path("data.records[0].id")).longValue();
    }

    /**
     * Helper: get the test conversion ID by listing.
     */
    private Long getTestConversionId() {
        return ((Number) given()
            .queryParam("fromUom", FROM_UOM_CODE)
            .queryParam("toUom", TO_UOM_CODE)
            .when()
            .get("/api/uom-conversions")
            .then()
            .statusCode(200)
            .extract()
            .path("data.records[0].id")).longValue();
    }

    @Test
    @Order(1)
    void shouldSetupUomsAndCreateConversion() {
        // Create from uom
        given()
            .contentType(ContentType.JSON)
            .body("{\"uomCode\": \"" + FROM_UOM_CODE + "\", \"uomName\": \"千克_测试\"}")
            .when()
            .post("/api/uoms")
            .then()
            .statusCode(anyOf(is(201), is(400)));

        // Create to uom
        given()
            .contentType(ContentType.JSON)
            .body("{\"uomCode\": \"" + TO_UOM_CODE + "\", \"uomName\": \"克_测试\"}")
            .when()
            .post("/api/uoms")
            .then()
            .statusCode(anyOf(is(201), is(400)));

        Long fromUomId = findUomIdByCode(FROM_UOM_CODE, "千克_测试");
        Long toUomId = findUomIdByCode(TO_UOM_CODE, "克_测试");

        // Create conversion (may already exist from prior run)
        given()
            .contentType(ContentType.JSON)
            .body("{\"fromUomId\": " + fromUomId + ", \"toUomId\": " + toUomId + ", \"conversionRate\": 1000}")
            .when()
            .post("/api/uom-conversions")
            .then()
            .statusCode(anyOf(is(201), is(400)));
    }

    @Test
    @Order(2)
    void shouldRejectDuplicateConversion() {
        Long fromUomId = findUomIdByCode(FROM_UOM_CODE, "千克_测试");
        Long toUomId = findUomIdByCode(TO_UOM_CODE, "克_测试");

        given()
            .contentType(ContentType.JSON)
            .body("{\"fromUomId\": " + fromUomId + ", \"toUomId\": " + toUomId + ", \"conversionRate\": 500}")
            .when()
            .post("/api/uom-conversions")
            .then()
            .statusCode(400)
            .body("message", containsString("换算比例已存在"));
    }

    @Test
    @Order(3)
    void shouldGetConversionById() {
        Long id = getTestConversionId();
        given()
            .when()
            .get("/api/uom-conversions/" + id)
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.fromUomCode", equalTo(FROM_UOM_CODE))
            .body("data.toUomCode", equalTo(TO_UOM_CODE))
            .body("data.conversionRate", comparesEqualTo(1000.0f))
            .body("data.status", equalTo(1));
    }

    @Test
    @Order(4)
    void shouldUpdateConversionRate() {
        Long id = getTestConversionId();
        given()
            .contentType(ContentType.JSON)
            .body("{\"conversionRate\": 1000.5}")
            .when()
            .put("/api/uom-conversions/" + id)
            .then()
            .statusCode(200)
            .body("code", equalTo(200));

        // Verify update
        given()
            .when()
            .get("/api/uom-conversions/" + id)
            .then()
            .statusCode(200)
            .body("data.conversionRate", comparesEqualTo(1000.5f));
    }

    @Test
    @Order(5)
    void shouldListConversions() {
        given()
            .when()
            .get("/api/uom-conversions")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.records", not(empty()))
            .body("data.total", greaterThan(0));
    }

    @Test
    @Order(6)
    void shouldFilterByFromUom() {
        given()
            .queryParam("fromUom", FROM_UOM_CODE)
            .when()
            .get("/api/uom-conversions")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.records", not(empty()));
    }

    @Test
    @Order(7)
    void shouldReturnNotFoundForMissingId() {
        given()
            .when()
            .get("/api/uom-conversions/99999")
            .then()
            .statusCode(400)
            .body("message", containsString("换算比例不存在"));
    }

    @Test
    @Order(8)
    void shouldRejectCreateWithNonexistentUom() {
        Long toUomId = findUomIdByCode(TO_UOM_CODE, "克_测试");
        given()
            .contentType(ContentType.JSON)
            .body("{\"fromUomId\": 99999, \"toUomId\": " + toUomId + ", \"conversionRate\": 1}")
            .when()
            .post("/api/uom-conversions")
            .then()
            .statusCode(400)
            .body("message", containsString("原单位不存在"));
    }

    @Test
    @Order(9)
    void shouldDeleteConversion() {
        Long id = getTestConversionId();

        // Delete
        given()
            .when()
            .delete("/api/uom-conversions/" + id)
            .then()
            .statusCode(200)
            .body("code", equalTo(200));

        // Verify deleted
        given()
            .when()
            .get("/api/uom-conversions/" + id)
            .then()
            .statusCode(400);
    }
}
