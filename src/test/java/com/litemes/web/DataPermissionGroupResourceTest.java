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
 * Integration tests for DataPermissionGroupResource.
 * Validates CRUD operations, uniqueness validation, pagination, and association management.
 */
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DataPermissionGroupResourceTest {

    private static Long createdGroupId;

    @Test
    @Order(1)
    void shouldCreateGroup() {
        Object id = given()
            .contentType(ContentType.JSON)
            .body("{\"groupName\": \"冲压车间权限组\", \"remark\": \"冲压车间相关权限\"}")
            .when()
            .post("/api/data-permission-groups")
            .then()
            .statusCode(201)
            .body("code", equalTo(200))
            .body("data", notNullValue())
            .extract()
            .path("data");
        createdGroupId = ((Number) id).longValue();
    }

    @Test
    @Order(2)
    void shouldRejectDuplicateName() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"groupName\": \"冲压车间权限组\", \"remark\": \"重复名称\"}")
            .when()
            .post("/api/data-permission-groups")
            .then()
            .statusCode(400)
            .body("message", containsString("权限组名称已存在"));
    }

    @Test
    @Order(3)
    void shouldGetGroupById() {
        given()
            .when()
            .get("/api/data-permission-groups/" + createdGroupId)
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.groupName", equalTo("冲压车间权限组"))
            .body("data.remark", equalTo("冲压车间相关权限"))
            .body("data.factoryCount", equalTo(0))
            .body("data.workCenterCount", equalTo(0))
            .body("data.processCount", equalTo(0));
    }

    @Test
    @Order(4)
    void shouldUpdateGroup() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"groupName\": \"冲压一车间权限组\", \"remark\": \"更新后的备注\"}")
            .when()
            .put("/api/data-permission-groups/" + createdGroupId)
            .then()
            .statusCode(200)
            .body("code", equalTo(200));

        // Verify update
        given()
            .when()
            .get("/api/data-permission-groups/" + createdGroupId)
            .then()
            .statusCode(200)
            .body("data.groupName", equalTo("冲压一车间权限组"))
            .body("data.remark", equalTo("更新后的备注"));
    }

    @Test
    @Order(5)
    void shouldListGroups() {
        given()
            .when()
            .get("/api/data-permission-groups")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.records", not(empty()))
            .body("data.total", greaterThan(0));
    }

    @Test
    @Order(6)
    void shouldFilterByName() {
        given()
            .queryParam("groupName", "冲压")
            .when()
            .get("/api/data-permission-groups")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.records", not(empty()));
    }

    @Test
    @Order(7)
    void shouldSaveFactoryAssociations() {
        // Save factory associations (factory with id=1 assumed to exist from seed data)
        given()
            .contentType(ContentType.JSON)
            .body("{\"ids\": [1, 2]}")
            .when()
            .post("/api/data-permission-groups/" + createdGroupId + "/factories")
            .then()
            .statusCode(200)
            .body("code", equalTo(200));

        // Verify associations
        given()
            .when()
            .get("/api/data-permission-groups/" + createdGroupId + "/factories")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.size()", equalTo(2));

        // Verify count in group detail
        given()
            .when()
            .get("/api/data-permission-groups/" + createdGroupId)
            .then()
            .statusCode(200)
            .body("data.factoryCount", equalTo(2));
    }

    @Test
    @Order(8)
    void shouldReplaceFactoryAssociations() {
        // Replace with only factory 1
        given()
            .contentType(ContentType.JSON)
            .body("{\"ids\": [1]}")
            .when()
            .post("/api/data-permission-groups/" + createdGroupId + "/factories")
            .then()
            .statusCode(200)
            .body("code", equalTo(200));

        // Verify only 1 factory now
        given()
            .when()
            .get("/api/data-permission-groups/" + createdGroupId + "/factories")
            .then()
            .statusCode(200)
            .body("data.size()", equalTo(1));
    }

    @Test
    @Order(9)
    void shouldSaveWorkCenterAssociations() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"ids\": [1]}")
            .when()
            .post("/api/data-permission-groups/" + createdGroupId + "/work-centers")
            .then()
            .statusCode(200)
            .body("code", equalTo(200));

        given()
            .when()
            .get("/api/data-permission-groups/" + createdGroupId + "/work-centers")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.size()", equalTo(1));
    }

    @Test
    @Order(10)
    void shouldSaveProcessAssociations() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"ids\": [1]}")
            .when()
            .post("/api/data-permission-groups/" + createdGroupId + "/processes")
            .then()
            .statusCode(200)
            .body("code", equalTo(200));

        given()
            .when()
            .get("/api/data-permission-groups/" + createdGroupId + "/processes")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.size()", equalTo(1));
    }

    @Test
    @Order(11)
    void shouldRejectCreateWithBlankName() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"groupName\": \"\", \"remark\": \"空名称\"}")
            .when()
            .post("/api/data-permission-groups")
            .then()
            .statusCode(422);
    }

    @Test
    @Order(12)
    void shouldReturnNotFoundForMissingId() {
        given()
            .when()
            .get("/api/data-permission-groups/99999")
            .then()
            .statusCode(400)
            .body("message", containsString("数据权限组不存在"));
    }

    @Test
    @Order(13)
    void shouldDeleteGroup() {
        // Create a group to delete
        Object id = given()
            .contentType(ContentType.JSON)
            .body("{\"groupName\": \"待删除权限组\", \"remark\": \"测试删除\"}")
            .when()
            .post("/api/data-permission-groups")
            .then()
            .statusCode(201)
            .extract()
            .path("data");
        Long deleteId = ((Number) id).longValue();

        // Delete
        given()
            .when()
            .delete("/api/data-permission-groups/" + deleteId)
            .then()
            .statusCode(200)
            .body("code", equalTo(200));

        // Verify deleted
        given()
            .when()
            .get("/api/data-permission-groups/" + deleteId)
            .then()
            .statusCode(400);
    }
}
