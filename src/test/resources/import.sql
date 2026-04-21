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
