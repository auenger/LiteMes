-- H2 test schema initialization
-- Column names match Java field names (MyBatis-Plus default mapping)
CREATE TABLE IF NOT EXISTS example_entity (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    createdBy   VARCHAR(64),
    createdAt   TIMESTAMP,
    updatedBy   VARCHAR(64),
    updatedAt   TIMESTAMP,
    deleted     TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS company (
    id            BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    companyCode   VARCHAR(50)  NOT NULL,
    name          VARCHAR(50)  NOT NULL,
    shortCode     VARCHAR(50)  NULL,
    status        TINYINT      NOT NULL DEFAULT 1,
    deleted       TINYINT      NOT NULL DEFAULT 0,
    createdBy     VARCHAR(64)  NULL,
    createdAt     TIMESTAMP    NULL,
    updatedBy     VARCHAR(64)  NULL,
    updatedAt     TIMESTAMP    NULL
);

CREATE TABLE IF NOT EXISTS factory (
    id            BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    factoryCode   VARCHAR(50)  NOT NULL,
    name          VARCHAR(50)  NOT NULL,
    shortName     VARCHAR(50)  NULL,
    companyId     BIGINT       NOT NULL,
    status        TINYINT      NOT NULL DEFAULT 1,
    deleted       TINYINT      NOT NULL DEFAULT 0,
    createdBy     VARCHAR(64)  NULL,
    createdAt     TIMESTAMP    NULL,
    updatedBy     VARCHAR(64)  NULL,
    updatedAt     TIMESTAMP    NULL
);

CREATE TABLE IF NOT EXISTS work_center (
    id              BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    workCenterCode  VARCHAR(50)  NOT NULL,
    name            VARCHAR(50)  NOT NULL,
    factoryId       BIGINT       NOT NULL,
    status          TINYINT      NOT NULL DEFAULT 1,
    deleted         TINYINT      NOT NULL DEFAULT 0,
    createdBy       VARCHAR(64)  NULL,
    createdAt       TIMESTAMP    NULL,
    updatedBy       VARCHAR(64)  NULL,
    updatedAt       TIMESTAMP    NULL
);

CREATE TABLE IF NOT EXISTS process (
    id              BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    processCode     VARCHAR(50)  NOT NULL,
    name            VARCHAR(50)  NOT NULL,
    workCenterId    BIGINT       NOT NULL,
    status          TINYINT      NOT NULL DEFAULT 1,
    deleted         TINYINT      NOT NULL DEFAULT 0,
    createdBy       VARCHAR(64)  NULL,
    createdAt       TIMESTAMP    NULL,
    updatedBy       VARCHAR(64)  NULL,
    updatedAt       TIMESTAMP    NULL
);

CREATE TABLE IF NOT EXISTS department (
    id              BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    departmentCode  VARCHAR(50)  NOT NULL,
    name            VARCHAR(50)  NOT NULL,
    factoryId       BIGINT       NOT NULL,
    parentId        BIGINT       NULL,
    sortOrder       INT          NOT NULL DEFAULT 0,
    status          TINYINT      NOT NULL DEFAULT 1,
    deleted         TINYINT      NOT NULL DEFAULT 0,
    createdBy       VARCHAR(64)  NULL,
    createdAt       TIMESTAMP    NULL,
    updatedBy       VARCHAR(64)  NULL,
    updatedAt       TIMESTAMP    NULL
);

CREATE TABLE IF NOT EXISTS user (
    id            BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL,
    realName      VARCHAR(50)  NOT NULL,
    status        TINYINT      NOT NULL DEFAULT 1,
    createdBy     VARCHAR(64)  NULL,
    createdAt     TIMESTAMP    NULL,
    updatedBy     VARCHAR(64)  NULL,
    updatedAt     TIMESTAMP    NULL
);

CREATE TABLE IF NOT EXISTS department_user (
    id              BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    departmentId    BIGINT       NOT NULL,
    userId          BIGINT       NOT NULL,
    createdBy       VARCHAR(64)  NULL,
    createdAt       TIMESTAMP    NULL,
    updatedBy       VARCHAR(64)  NULL,
    updatedAt       TIMESTAMP    NULL
);

CREATE TABLE IF NOT EXISTS shift_schedule (
    id            BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    shiftCode     VARCHAR(50)  NOT NULL,
    name          VARCHAR(50)  NOT NULL,
    isDefault     TINYINT      NOT NULL DEFAULT 0,
    status        TINYINT      NOT NULL DEFAULT 1,
    deleted       TINYINT      NOT NULL DEFAULT 0,
    createdBy     VARCHAR(64)  NULL,
    createdAt     TIMESTAMP    NULL,
    updatedBy     VARCHAR(64)  NULL,
    updatedAt     TIMESTAMP    NULL
);

CREATE TABLE IF NOT EXISTS shift (
    id                 BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    shiftScheduleId    BIGINT       NOT NULL,
    shiftCode          VARCHAR(50)  NOT NULL,
    name               VARCHAR(50)  NOT NULL,
    startTime          VARCHAR(20)  NOT NULL,
    endTime            VARCHAR(20)  NOT NULL,
    crossDay           TINYINT      NOT NULL DEFAULT 0,
    status             TINYINT      NOT NULL DEFAULT 1,
    deleted            TINYINT      NOT NULL DEFAULT 0,
    createdBy          VARCHAR(64)  NULL,
    createdAt          TIMESTAMP    NULL,
    updatedBy          VARCHAR(64)  NULL,
    updatedAt          TIMESTAMP    NULL
);

CREATE TABLE IF NOT EXISTS audit_log (
    id              BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    tableName       VARCHAR(64)  NOT NULL,
    recordId        BIGINT       NOT NULL,
    action          VARCHAR(16)  NOT NULL,
    oldValue        TEXT         NULL,
    newValue        TEXT         NULL,
    changedFields   VARCHAR(512) NULL,
    operatorId      BIGINT       NULL,
    operatorName    VARCHAR(64)  NULL,
    createdAt       TIMESTAMP    NULL,
    createdBy       VARCHAR(64)  NULL,
    updatedBy       VARCHAR(64)  NULL,
    updatedAt       TIMESTAMP    NULL
);

CREATE TABLE IF NOT EXISTS material_category (
    id                  BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    categoryCode        VARCHAR(50)  NOT NULL,
    categoryName        VARCHAR(50)  NOT NULL,
    isQualityCategory   TINYINT      NOT NULL DEFAULT 0,
    parentId            BIGINT       NULL,
    status              TINYINT      NOT NULL DEFAULT 1,
    deleted             TINYINT      NOT NULL DEFAULT 0,
    createdBy           VARCHAR(64)  NULL,
    createdAt           TIMESTAMP    NULL,
    updatedBy           VARCHAR(64)  NULL,
    updatedAt           TIMESTAMP    NULL
);

-- Seed data for testing
INSERT INTO company (id, companyCode, name, shortCode, status, deleted) VALUES (1, 'CO001', '测试公司', 'TC', 1, 0);
INSERT INTO factory (id, factoryCode, name, shortName, companyId, status, deleted) VALUES (1, 'F001', '测试工厂', 'TF', 1, 1, 0);
INSERT INTO work_center (id, workCenterCode, name, factoryId, status, deleted) VALUES (1, 'WC001', '测试工作中心', 1, 1, 0);
