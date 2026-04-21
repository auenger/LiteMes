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
 * Integration tests for EquipmentLedgerResource.
 * Validates CRUD operations, uniqueness validation, pagination, status management,
 * equipment model/factory reference validation, and redundant field population.
 */
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EquipmentLedgerResourceTest {

    private static final String TEST_EQUIPMENT_CODE = "EQ-001";
    private static final String TEST_EQUIPMENT_NAME = "1号钻孔机";

    /**
     * Helper: get the ID of the test equipment ledger by listing and filtering by code.
     */
    private Long getTestLedgerId() {
        return ((Number) given()
            .queryParam("equipmentCode", TEST_EQUIPMENT_CODE)
            .when()
            .get("/api/equipment-ledger")
            .then()
            .statusCode(200)
            .extract()
            .path("data.records[0].id")).longValue();
    }

    /**
     * Helper: get the ID of an equipment type by code.
     */
    private Long getEquipmentTypeId(String typeCode) {
        return ((Number) given()
            .queryParam("typeCode", typeCode)
            .when()
            .get("/api/equipment-types")
            .then()
            .statusCode(200)
            .extract()
            .path("data.records[0].id")).longValue();
    }

    /**
     * Helper: get the ID of an equipment model by code.
     */
    private Long getEquipmentModelId(String modelCode) {
        return ((Number) given()
            .queryParam("modelCode", modelCode)
            .when()
            .get("/api/equipment-models")
            .then()
            .statusCode(200)
            .extract()
            .path("data.records[0].id")).longValue();
    }

    /**
     * Helper: get factory ID by code.
     */
    private Long getFactoryId(String factoryCode) {
        return ((Number) given()
            .queryParam("factoryCode", factoryCode)
            .when()
            .get("/api/factories")
            .then()
            .statusCode(200)
            .extract()
            .path("data.records[0].id")).longValue();
    }

    @Test
    @Order(1)
    void shouldCreateEquipmentTypeAndModelForTest() {
        // Ensure equipment type exists
        given()
            .contentType(ContentType.JSON)
            .body("{\"typeCode\": \"DRILL\", \"typeName\": \"钻孔设备\"}")
            .when()
            .post("/api/equipment-types")
            .then()
            .statusCode(201);

        Long typeId = getEquipmentTypeId("DRILL");

        // Create equipment model
        given()
            .contentType(ContentType.JSON)
            .body("{\"modelCode\": \"CNC-500\", \"modelName\": \"CNC钻孔机\", \"equipmentTypeId\": " + typeId + "}")
            .when()
            .post("/api/equipment-models")
            .then()
            .statusCode(201);
    }

    @Test
    @Order(2)
    void shouldCreateEquipmentLedger() {
        Long modelId = getEquipmentModelId("CNC-500");
        Long factoryId = getFactoryId("F001");

        given()
            .contentType(ContentType.JSON)
            .body("{\"equipmentCode\": \"" + TEST_EQUIPMENT_CODE + "\", \"equipmentName\": \"" + TEST_EQUIPMENT_NAME + "\", " +
                  "\"equipmentModelId\": " + modelId + ", \"runningStatus\": \"RUNNING\", \"manageStatus\": \"IN_USE\", " +
                  "\"factoryId\": " + factoryId + ", \"commissioningDate\": \"2025-01-01\"}")
            .when()
            .post("/api/equipment-ledger")
            .then()
            .statusCode(201)
            .body("code", equalTo(200))
            .body("data", notNullValue());
    }

    @Test
    @Order(3)
    void shouldAutoFillRedundantFields() {
        Long id = getTestLedgerId();

        given()
            .when()
            .get("/api/equipment-ledger/" + id)
            .then()
            .statusCode(200)
            .body("data.modelCode", equalTo("CNC-500"))
            .body("data.modelName", equalTo("CNC钻孔机"))
            .body("data.typeCode", equalTo("DRILL"))
            .body("data.typeName", equalTo("钻孔设备"))
            .body("data.factoryCode", equalTo("F001"))
            .body("data.factoryName", equalTo("测试工厂"));
    }

    @Test
    @Order(4)
    void shouldRejectDuplicateCode() {
        Long modelId = getEquipmentModelId("CNC-500");
        Long factoryId = getFactoryId("F001");

        given()
            .contentType(ContentType.JSON)
            .body("{\"equipmentCode\": \"" + TEST_EQUIPMENT_CODE + "\", \"equipmentName\": \"重复编码\", " +
                  "\"equipmentModelId\": " + modelId + ", \"runningStatus\": \"RUNNING\", \"manageStatus\": \"IN_USE\", " +
                  "\"factoryId\": " + factoryId + ", \"commissioningDate\": \"2025-01-01\"}")
            .when()
            .post("/api/equipment-ledger")
            .then()
            .statusCode(400)
            .body("message", containsString("设备编码已存在"));
    }

    @Test
    @Order(5)
    void shouldGetEquipmentLedgerById() {
        Long id = getTestLedgerId();

        given()
            .when()
            .get("/api/equipment-ledger/" + id)
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.equipmentCode", equalTo(TEST_EQUIPMENT_CODE))
            .body("data.equipmentName", equalTo(TEST_EQUIPMENT_NAME))
            .body("data.runningStatus", equalTo("RUNNING"))
            .body("data.manageStatus", equalTo("IN_USE"))
            .body("data.status", equalTo(1));
    }

    @Test
    @Order(6)
    void shouldUpdateEquipmentLedger() {
        Long id = getTestLedgerId();
        Long modelId = getEquipmentModelId("CNC-500");
        Long factoryId = getFactoryId("F001");

        given()
            .contentType(ContentType.JSON)
            .body("{\"equipmentName\": \"1号钻孔机-Pro\", \"equipmentModelId\": " + modelId + ", " +
                  "\"runningStatus\": \"FAULT\", \"manageStatus\": \"MAINTENANCE\", " +
                  "\"factoryId\": " + factoryId + ", \"commissioningDate\": \"2025-01-01\"}")
            .when()
            .put("/api/equipment-ledger/" + id)
            .then()
            .statusCode(200)
            .body("code", equalTo(200));

        // Verify update - code unchanged, name and status changed
        given()
            .when()
            .get("/api/equipment-ledger/" + id)
            .then()
            .statusCode(200)
            .body("data.equipmentName", equalTo("1号钻孔机-Pro"))
            .body("data.runningStatus", equalTo("FAULT"))
            .body("data.manageStatus", equalTo("MAINTENANCE"))
            .body("data.equipmentCode", equalTo(TEST_EQUIPMENT_CODE)); // Code unchanged
    }

    @Test
    @Order(7)
    void shouldUpdateRedundantFieldsOnModelChange() {
        // Create another equipment type and model
        given()
            .contentType(ContentType.JSON)
            .body("{\"typeCode\": \"PRESS\", \"typeName\": \"压合设备\"}")
            .when()
            .post("/api/equipment-types")
            .then()
            .statusCode(201);

        Long pressTypeId = getEquipmentTypeId("PRESS");
        given()
            .contentType(ContentType.JSON)
            .body("{\"modelCode\": \"PRS-200\", \"modelName\": \"压合机\", \"equipmentTypeId\": " + pressTypeId + "}")
            .when()
            .post("/api/equipment-models")
            .then()
            .statusCode(201);

        Long id = getTestLedgerId();
        Long prsModelId = getEquipmentModelId("PRS-200");
        Long factoryId = getFactoryId("F001");

        // Change equipment model
        given()
            .contentType(ContentType.JSON)
            .body("{\"equipmentName\": \"1号钻孔机-Pro\", \"equipmentModelId\": " + prsModelId + ", " +
                  "\"runningStatus\": \"FAULT\", \"manageStatus\": \"MAINTENANCE\", " +
                  "\"factoryId\": " + factoryId + ", \"commissioningDate\": \"2025-01-01\"}")
            .when()
            .put("/api/equipment-ledger/" + id)
            .then()
            .statusCode(200);

        // Verify redundant fields updated
        given()
            .when()
            .get("/api/equipment-ledger/" + id)
            .then()
            .statusCode(200)
            .body("data.modelCode", equalTo("PRS-200"))
            .body("data.modelName", equalTo("压合机"))
            .body("data.typeCode", equalTo("PRESS"))
            .body("data.typeName", equalTo("压合设备"));

        // Change back to CNC-500 for remaining tests
        Long cncModelId = getEquipmentModelId("CNC-500");
        given()
            .contentType(ContentType.JSON)
            .body("{\"equipmentName\": \"1号钻孔机-Pro\", \"equipmentModelId\": " + cncModelId + ", " +
                  "\"runningStatus\": \"FAULT\", \"manageStatus\": \"MAINTENANCE\", " +
                  "\"factoryId\": " + factoryId + ", \"commissioningDate\": \"2025-01-01\"}")
            .when()
            .put("/api/equipment-ledger/" + id)
            .then()
            .statusCode(200);
    }

    @Test
    @Order(8)
    void shouldRejectCreateWithInvalidModelId() {
        Long factoryId = getFactoryId("F001");

        given()
            .contentType(ContentType.JSON)
            .body("{\"equipmentCode\": \"INVALID\", \"equipmentName\": \"无效型号\", " +
                  "\"equipmentModelId\": 99999, \"runningStatus\": \"RUNNING\", \"manageStatus\": \"IN_USE\", " +
                  "\"factoryId\": " + factoryId + ", \"commissioningDate\": \"2025-01-01\"}")
            .when()
            .post("/api/equipment-ledger")
            .then()
            .statusCode(400)
            .body("message", containsString("设备型号不存在"));
    }

    @Test
    @Order(9)
    void shouldRejectCreateWithInvalidFactoryId() {
        Long modelId = getEquipmentModelId("CNC-500");

        given()
            .contentType(ContentType.JSON)
            .body("{\"equipmentCode\": \"INVALID-FAC\", \"equipmentName\": \"无效工厂\", " +
                  "\"equipmentModelId\": " + modelId + ", \"runningStatus\": \"RUNNING\", \"manageStatus\": \"IN_USE\", " +
                  "\"factoryId\": 99999, \"commissioningDate\": \"2025-01-01\"}")
            .when()
            .post("/api/equipment-ledger")
            .then()
            .statusCode(400)
            .body("message", containsString("工厂不存在"));
    }

    @Test
    @Order(10)
    void shouldListEquipmentLedgers() {
        given()
            .when()
            .get("/api/equipment-ledger")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.records", not(empty()))
            .body("data.total", greaterThan(0));
    }

    @Test
    @Order(11)
    void shouldFilterByCode() {
        given()
            .queryParam("equipmentCode", TEST_EQUIPMENT_CODE)
            .when()
            .get("/api/equipment-ledger")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.records", not(empty()));
    }

    @Test
    @Order(12)
    void shouldFilterByName() {
        given()
            .queryParam("equipmentName", "钻孔")
            .when()
            .get("/api/equipment-ledger")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.records", not(empty()));
    }

    @Test
    @Order(13)
    void shouldFilterByRunningStatus() {
        given()
            .queryParam("runningStatus", "FAULT")
            .when()
            .get("/api/equipment-ledger")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.records", not(empty()));
    }

    @Test
    @Order(14)
    void shouldFilterByManageStatus() {
        given()
            .queryParam("manageStatus", "MAINTENANCE")
            .when()
            .get("/api/equipment-ledger")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.records", not(empty()));
    }

    @Test
    @Order(15)
    void shouldFilterByFactory() {
        Long factoryId = getFactoryId("F001");

        given()
            .queryParam("factoryId", factoryId)
            .when()
            .get("/api/equipment-ledger")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.records", not(empty()));
    }

    @Test
    @Order(16)
    void shouldToggleStatus() {
        Long id = getTestLedgerId();

        // Disable
        given()
            .contentType(ContentType.JSON)
            .when()
            .put("/api/equipment-ledger/" + id + "/status?status=0")
            .then()
            .statusCode(200)
            .body("code", equalTo(200));

        // Verify disabled
        given()
            .when()
            .get("/api/equipment-ledger/" + id)
            .then()
            .statusCode(200)
            .body("data.status", equalTo(0));

        // Re-enable
        given()
            .contentType(ContentType.JSON)
            .when()
            .put("/api/equipment-ledger/" + id + "/status?status=1")
            .then()
            .statusCode(200)
            .body("code", equalTo(200));
    }

    @Test
    @Order(17)
    void shouldRejectStatusUnchanged() {
        Long id = getTestLedgerId();

        given()
            .contentType(ContentType.JSON)
            .when()
            .put("/api/equipment-ledger/" + id + "/status?status=1")
            .then()
            .statusCode(400)
            .body("message", containsString("状态未发生变化"));
    }

    @Test
    @Order(18)
    void shouldReturnNotFoundForMissingId() {
        given()
            .when()
            .get("/api/equipment-ledger/99999")
            .then()
            .statusCode(400)
            .body("message", containsString("设备台账不存在"));
    }

    @Test
    @Order(19)
    void shouldRejectCreateWithBlankCode() {
        Long modelId = getEquipmentModelId("CNC-500");
        Long factoryId = getFactoryId("F001");

        given()
            .contentType(ContentType.JSON)
            .body("{\"equipmentCode\": \"\", \"equipmentName\": \"空编码\", " +
                  "\"equipmentModelId\": " + modelId + ", \"runningStatus\": \"RUNNING\", \"manageStatus\": \"IN_USE\", " +
                  "\"factoryId\": " + factoryId + ", \"commissioningDate\": \"2025-01-01\"}")
            .when()
            .post("/api/equipment-ledger")
            .then()
            .statusCode(422);
    }

    @Test
    @Order(20)
    void shouldDeleteEquipmentLedger() {
        Long modelId = getEquipmentModelId("CNC-500");
        Long factoryId = getFactoryId("F001");

        // Create a new ledger to delete
        given()
            .contentType(ContentType.JSON)
            .body("{\"equipmentCode\": \"DELETE-TEST\", \"equipmentName\": \"待删除设备\", " +
                  "\"equipmentModelId\": " + modelId + ", \"runningStatus\": \"SHUTDOWN\", \"manageStatus\": \"IDLE\", " +
                  "\"factoryId\": " + factoryId + ", \"commissioningDate\": \"2025-06-01\"}")
            .when()
            .post("/api/equipment-ledger")
            .then()
            .statusCode(201);

        // Find it
        Long id = ((Number) given()
            .queryParam("equipmentCode", "DELETE-TEST")
            .when()
            .get("/api/equipment-ledger")
            .then()
            .statusCode(200)
            .extract()
            .path("data.records[0].id")).longValue();

        // Delete it
        given()
            .when()
            .delete("/api/equipment-ledger/" + id)
            .then()
            .statusCode(200)
            .body("code", equalTo(200));

        // Verify deleted
        given()
            .when()
            .get("/api/equipment-ledger/" + id)
            .then()
            .statusCode(400)
            .body("message", containsString("设备台账不存在"));
    }
}
