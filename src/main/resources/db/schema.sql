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
