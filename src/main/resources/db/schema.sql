-- LiteMes Schema Initialization
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
    shiftCode     VARCHAR(50)  NOT NULL,
    name          VARCHAR(50)  NOT NULL,
    isDefault     TINYINT      NOT NULL DEFAULT 0 COMMENT '1=默认,0=非默认',
    status        TINYINT      NOT NULL DEFAULT 1 COMMENT '1=启用,0=禁用',
    deleted       TINYINT      NOT NULL DEFAULT 0,
    createdBy     VARCHAR(64)  NULL,
    createdAt     DATETIME     NULL,
    updatedBy     VARCHAR(64)  NULL,
    updatedAt     DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_shift_schedule_code (shiftCode, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班制';

-- 班次表
CREATE TABLE IF NOT EXISTS shift (
    id                 BIGINT       NOT NULL AUTO_INCREMENT,
    shiftScheduleId    BIGINT       NOT NULL,
    shiftCode          VARCHAR(50)  NOT NULL,
    name               VARCHAR(50)  NOT NULL,
    startTime          TIME         NOT NULL,
    endTime            TIME         NOT NULL,
    crossDay           TINYINT      NOT NULL DEFAULT 0 COMMENT '1=跨天,0=不跨天',
    status             TINYINT      NOT NULL DEFAULT 1 COMMENT '1=启用,0=禁用',
    deleted            TINYINT      NOT NULL DEFAULT 0,
    createdBy          VARCHAR(64)  NULL,
    createdAt          DATETIME     NULL,
    updatedBy          VARCHAR(64)  NULL,
    updatedAt          DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_shift_code (shiftCode, deleted),
    INDEX idx_shift_schedule (shiftScheduleId),
    CONSTRAINT fk_shift_schedule FOREIGN KEY (shiftScheduleId) REFERENCES shift_schedule(id)
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

-- Factory table
CREATE TABLE IF NOT EXISTS factory (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    factoryCode   VARCHAR(50)  NOT NULL,
    name          VARCHAR(50)  NOT NULL,
    shortName     VARCHAR(50)  NULL,
    companyId     BIGINT       NOT NULL,
    status        TINYINT      NOT NULL DEFAULT 1 COMMENT '1=启用,0=禁用',
    deleted       TINYINT      NOT NULL DEFAULT 0,
    createdBy     VARCHAR(64)  NULL,
    createdAt     DATETIME     NULL,
    updatedBy     VARCHAR(64)  NULL,
    updatedAt     DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_factory_code (factoryCode, deleted),
    INDEX idx_factory_company (companyId),
    CONSTRAINT fk_factory_company FOREIGN KEY (companyId) REFERENCES company(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工厂';

-- Department table
CREATE TABLE IF NOT EXISTS department (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    departmentCode  VARCHAR(50)  NOT NULL,
    name            VARCHAR(50)  NOT NULL,
    factoryId       BIGINT       NOT NULL,
    parentId        BIGINT       NULL COMMENT '上级部门ID，NULL表示顶级部门',
    sortOrder       INT          NOT NULL DEFAULT 0,
    status          TINYINT      NOT NULL DEFAULT 1 COMMENT '1=启用,0=禁用',
    deleted         TINYINT      NOT NULL DEFAULT 0,
    createdBy       VARCHAR(64)  NULL,
    createdAt       DATETIME     NULL,
    updatedBy       VARCHAR(64)  NULL,
    updatedAt       DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_department_code (departmentCode, deleted),
    INDEX idx_dept_factory (factoryId),
    INDEX idx_dept_parent (parentId),
    CONSTRAINT fk_dept_factory FOREIGN KEY (factoryId) REFERENCES factory(id),
    CONSTRAINT fk_dept_parent FOREIGN KEY (parentId) REFERENCES department(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门';

-- User table
CREATE TABLE IF NOT EXISTS user (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    username      VARCHAR(50)  NOT NULL,
    realName      VARCHAR(50)  NOT NULL,
    status        TINYINT      NOT NULL DEFAULT 1 COMMENT '1=启用,0=禁用',
    createdBy     VARCHAR(64)  NULL,
    createdAt     DATETIME     NULL,
    updatedBy     VARCHAR(64)  NULL,
    updatedAt     DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户';

-- Department User relationship table
CREATE TABLE IF NOT EXISTS department_user (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    departmentId    BIGINT       NOT NULL,
    userId          BIGINT       NOT NULL,
    createdBy       VARCHAR(64)  NULL,
    createdAt       DATETIME     NULL,
    updatedBy       VARCHAR(64)  NULL,
    updatedAt       DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_dept_user (departmentId, userId),
    INDEX idx_dept_user_dept (departmentId),
    INDEX idx_dept_user_user (userId),
    CONSTRAINT fk_du_department FOREIGN KEY (departmentId) REFERENCES department(id),
    CONSTRAINT fk_du_user FOREIGN KEY (userId) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门用户关系';

-- Work Center table
CREATE TABLE IF NOT EXISTS work_center (
    id                 BIGINT       NOT NULL AUTO_INCREMENT,
    workCenterCode     VARCHAR(50)  NOT NULL,
    name               VARCHAR(50)  NOT NULL,
    factoryId          BIGINT       NOT NULL,
    status             TINYINT      NOT NULL DEFAULT 1 COMMENT '1=启用,0=禁用',
    deleted            TINYINT      NOT NULL DEFAULT 0,
    createdBy          VARCHAR(64)  NULL,
    createdAt          DATETIME     NULL,
    updatedBy          VARCHAR(64)  NULL,
    updatedAt          DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_work_center_code (workCenterCode, deleted),
    INDEX idx_wc_factory (factoryId),
    CONSTRAINT fk_wc_factory FOREIGN KEY (factoryId) REFERENCES factory(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工作中心';

-- Process table
CREATE TABLE IF NOT EXISTS process (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    processCode     VARCHAR(50)  NOT NULL,
    name            VARCHAR(50)  NOT NULL,
    workCenterId    BIGINT       NOT NULL,
    status          TINYINT      NOT NULL DEFAULT 1 COMMENT '1=启用,0=禁用',
    deleted         TINYINT      NOT NULL DEFAULT 0,
    createdBy       VARCHAR(64)  NULL,
    createdAt       DATETIME     NULL,
    updatedBy       VARCHAR(64)  NULL,
    updatedAt       DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_process_code (processCode, deleted),
    INDEX idx_process_work_center (workCenterId),
    CONSTRAINT fk_process_wc FOREIGN KEY (workCenterId) REFERENCES work_center(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工序';

-- Audit Log table
CREATE TABLE IF NOT EXISTS audit_log (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    tableName       VARCHAR(64)  NOT NULL,
    recordId        BIGINT       NOT NULL,
    action          VARCHAR(16)  NOT NULL COMMENT 'CREATE/UPDATE/DELETE',
    oldValue        TEXT         NULL COMMENT '变更前值(JSON)',
    newValue        TEXT         NULL COMMENT '变更后值(JSON)',
    changedFields   VARCHAR(512) NULL COMMENT '变更字段列表',
    operatorId      BIGINT       NULL,
    operatorName    VARCHAR(64)  NULL,
    createdAt       DATETIME     NULL,
    createdBy       VARCHAR(64)  NULL,
    updatedBy       VARCHAR(64)  NULL,
    updatedAt       DATETIME     NULL,
    PRIMARY KEY (id),
    INDEX idx_audit_table_record (tableName, recordId),
    INDEX idx_audit_created (createdAt)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='变更日志';

-- Material Category table
CREATE TABLE IF NOT EXISTS material_category (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    categoryCode        VARCHAR(50)  NOT NULL,
    categoryName        VARCHAR(50)  NOT NULL,
    isQualityCategory   TINYINT      NOT NULL DEFAULT 0 COMMENT '是否质量分类 0-否, 1-是',
    parentId            BIGINT       NULL COMMENT '父分类ID，NULL表示顶级分类',
    status              TINYINT      NOT NULL DEFAULT 1 COMMENT '1=启用,0=禁用',
    deleted             TINYINT      NOT NULL DEFAULT 0,
    createdBy           VARCHAR(64)  NULL,
    createdAt           DATETIME     NULL,
    updatedBy           VARCHAR(64)  NULL,
    updatedAt           DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_category_code (categoryCode, deleted),
    UNIQUE KEY uk_category_name (categoryName, deleted),
    INDEX idx_parent (parentId),
    CONSTRAINT fk_category_parent FOREIGN KEY (parentId) REFERENCES material_category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='物料分类';
