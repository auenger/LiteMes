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
 * Integration tests for EquipmentModelResource.
 * Validates CRUD operations, uniqueness validation, pagination, status management,
 * equipment type reference validation, and redundant field population.
 * Each test is self-contained and looks up the created equipment model by code.
 */
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EquipmentModelResourceTest {

    private static final String TEST_MODEL_CODE = "CNC-500";
    private static final String TEST_MODEL_NAME = "CNC钻孔机";

    /**
     * Helper: get the ID of the test equipment model by listing and filtering by code.
     */
    private Long getTestModelId() {
        return ((Number) given()
            .queryParam("modelCode", TEST_MODEL_CODE)
            .when()
            .get("/api/equipment-models")
            .then()
            .statusCode(200)
            .extract()
            .path("data.records[0].id")).longValue();
    }

    /**
     * Helper: get the ID of an equipment type by listing and filtering by code.
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

    @Test
    @Order(1)
    void shouldCreateEquipmentTypeForTest() {
        // Ensure an equipment type exists for the model FK reference
        given()
            .contentType(ContentType.JSON)
            .body("{\"typeCode\": \"DRILL\", \"typeName\": \"钻孔设备\"}")
            .when()
            .post("/api/equipment-types")
            .then()
            .statusCode(201);
    }

    @Test
    @Order(2)
    void shouldCreateEquipmentModel() {
        Long typeId = getEquipmentTypeId("DRILL");

        given()
            .contentType(ContentType.JSON)
            .body("{\"modelCode\": \"" + TEST_MODEL_CODE + "\", \"modelName\": \"" + TEST_MODEL_NAME + "\", \"equipmentTypeId\": " + typeId + "}")
            .when()
            .post("/api/equipment-models")
            .then()
            .statusCode(201)
            .body("code", equalTo(200))
            .body("data", notNullValue());
    }

    @Test
    @Order(3)
    void shouldAutoFillRedundantTypeFields() {
        Long id = getTestModelId();

        given()
            .when()
            .get("/api/equipment-models/" + id)
            .then()
            .statusCode(200)
            .body("data.typeCode", equalTo("DRILL"))
            .body("data.typeName", equalTo("钻孔设备"));
    }

    @Test
    @Order(4)
    void shouldRejectDuplicateCode() {
        Long typeId = getEquipmentTypeId("DRILL");

        given()
            .contentType(ContentType.JSON)
            .body("{\"modelCode\": \"" + TEST_MODEL_CODE + "\", \"modelName\": \"重复编码\", \"equipmentTypeId\": " + typeId + "}")
            .when()
            .post("/api/equipment-models")
            .then()
            .statusCode(400)
            .body("message", containsString("设备型号编码已存在"));
    }

    @Test
    @Order(5)
    void shouldGetEquipmentModelById() {
        Long id = getTestModelId();

        given()
            .when()
            .get("/api/equipment-models/" + id)
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.modelCode", equalTo(TEST_MODEL_CODE))
            .body("data.modelName", equalTo(TEST_MODEL_NAME))
            .body("data.status", equalTo(1))
            .body("data.equipmentTypeId", notNullValue());
    }

    @Test
    @Order(6)
    void shouldUpdateEquipmentModel() {
        Long id = getTestModelId();
        Long typeId = getEquipmentTypeId("DRILL");

        given()
            .contentType(ContentType.JSON)
            .body("{\"modelName\": \"CNC钻孔机-Pro\", \"equipmentTypeId\": " + typeId + "}")
            .when()
            .put("/api/equipment-models/" + id)
            .then()
            .statusCode(200)
            .body("code", equalTo(200));

        // Verify update - code unchanged, name changed
        given()
            .when()
            .get("/api/equipment-models/" + id)
            .then()
            .statusCode(200)
            .body("data.modelName", equalTo("CNC钻孔机-Pro"))
            .body("data.modelCode", equalTo(TEST_MODEL_CODE)); // Code unchanged
    }

    @Test
    @Order(7)
    void shouldUpdateRedundantFieldsOnTypeChange() {
        // Create another equipment type
        given()
            .contentType(ContentType.JSON)
            .body("{\"typeCode\": \"PRESS\", \"typeName\": \"压合设备\"}")
            .when()
            .post("/api/equipment-types")
            .then()
            .statusCode(201);

        Long id = getTestModelId();
        Long pressTypeId = getEquipmentTypeId("PRESS");

        // Change equipment type
        given()
            .contentType(ContentType.JSON)
            .body("{\"modelName\": \"CNC钻孔机-Pro\", \"equipmentTypeId\": " + pressTypeId + "}")
            .when()
            .put("/api/equipment-models/" + id)
            .then()
            .statusCode(200);

        // Verify redundant fields updated
        given()
            .when()
            .get("/api/equipment-models/" + id)
            .then()
            .statusCode(200)
            .body("data.typeCode", equalTo("PRESS"))
            .body("data.typeName", equalTo("压合设备"));

        // Change back to DRILL type for remaining tests
        Long drillTypeId = getEquipmentTypeId("DRILL");
        given()
            .contentType(ContentType.JSON)
            .body("{\"modelName\": \"CNC钻孔机-Pro\", \"equipmentTypeId\": " + drillTypeId + "}")
            .when()
            .put("/api/equipment-models/" + id)
            .then()
            .statusCode(200);
    }

    @Test
    @Order(8)
    void shouldRejectCreateWithInvalidTypeId() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"modelCode\": \"INVALID-TYPE\", \"modelName\": \"无效类型\", \"equipmentTypeId\": 99999}")
            .when()
            .post("/api/equipment-models")
            .then()
            .statusCode(400)
            .body("message", containsString("设备类型不存在"));
    }

    @Test
    @Order(9)
    void shouldListEquipmentModels() {
        given()
            .when()
            .get("/api/equipment-models")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.records", not(empty()))
            .body("data.total", greaterThan(0));
    }

    @Test
    @Order(10)
    void shouldFilterByCode() {
        given()
            .queryParam("modelCode", TEST_MODEL_CODE)
            .when()
            .get("/api/equipment-models")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.records", not(empty()));
    }

    @Test
    @Order(11)
    void shouldFilterByName() {
        given()
            .queryParam("modelName", "钻孔")
            .when()
            .get("/api/equipment-models")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.records", not(empty()));
    }

    @Test
    @Order(12)
    void shouldFilterByEquipmentType() {
        Long typeId = getEquipmentTypeId("DRILL");

        given()
            .queryParam("equipmentTypeId", typeId)
            .when()
            .get("/api/equipment-models")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.records", not(empty()));
    }

    @Test
    @Order(13)
    void shouldFilterByStatus() {
        given()
            .queryParam("status", 1)
            .when()
            .get("/api/equipment-models")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.records", not(empty()));
    }

    @Test
    @Order(14)
    void shouldToggleStatus() {
        Long id = getTestModelId();

        // Disable
        given()
            .contentType(ContentType.JSON)
            .when()
            .put("/api/equipment-models/" + id + "/status?status=0")
            .then()
            .statusCode(200)
            .body("code", equalTo(200));

        // Verify disabled
        given()
            .when()
            .get("/api/equipment-models/" + id)
            .then()
            .statusCode(200)
            .body("data.status", equalTo(0));

        // Re-enable
        given()
            .contentType(ContentType.JSON)
            .when()
            .put("/api/equipment-models/" + id + "/status?status=1")
            .then()
            .statusCode(200)
            .body("code", equalTo(200));
    }

    @Test
    @Order(15)
    void shouldRejectStatusUnchanged() {
        Long id = getTestModelId();

        given()
            .contentType(ContentType.JSON)
            .when()
            .put("/api/equipment-models/" + id + "/status?status=1")
            .then()
            .statusCode(400)
            .body("message", containsString("状态未发生变化"));
    }

    @Test
    @Order(16)
    void shouldReturnNotFoundForMissingId() {
        given()
            .when()
            .get("/api/equipment-models/99999")
            .then()
            .statusCode(400)
            .body("message", containsString("设备型号不存在"));
    }

    @Test
    @Order(17)
    void shouldRejectCreateWithBlankCode() {
        Long typeId = getEquipmentTypeId("DRILL");

        given()
            .contentType(ContentType.JSON)
            .body("{\"modelCode\": \"\", \"modelName\": \"空编码\", \"equipmentTypeId\": " + typeId + "}")
            .when()
            .post("/api/equipment-models")
            .then()
            .statusCode(422);
    }

    @Test
    @Order(18)
    void shouldRejectCreateWithoutName() {
        Long typeId = getEquipmentTypeId("DRILL");

        given()
            .contentType(ContentType.JSON)
            .body("{\"modelCode\": \"NAMETEST\", \"equipmentTypeId\": " + typeId + "}")
            .when()
            .post("/api/equipment-models")
            .then()
            .statusCode(422);
    }

    @Test
    @Order(19)
    void shouldRejectCreateWithoutTypeId() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"modelCode\": \"NOTYPE\", \"modelName\": \"无类型\"}")
            .when()
            .post("/api/equipment-models")
            .then()
            .statusCode(422);
    }

    @Test
    @Order(20)
    void shouldDeleteEquipmentModel() {
        Long typeId = getEquipmentTypeId("DRILL");

        // Create a new model to delete
        given()
            .contentType(ContentType.JSON)
            .body("{\"modelCode\": \"DELETE_TEST\", \"modelName\": \"待删除型号\", \"equipmentTypeId\": " + typeId + "}")
            .when()
            .post("/api/equipment-models")
            .then()
            .statusCode(201);

        // Find it
        Long id = ((Number) given()
            .queryParam("modelCode", "DELETE_TEST")
            .when()
            .get("/api/equipment-models")
            .then()
            .statusCode(200)
            .extract()
            .path("data.records[0].id")).longValue();

        // Delete it
        given()
            .when()
            .delete("/api/equipment-models/" + id)
            .then()
            .statusCode(200)
            .body("code", equalTo(200));

        // Verify deleted - should return 400 NOT_FOUND
        given()
            .when()
            .get("/api/equipment-models/" + id)
            .then()
            .statusCode(400)
            .body("message", containsString("设备型号不存在"));
    }
}
