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
 * Integration tests for MaterialCategoryResource.
 * Validates CRUD operations, uniqueness validation, pagination, tree structure, and status management.
 */
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MaterialCategoryResourceTest {

    private static Long createdCategoryId;
    private static Long childCategoryId;

    @Test
    @Order(1)
    void shouldCreateTopLevelCategory() {
        Object id = given()
            .contentType(ContentType.JSON)
            .body("{\"categoryCode\": \"CAT-RAW\", \"categoryName\": \"原材料\", \"isQualityCategory\": false}")
            .when()
            .post("/api/material-categories")
            .then()
            .statusCode(201)
            .body("code", equalTo(200))
            .body("data", notNullValue())
            .extract()
            .path("data");
        createdCategoryId = ((Number) id).longValue();
    }

    @Test
    @Order(2)
    void shouldCreateChildCategory() {
        Object id = given()
            .contentType(ContentType.JSON)
            .body("{\"categoryCode\": \"CAT-PCB\", \"categoryName\": \"PCB板材\", \"isQualityCategory\": true, \"parentId\": " + createdCategoryId + "}")
            .when()
            .post("/api/material-categories")
            .then()
            .statusCode(201)
            .body("code", equalTo(200))
            .body("data", notNullValue())
            .extract()
            .path("data");
        childCategoryId = ((Number) id).longValue();
    }

    @Test
    @Order(3)
    void shouldRejectDuplicateCode() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"categoryCode\": \"CAT-RAW\", \"categoryName\": \"重复编码\"}")
            .when()
            .post("/api/material-categories")
            .then()
            .statusCode(400)
            .body("message", containsString("物料分类编码已存在"));
    }

    @Test
    @Order(4)
    void shouldRejectDuplicateName() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"categoryCode\": \"CAT-NEW\", \"categoryName\": \"原材料\"}")
            .when()
            .post("/api/material-categories")
            .then()
            .statusCode(400)
            .body("message", containsString("物料分类名称已存在"));
    }

    @Test
    @Order(5)
    void shouldGetCategoryById() {
        given()
            .when()
            .get("/api/material-categories/" + createdCategoryId)
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.categoryCode", equalTo("CAT-RAW"))
            .body("data.categoryName", equalTo("原材料"))
            .body("data.isQualityCategory", equalTo(false))
            .body("data.status", equalTo(1));
    }

    @Test
    @Order(6)
    void shouldGetChildCategoryWithParentName() {
        given()
            .when()
            .get("/api/material-categories/" + childCategoryId)
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.categoryCode", equalTo("CAT-PCB"))
            .body("data.parentName", equalTo("原材料"))
            .body("data.isQualityCategory", equalTo(true));
    }

    @Test
    @Order(7)
    void shouldUpdateCategory() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"categoryName\": \"原材料类\", \"isQualityCategory\": true}")
            .when()
            .put("/api/material-categories/" + createdCategoryId)
            .then()
            .statusCode(200)
            .body("code", equalTo(200));

        // Verify update
        given()
            .when()
            .get("/api/material-categories/" + createdCategoryId)
            .then()
            .statusCode(200)
            .body("data.categoryName", equalTo("原材料类"))
            .body("data.isQualityCategory", equalTo(true))
            .body("data.categoryCode", equalTo("CAT-RAW")); // Code unchanged
    }

    @Test
    @Order(8)
    void shouldListCategories() {
        given()
            .when()
            .get("/api/material-categories")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.records", not(empty()))
            .body("data.total", greaterThan(0));
    }

    @Test
    @Order(9)
    void shouldFilterByName() {
        given()
            .queryParam("categoryName", "原材料")
            .when()
            .get("/api/material-categories")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.records", not(empty()));
    }

    @Test
    @Order(10)
    void shouldGetCategoryTree() {
        given()
            .when()
            .get("/api/material-categories/tree")
            .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data", not(empty()))
            .body("data[0].categoryCode", equalTo("CAT-RAW"))
            .body("data[0].children", not(empty()))
            .body("data[0].children[0].categoryCode", equalTo("CAT-PCB"));
    }

    @Test
    @Order(11)
    void shouldToggleStatus() {
        // Disable
        given()
            .contentType(ContentType.JSON)
            .when()
            .put("/api/material-categories/" + createdCategoryId + "/status?status=0")
            .then()
            .statusCode(200)
            .body("code", equalTo(200));

        // Verify disabled
        given()
            .when()
            .get("/api/material-categories/" + createdCategoryId)
            .then()
            .statusCode(200)
            .body("data.status", equalTo(0));

        // Re-enable
        given()
            .contentType(ContentType.JSON)
            .when()
            .put("/api/material-categories/" + createdCategoryId + "/status?status=1")
            .then()
            .statusCode(200)
            .body("code", equalTo(200));
    }

    @Test
    @Order(12)
    void shouldRejectStatusUnchanged() {
        given()
            .contentType(ContentType.JSON)
            .when()
            .put("/api/material-categories/" + createdCategoryId + "/status?status=1")
            .then()
            .statusCode(400)
            .body("message", containsString("状态未发生变化"));
    }

    @Test
    @Order(13)
    void shouldRejectDeleteWithChildren() {
        given()
            .when()
            .delete("/api/material-categories/" + createdCategoryId)
            .then()
            .statusCode(400)
            .body("message", containsString("存在子分类"));
    }

    @Test
    @Order(14)
    void shouldRejectSelfParent() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"parentId\": " + childCategoryId + "}")
            .when()
            .put("/api/material-categories/" + childCategoryId)
            .then()
            .statusCode(400)
            .body("message", containsString("不能将自身设为上级分类"));
    }

    @Test
    @Order(15)
    void shouldReturnNotFoundForMissingId() {
        given()
            .when()
            .get("/api/material-categories/99999")
            .then()
            .statusCode(400)
            .body("message", containsString("物料分类不存在"));
    }

    @Test
    @Order(16)
    void shouldDeleteChildCategory() {
        given()
            .when()
            .delete("/api/material-categories/" + childCategoryId)
            .then()
            .statusCode(200)
            .body("code", equalTo(200));

        // Verify deleted
        given()
            .when()
            .get("/api/material-categories/" + childCategoryId)
            .then()
            .statusCode(400);
    }

    @Test
    @Order(17)
    void shouldRejectCreateWithBlankCode() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"categoryCode\": \"\", \"categoryName\": \"空编码\"}")
            .when()
            .post("/api/material-categories")
            .then()
            .statusCode(422);
    }

    @Test
    @Order(18)
    void shouldFilterByStatus() {
        given()
            .queryParam("status", 1)
            .when()
            .get("/api/material-categories")
            .then()
            .statusCode(200)
            .body("code", equalTo(200));
    }
}
