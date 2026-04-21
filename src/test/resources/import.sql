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
