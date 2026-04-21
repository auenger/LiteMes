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

-- 班制表
CREATE TABLE IF NOT EXISTS shift_schedule (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    shift_code    VARCHAR(50)  NOT NULL,
    name          VARCHAR(50)  NOT NULL,
    is_default    TINYINT      NOT NULL DEFAULT 0 COMMENT '1=默认,0=非默认',
    status        TINYINT      NOT NULL DEFAULT 1 COMMENT '1=启用,0=禁用',
    deleted       TINYINT      NOT NULL DEFAULT 0,
    created_by    VARCHAR(64)  NULL,
    created_at    DATETIME     NULL,
    updated_by    VARCHAR(64)  NULL,
    updated_at    DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_shift_schedule_code (shift_code, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班制';

-- 班次表
CREATE TABLE IF NOT EXISTS shift (
    id                 BIGINT       NOT NULL AUTO_INCREMENT,
    shift_schedule_id  BIGINT       NOT NULL,
    shift_code         VARCHAR(50)  NOT NULL,
    name               VARCHAR(50)  NOT NULL,
    start_time         TIME         NOT NULL,
    end_time           TIME         NOT NULL,
    cross_day          TINYINT      NOT NULL DEFAULT 0 COMMENT '1=跨天,0=不跨天',
    status             TINYINT      NOT NULL DEFAULT 1 COMMENT '1=启用,0=禁用',
    deleted            TINYINT      NOT NULL DEFAULT 0,
    created_by         VARCHAR(64)  NULL,
    created_at         DATETIME     NULL,
    updated_by         VARCHAR(64)  NULL,
    updated_at         DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_shift_code (shift_code, deleted),
    INDEX idx_shift_schedule (shift_schedule_id),
    CONSTRAINT fk_shift_schedule FOREIGN KEY (shift_schedule_id) REFERENCES shift_schedule(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班次';

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
