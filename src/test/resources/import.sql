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

CREATE TABLE IF NOT EXISTS uom (
    id              BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    uomCode         VARCHAR(50)  NOT NULL,
    uomName         VARCHAR(50)  NOT NULL,
    status          TINYINT      NOT NULL DEFAULT 1,
    uomPrecision    DECIMAL(10,4) DEFAULT NULL,
    deleted         TINYINT      NOT NULL DEFAULT 0,
    createdBy       VARCHAR(64)  NULL,
    createdAt       TIMESTAMP    NULL,
    updatedBy       VARCHAR(64)  NULL,
    updatedAt       TIMESTAMP    NULL
);

CREATE TABLE IF NOT EXISTS uom_conversion (
    id              BIGINT        NOT NULL AUTO_INCREMENT PRIMARY KEY,
    fromUomId       BIGINT        NOT NULL,
    fromUomCode     VARCHAR(50)   NOT NULL,
    fromUomName     VARCHAR(50)   NOT NULL,
    toUomId         BIGINT        NOT NULL,
    toUomCode       VARCHAR(50)   NOT NULL,
    toUomName       VARCHAR(50)   NOT NULL,
    conversionRate  DECIMAL(18,6) NOT NULL,
    status          TINYINT       NOT NULL DEFAULT 1,
    deleted         TINYINT       NOT NULL DEFAULT 0,
    createdBy       VARCHAR(64)   NULL,
    createdAt       TIMESTAMP     NULL,
    updatedBy       VARCHAR(64)   NULL,
    updatedAt       TIMESTAMP     NULL
);

