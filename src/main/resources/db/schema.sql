-- LiteMes Schema Initialization
-- Example entity table for skeleton validation
-- Column names use Java camelCase convention to match MyBatis-Plus default mapping

CREATE TABLE IF NOT EXISTS example_entity (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    createdBy   VARCHAR(64),
    createdAt   DATETIME,
    updatedBy   VARCHAR(64),
    updatedAt   DATETIME,
    deleted     TINYINT(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Company table
CREATE TABLE IF NOT EXISTS company (
    id            BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    companyCode   VARCHAR(50)  NOT NULL,
    name          VARCHAR(50)  NOT NULL,
    shortCode     VARCHAR(50)  NULL,
    status        TINYINT      NOT NULL DEFAULT 1 COMMENT '1=enabled,0=disabled',
    deleted       TINYINT(1)   NOT NULL DEFAULT 0,
    createdBy     VARCHAR(64)  NULL,
    createdAt     DATETIME     NULL,
    updatedBy     VARCHAR(64)  NULL,
    updatedAt     DATETIME     NULL,
    UNIQUE KEY uk_company_code (companyCode, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公司';