CREATE TABLE IF NOT EXISTS `user` (
    id            BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL,
    password      VARCHAR(100) NOT NULL DEFAULT '',
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

CREATE TABLE IF NOT EXISTS equipment_type (
    id              BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    typeCode        VARCHAR(50)  NOT NULL,
    typeName        VARCHAR(50)  NOT NULL,
    status          TINYINT      NOT NULL DEFAULT 1,
    deleted         TINYINT      NOT NULL DEFAULT 0,
    createdBy       VARCHAR(64)  NULL,
    createdAt       TIMESTAMP    NULL,
    updatedBy       VARCHAR(64)  NULL,
    updatedAt       TIMESTAMP    NULL
);

CREATE TABLE IF NOT EXISTS equipment_model (
    id                  BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    modelCode           VARCHAR(50)  NOT NULL,
    modelName           VARCHAR(50)  NOT NULL,
    equipmentTypeId     BIGINT       NOT NULL,
    typeCode            VARCHAR(50)  NOT NULL,
    typeName            VARCHAR(50)  NOT NULL,
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
INSERT INTO factory (id, factoryCode, name, shortName, companyId, status, deleted) VALUES (2, 'F002', '测试工厂2', 'TF2', 1, 1, 0);
INSERT INTO work_center (id, workCenterCode, name, factoryId, status, deleted) VALUES (1, 'WC001', '测试工作中心', 1, 1, 0);

-- Seed process data
INSERT INTO process (id, processCode, name, workCenterId, status, deleted) VALUES (1, 'PS001', '测试工序', 1, 1, 0);

-- Material tables for inspection exemption tests
CREATE TABLE IF NOT EXISTS material_master (
    id                  BIGINT        NOT NULL AUTO_INCREMENT PRIMARY KEY,
    materialCode        VARCHAR(50)   NOT NULL,
    materialName        VARCHAR(255)  NOT NULL,
    status              TINYINT       NOT NULL DEFAULT 1,
    basicCategory       VARCHAR(50)   NOT NULL,
    categoryId          BIGINT        NOT NULL,
    attributeCategory   VARCHAR(50)   NOT NULL,
    uomId               BIGINT        NOT NULL,
    size                DECIMAL(18,4) DEFAULT NULL,
    length              DECIMAL(18,4) DEFAULT NULL,
    width               DECIMAL(18,4) DEFAULT NULL,
    model               VARCHAR(50)   DEFAULT NULL,
    specification       VARCHAR(50)   DEFAULT NULL,
    thickness           DECIMAL(18,4) DEFAULT NULL,
    color               VARCHAR(50)   DEFAULT NULL,
    tgValue             VARCHAR(50)   DEFAULT NULL,
    copperThickness     VARCHAR(50)   DEFAULT NULL,
    isCopperContained   TINYINT       DEFAULT NULL,
    diameter            DECIMAL(18,4) DEFAULT NULL,
    bladeLength         DECIMAL(18,4) DEFAULT NULL,
    totalLength         DECIMAL(18,4) DEFAULT NULL,
    ext1                VARCHAR(50)   DEFAULT NULL,
    ext2                VARCHAR(50)   DEFAULT NULL,
    ext3                VARCHAR(50)   DEFAULT NULL,
    ext4                VARCHAR(50)   DEFAULT NULL,
    ext5                VARCHAR(50)   DEFAULT NULL,
    deleted             TINYINT       NOT NULL DEFAULT 0,
    createdBy           VARCHAR(64)   NULL,
    createdAt           TIMESTAMP     NULL,
    updatedBy           VARCHAR(64)   NULL,
    updatedAt           TIMESTAMP     NULL
);

-- Seed material data
INSERT INTO material_category (id, categoryCode, categoryName, isQualityCategory, status, deleted) VALUES (1, 'CAT001', '测试分类', 0, 1, 0);
INSERT INTO uom (id, uomCode, uomName, status, deleted) VALUES (1, 'PCS', '个', 1, 0);
INSERT INTO material_master (id, materialCode, materialName, status, basicCategory, categoryId, attributeCategory, uomId, deleted) VALUES (1, 'MAT-001', '测试物料001', 1, '原材料', 1, '采购件', 1, 0);
INSERT INTO material_master (id, materialCode, materialName, status, basicCategory, categoryId, attributeCategory, uomId, deleted) VALUES (2, 'MAT-002', '测试物料002', 1, '原材料', 1, '采购件', 1, 0);

-- Inspection exemption table
CREATE TABLE IF NOT EXISTS inspection_exemption (
    id              BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    materialId      BIGINT       NOT NULL,
    materialCode    VARCHAR(50)  NOT NULL,
    materialName    VARCHAR(255) NOT NULL,
    supplierId      BIGINT       DEFAULT NULL,
    supplierCode    VARCHAR(50)  DEFAULT NULL,
    supplierName    VARCHAR(50)  DEFAULT NULL,
    status          TINYINT      NOT NULL DEFAULT 1,
    validFrom       DATE         DEFAULT NULL,
    validTo         DATE         DEFAULT NULL,
    deleted         TINYINT      NOT NULL DEFAULT 0,
    createdBy       VARCHAR(64)  NULL,
    createdAt       TIMESTAMP    NULL,
    updatedBy       VARCHAR(64)  NULL,
    updatedAt       TIMESTAMP    NULL
);

-- Equipment Ledger table (H2 test version)
CREATE TABLE IF NOT EXISTS equipment_ledger (
    id                    BIGINT        NOT NULL AUTO_INCREMENT PRIMARY KEY,
    equipmentCode         VARCHAR(50)   NOT NULL,
    equipmentName         VARCHAR(50)   NOT NULL,
    equipmentModelId      BIGINT        NOT NULL,
    modelCode             VARCHAR(50)   NOT NULL,
    modelName             VARCHAR(50)   NOT NULL,
    equipmentTypeId       BIGINT        NOT NULL,
    typeCode              VARCHAR(50)   NOT NULL,
    typeName              VARCHAR(50)   NOT NULL,
    runningStatus         VARCHAR(20)   NOT NULL DEFAULT 'SHUTDOWN',
    manageStatus          VARCHAR(20)   NOT NULL DEFAULT 'IN_USE',
    factoryId             BIGINT        NOT NULL,
    factoryCode           VARCHAR(50)   NOT NULL,
    factoryName           VARCHAR(50)   NOT NULL,
    manufacturer          VARCHAR(100)  DEFAULT NULL,
    commissioningDate     DATE          NOT NULL,
    status                TINYINT       NOT NULL DEFAULT 1,
    deleted               TINYINT       NOT NULL DEFAULT 0,
    createdBy             VARCHAR(64)   DEFAULT NULL,
    createdAt             TIMESTAMP     DEFAULT NULL,
    updatedBy             VARCHAR(64)   DEFAULT NULL,
    updatedAt             TIMESTAMP     DEFAULT NULL
);

-- Data Permission Group tables (H2 test version)
CREATE TABLE IF NOT EXISTS data_permission_group (
    id            BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    groupName     VARCHAR(50)  NOT NULL,
    remark        VARCHAR(200) NULL,
    deleted       TINYINT      NOT NULL DEFAULT 0,
    createdBy     VARCHAR(64)  NULL,
    createdAt     TIMESTAMP    NULL,
    updatedBy     VARCHAR(64)  NULL,
    updatedAt     TIMESTAMP    NULL
);

CREATE TABLE IF NOT EXISTS data_permission_group_factory (
    id            BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    groupId       BIGINT       NOT NULL,
    factoryId     BIGINT       NOT NULL,
    createdBy     VARCHAR(64)  NULL,
    createdAt     TIMESTAMP    NULL,
    updatedBy     VARCHAR(64)  NULL,
    updatedAt     TIMESTAMP    NULL
);

CREATE TABLE IF NOT EXISTS data_permission_group_work_center (
    id               BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    groupId          BIGINT       NOT NULL,
    workCenterId     BIGINT       NOT NULL,
    createdBy        VARCHAR(64)  NULL,
    createdAt        TIMESTAMP    NULL,
    updatedBy        VARCHAR(64)  NULL,
    updatedAt        TIMESTAMP    NULL
);

CREATE TABLE IF NOT EXISTS data_permission_group_process (
    id            BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    groupId       BIGINT       NOT NULL,
    processId     BIGINT       NOT NULL,
    createdBy     VARCHAR(64)  NULL,
    createdAt     TIMESTAMP    NULL,
    updatedBy     VARCHAR(64)  NULL,
    updatedAt     TIMESTAMP    NULL
);
