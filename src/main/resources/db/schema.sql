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
    password      VARCHAR(100) NOT NULL DEFAULT '' COMMENT '密码',
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

-- Uom (计量单位) table
CREATE TABLE IF NOT EXISTS uom (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    uomCode         VARCHAR(50)  NOT NULL,
    uomName         VARCHAR(50)  NOT NULL,
    status          TINYINT      NOT NULL DEFAULT 1 COMMENT '1=启用,0=禁用',
    uomPrecision    DECIMAL(10,4) DEFAULT NULL COMMENT '计算精度',
    deleted         TINYINT      NOT NULL DEFAULT 0,
    createdBy       VARCHAR(64)  NULL,
    createdAt       DATETIME     NULL,
    updatedBy       VARCHAR(64)  NULL,
    updatedAt       DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_uom_code (uomCode, deleted),
    UNIQUE KEY uk_uom_name (uomName, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='计量单位';

-- UomConversion (单位换算比例) table
CREATE TABLE IF NOT EXISTS uom_conversion (
    id              BIGINT        NOT NULL AUTO_INCREMENT,
    fromUomId       BIGINT        NOT NULL COMMENT '原单位ID',
    fromUomCode     VARCHAR(50)   NOT NULL COMMENT '原单位编码(冗余)',
    fromUomName     VARCHAR(50)   NOT NULL COMMENT '原单位名称(冗余)',
    toUomId         BIGINT        NOT NULL COMMENT '目标单位ID',
    toUomCode       VARCHAR(50)   NOT NULL COMMENT '目标单位编码(冗余)',
    toUomName       VARCHAR(50)   NOT NULL COMMENT '目标单位名称(冗余)',
    conversionRate  DECIMAL(18,6) NOT NULL COMMENT '换算率',
    status          TINYINT       NOT NULL DEFAULT 1 COMMENT '1=启用,0=禁用',
    deleted         TINYINT       NOT NULL DEFAULT 0,
    createdBy       VARCHAR(64)   NULL,
    createdAt       DATETIME      NULL,
    updatedBy       VARCHAR(64)   NULL,
    updatedAt       DATETIME      NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_from_to (fromUomId, toUomId, deleted),
    INDEX idx_uom_conv_from (fromUomId),
    INDEX idx_uom_conv_to (toUomId),
    CONSTRAINT fk_uom_conv_from FOREIGN KEY (fromUomId) REFERENCES uom(id),
    CONSTRAINT fk_uom_conv_to FOREIGN KEY (toUomId) REFERENCES uom(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='单位换算比例';

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

-- Material Master table
CREATE TABLE IF NOT EXISTS material_master (
    id                  BIGINT        NOT NULL AUTO_INCREMENT,
    materialCode        VARCHAR(50)   NOT NULL,
    materialName        VARCHAR(255)  NOT NULL,
    status              TINYINT       NOT NULL DEFAULT 1 COMMENT '1=启用, 0=禁用',
    basicCategory       VARCHAR(50)   NOT NULL COMMENT '基本分类: 成品/半成品/原材料/备件',
    categoryId          BIGINT        NOT NULL COMMENT '物料分类ID',
    attributeCategory   VARCHAR(50)   NOT NULL COMMENT '属性分类: 采购件/自制件/采购&自制件',
    uomId               BIGINT        NOT NULL COMMENT '单位ID',
    size                DECIMAL(18,4) DEFAULT NULL,
    length              DECIMAL(18,4) DEFAULT NULL,
    width               DECIMAL(18,4) DEFAULT NULL,
    model               VARCHAR(50)   DEFAULT NULL,
    specification       VARCHAR(50)   DEFAULT NULL,
    thickness           DECIMAL(18,4) DEFAULT NULL,
    color               VARCHAR(50)   DEFAULT NULL,
    tgValue             VARCHAR(50)   DEFAULT NULL,
    copperThickness     VARCHAR(50)   DEFAULT NULL,
    isCopperContained   TINYINT       DEFAULT NULL COMMENT '是否含铜 0=否, 1=是',
    diameter            DECIMAL(18,4) DEFAULT NULL,
    bladeLength         DECIMAL(18,4) DEFAULT NULL,
    totalLength         DECIMAL(18,4) DEFAULT NULL,
    ext1                VARCHAR(50)   DEFAULT NULL COMMENT '扩展字段1',
    ext2                VARCHAR(50)   DEFAULT NULL COMMENT '扩展字段2',
    ext3                VARCHAR(50)   DEFAULT NULL COMMENT '扩展字段3',
    ext4                VARCHAR(50)   DEFAULT NULL COMMENT '扩展字段4',
    ext5                VARCHAR(50)   DEFAULT NULL COMMENT '扩展字段5',
    deleted             TINYINT       NOT NULL DEFAULT 0 COMMENT '0=未删除, 1=已删除',
    createdBy           VARCHAR(64)   NULL,
    createdAt           DATETIME      NULL,
    updatedBy           VARCHAR(64)   NULL,
    updatedAt           DATETIME      NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_material_code (materialCode, deleted),
    UNIQUE KEY uk_material_name (materialName, deleted),
    INDEX idx_material_category (categoryId),
    INDEX idx_material_uom (uomId),
    INDEX idx_basic_category (basicCategory),
    INDEX idx_material_status (status),
    CONSTRAINT fk_material_category FOREIGN KEY (categoryId) REFERENCES material_category(id),
    CONSTRAINT fk_material_uom FOREIGN KEY (uomId) REFERENCES uom(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='物料信息';

-- Material Version table
CREATE TABLE IF NOT EXISTS material_version (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    materialId      BIGINT       NOT NULL COMMENT '物料ID',
    versionNo       VARCHAR(20)  NOT NULL COMMENT '版本号, 如 A.1, A.2',
    status          TINYINT      NOT NULL DEFAULT 1 COMMENT '1=启用, 0=禁用',
    deleted         TINYINT      NOT NULL DEFAULT 0 COMMENT '0=未删除, 1=已删除',
    createdBy       VARCHAR(64)  NULL,
    createdAt       DATETIME     NULL,
    updatedBy       VARCHAR(64)  NULL,
    updatedAt       DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_material_version (materialId, versionNo, deleted),
    INDEX idx_version_material (materialId),
    CONSTRAINT fk_version_material FOREIGN KEY (materialId) REFERENCES material_master(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='物料版本';

-- Equipment Type table
CREATE TABLE IF NOT EXISTS equipment_type (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    typeCode        VARCHAR(50)  NOT NULL,
    typeName        VARCHAR(50)  NOT NULL,
    status          TINYINT      NOT NULL DEFAULT 1 COMMENT '1=启用,0=禁用',
    deleted         TINYINT      NOT NULL DEFAULT 0,
    createdBy       VARCHAR(64)  NULL,
    createdAt       DATETIME     NULL,
    updatedBy       VARCHAR(64)  NULL,
    updatedAt       DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_type_code (typeCode, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备类型';

-- Equipment Model table
CREATE TABLE IF NOT EXISTS equipment_model (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    modelCode           VARCHAR(50)  NOT NULL,
    modelName           VARCHAR(50)  NOT NULL,
    equipmentTypeId     BIGINT       NOT NULL COMMENT '设备类型ID',
    typeCode            VARCHAR(50)  NOT NULL COMMENT '设备类型编码(冗余)',
    typeName            VARCHAR(50)  NOT NULL COMMENT '设备类型名称(冗余)',
    status              TINYINT      NOT NULL DEFAULT 1 COMMENT '1=启用,0=禁用',
    deleted             TINYINT      NOT NULL DEFAULT 0,
    createdBy           VARCHAR(64)  NULL,
    createdAt           DATETIME     NULL,
    updatedBy           VARCHAR(64)  NULL,
    updatedAt           DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_model_code (modelCode, deleted),
    INDEX idx_equipment_type (equipmentTypeId),
    CONSTRAINT fk_model_type FOREIGN KEY (equipmentTypeId) REFERENCES equipment_type(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备型号';

-- Inspection Exemption (免检清单) table
CREATE TABLE IF NOT EXISTS inspection_exemption (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    materialId      BIGINT       NOT NULL COMMENT '物料ID',
    materialCode    VARCHAR(50)  NOT NULL COMMENT '物料编码(冗余)',
    materialName    VARCHAR(255) NOT NULL COMMENT '物料名称(冗余)',
    supplierId      BIGINT       DEFAULT NULL COMMENT '供应商ID(可空, 逻辑引用)',
    supplierCode    VARCHAR(50)  DEFAULT NULL COMMENT '供应商编码(冗余,可空)',
    supplierName    VARCHAR(50)  DEFAULT NULL COMMENT '供应商名称(冗余,可空)',
    status          TINYINT      NOT NULL DEFAULT 1 COMMENT '1=启用, 0=禁用',
    validFrom       DATE         DEFAULT NULL COMMENT '有效开始日期',
    validTo         DATE         DEFAULT NULL COMMENT '有效结束日期',
    deleted         TINYINT      NOT NULL DEFAULT 0 COMMENT '0=未删除, 1=已删除',
    createdBy       VARCHAR(64)  NULL,
    createdAt       DATETIME     NULL,
    updatedBy       VARCHAR(64)  NULL,
    updatedAt       DATETIME     NULL,
    PRIMARY KEY (id),
    INDEX idx_material (materialId),
    INDEX idx_supplier (supplierId),
    INDEX idx_status (status),
    INDEX idx_valid_date (validFrom, validTo),
    CONSTRAINT fk_exemption_material FOREIGN KEY (materialId) REFERENCES material_master(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='免检清单';

-- Customer (客户) table
CREATE TABLE IF NOT EXISTS customer (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    customerCode    VARCHAR(50)  NOT NULL COMMENT '客户编码，创建后不可修改',
    customerName    VARCHAR(50)  NOT NULL COMMENT '客户名称',
    status          TINYINT      NOT NULL DEFAULT 1 COMMENT '1=启用, 0=禁用',
    type            VARCHAR(50)  DEFAULT NULL COMMENT '类型: 外贸客户/内贸客户',
    shortName       VARCHAR(50)  DEFAULT NULL COMMENT '简称',
    contactPerson   VARCHAR(50)  DEFAULT NULL COMMENT '联系人',
    phone           VARCHAR(50)  DEFAULT NULL COMMENT '电话',
    address         VARCHAR(50)  DEFAULT NULL COMMENT '地址',
    email           VARCHAR(50)  DEFAULT NULL COMMENT '邮箱',
    deleted         TINYINT      NOT NULL DEFAULT 0 COMMENT '0=未删除, 1=已删除',
    createdBy       VARCHAR(64)  NULL,
    createdAt       DATETIME     NULL,
    updatedBy       VARCHAR(64)  NULL,
    updatedAt       DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_customer_code (customerCode),
    INDEX idx_status (status),
    INDEX idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户';

-- Customer Material Association (客户物料关联) table
CREATE TABLE IF NOT EXISTS customer_material (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    customerId    BIGINT       NOT NULL COMMENT '客户ID',
    materialId    BIGINT       NOT NULL COMMENT '物料ID',
    deleted       TINYINT      NOT NULL DEFAULT 0 COMMENT '0=未删除, 1=已删除',
    createdBy     VARCHAR(64)  NULL,
    createdAt     DATETIME     NULL,
    updatedBy     VARCHAR(64)  NULL,
    updatedAt     DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_customer_material (customerId, materialId),
    INDEX idx_customer (customerId),
    INDEX idx_material (materialId),
    CONSTRAINT fk_cm_customer FOREIGN KEY (customerId) REFERENCES customer(id),
    CONSTRAINT fk_cm_material FOREIGN KEY (materialId) REFERENCES material_master(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户物料关联';

-- Equipment Ledger table
CREATE TABLE IF NOT EXISTS equipment_ledger (
    id                    BIGINT        NOT NULL AUTO_INCREMENT,
    equipmentCode         VARCHAR(50)   NOT NULL,
    equipmentName         VARCHAR(50)   NOT NULL,
    equipmentModelId      BIGINT        NOT NULL COMMENT '设备型号ID',
    modelCode             VARCHAR(50)   NOT NULL COMMENT '设备型号编码(冗余)',
    modelName             VARCHAR(50)   NOT NULL COMMENT '设备型号名称(冗余)',
    equipmentTypeId       BIGINT        NOT NULL COMMENT '设备类型ID(冗余,随型号带出)',
    typeCode              VARCHAR(50)   NOT NULL COMMENT '设备类型编码(冗余)',
    typeName              VARCHAR(50)   NOT NULL COMMENT '设备类型名称(冗余)',
    runningStatus         VARCHAR(20)   NOT NULL DEFAULT 'SHUTDOWN' COMMENT '运行状态: RUNNING/FAULT/SHUTDOWN/MAINTENANCE',
    manageStatus          VARCHAR(20)   NOT NULL DEFAULT 'IN_USE' COMMENT '管理状态: IN_USE/IDLE/SCRAPPED',
    factoryId             BIGINT        NOT NULL COMMENT '工厂ID',
    factoryCode           VARCHAR(50)   NOT NULL COMMENT '工厂编码(冗余)',
    factoryName           VARCHAR(50)   NOT NULL COMMENT '工厂名称(冗余)',
    manufacturer          VARCHAR(100)  DEFAULT NULL COMMENT '生产厂家',
    commissioningDate     DATE          NOT NULL COMMENT '入场时间',
    status                TINYINT       NOT NULL DEFAULT 1 COMMENT '1-启用, 0-禁用',
    deleted               TINYINT       NOT NULL DEFAULT 0 COMMENT '0-未删除, 1-已删除',
    createdBy             VARCHAR(64)   DEFAULT NULL,
    createdAt             DATETIME      DEFAULT NULL,
    updatedBy             VARCHAR(64)   DEFAULT NULL,
    updatedAt             DATETIME      DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_equipment_code (equipmentCode, deleted),
    INDEX idx_equipment_model (equipmentModelId),
    INDEX idx_equipment_type (equipmentTypeId),
    INDEX idx_factory (factoryId),
    INDEX idx_running_status (runningStatus),
    INDEX idx_manage_status (manageStatus),
    CONSTRAINT fk_ledger_model FOREIGN KEY (equipmentModelId) REFERENCES equipment_model(id),
    CONSTRAINT fk_ledger_factory FOREIGN KEY (factoryId) REFERENCES factory(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备台账';

-- Supplier (供应商) table
CREATE TABLE IF NOT EXISTS supplier (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    supplierCode    VARCHAR(50)  NOT NULL COMMENT '供应商编码，创建后不可修改',
    supplierName    VARCHAR(255) NOT NULL COMMENT '供应商名称',
    status          TINYINT      NOT NULL DEFAULT 1 COMMENT '1=启用, 0=禁用',
    shortName       VARCHAR(50)  DEFAULT NULL COMMENT '简称',
    contactPerson   VARCHAR(50)  DEFAULT NULL COMMENT '联系人',
    phone           VARCHAR(50)  DEFAULT NULL COMMENT '电话',
    address         VARCHAR(50)  DEFAULT NULL COMMENT '地址',
    email           VARCHAR(50)  DEFAULT NULL COMMENT '邮箱',
    description     VARCHAR(50)  DEFAULT NULL COMMENT '描述',
    deleted         TINYINT      NOT NULL DEFAULT 0 COMMENT '0=未删除, 1=已删除',
    createdBy       VARCHAR(64)  NULL,
    createdAt       DATETIME     NULL,
    updatedBy       VARCHAR(64)  NULL,
    updatedAt       DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_supplier_code (supplierCode),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='供应商';

-- Supplier Material Association (供应商物料关联) table
CREATE TABLE IF NOT EXISTS supplier_material (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    supplierId    BIGINT       NOT NULL COMMENT '供应商ID',
    materialId    BIGINT       NOT NULL COMMENT '物料ID',
    deleted       TINYINT      NOT NULL DEFAULT 0 COMMENT '0=未删除, 1=已删除',
    createdBy     VARCHAR(64)  NULL,
    createdAt     DATETIME     NULL,
    updatedBy     VARCHAR(64)  NULL,
    updatedAt     DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_supplier_material (supplierId, materialId),
    INDEX idx_supplier (supplierId),
    INDEX idx_material (materialId),
    CONSTRAINT fk_sm_supplier FOREIGN KEY (supplierId) REFERENCES supplier(id),
    CONSTRAINT fk_sm_material FOREIGN KEY (materialId) REFERENCES material_master(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='供应商物料关联';

-- Data Permission Group table
CREATE TABLE IF NOT EXISTS data_permission_group (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    groupName     VARCHAR(50)  NOT NULL,
    remark        VARCHAR(200) NULL,
    deleted       TINYINT      NOT NULL DEFAULT 0,
    createdBy     VARCHAR(64)  NULL,
    createdAt     DATETIME     NULL,
    updatedBy     VARCHAR(64)  NULL,
    updatedAt     DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_group_name (groupName, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据权限组';

-- Data Permission Group - Factory association table
CREATE TABLE IF NOT EXISTS data_permission_group_factory (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    groupId       BIGINT       NOT NULL,
    factoryId     BIGINT       NOT NULL,
    createdBy     VARCHAR(64)  NULL,
    createdAt     DATETIME     NULL,
    updatedBy     VARCHAR(64)  NULL,
    updatedAt     DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_group_factory (groupId, factoryId),
    INDEX idx_pg_factory_factory (factoryId),
    CONSTRAINT fk_pgf_group FOREIGN KEY (groupId) REFERENCES data_permission_group(id),
    CONSTRAINT fk_pgf_factory FOREIGN KEY (factoryId) REFERENCES factory(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据权限组-工厂关联';

-- Data Permission Group - Work Center association table
CREATE TABLE IF NOT EXISTS data_permission_group_work_center (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    groupId          BIGINT       NOT NULL,
    workCenterId     BIGINT       NOT NULL,
    createdBy        VARCHAR(64)  NULL,
    createdAt        DATETIME     NULL,
    updatedBy        VARCHAR(64)  NULL,
    updatedAt        DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_group_work_center (groupId, workCenterId),
    INDEX idx_pg_wc_wc (workCenterId),
    CONSTRAINT fk_pgw_group FOREIGN KEY (groupId) REFERENCES data_permission_group(id),
    CONSTRAINT fk_pgw_wc FOREIGN KEY (workCenterId) REFERENCES work_center(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据权限组-工作中心关联';

-- Data Permission Group - Process association table
CREATE TABLE IF NOT EXISTS data_permission_group_process (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    groupId       BIGINT       NOT NULL,
    processId     BIGINT       NOT NULL,
    createdBy     VARCHAR(64)  NULL,
    createdAt     DATETIME     NULL,
    updatedBy     VARCHAR(64)  NULL,
    updatedAt     DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_group_process (groupId, processId),
    INDEX idx_pg_process_process (processId),
    CONSTRAINT fk_pgp_group FOREIGN KEY (groupId) REFERENCES data_permission_group(id),
    CONSTRAINT fk_pgp_process FOREIGN KEY (processId) REFERENCES process(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据权限组-工序关联';

-- User Data Permission (用户数据权限) table
CREATE TABLE IF NOT EXISTS user_data_permission (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    userId        BIGINT       NOT NULL,
    groupId       BIGINT       NULL COMMENT '关联权限组，NULL表示仅直接授权',
    createdBy     VARCHAR(64)  NULL,
    createdAt     DATETIME     NULL,
    updatedBy     VARCHAR(64)  NULL,
    updatedAt     DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_perm_user (userId),
    INDEX idx_user_perm_group (groupId),
    CONSTRAINT fk_udp_group FOREIGN KEY (groupId) REFERENCES data_permission_group(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户数据权限';

-- User Data Permission - Factory association table
CREATE TABLE IF NOT EXISTS user_data_permission_factory (
    id                   BIGINT       NOT NULL AUTO_INCREMENT,
    userPermissionId     BIGINT       NOT NULL,
    factoryId            BIGINT       NOT NULL,
    source               VARCHAR(10)  NOT NULL COMMENT 'GROUP=权限组继承, DIRECT=直接授权',
    createdBy            VARCHAR(64)  NULL,
    createdAt            DATETIME     NULL,
    updatedBy            VARCHAR(64)  NULL,
    updatedAt            DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_up_factory (userPermissionId, factoryId),
    INDEX idx_upf_factory (factoryId),
    INDEX idx_upf_source (userPermissionId, source),
    CONSTRAINT fk_upf_perm FOREIGN KEY (userPermissionId) REFERENCES user_data_permission(id),
    CONSTRAINT fk_upf_factory FOREIGN KEY (factoryId) REFERENCES factory(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户数据权限-工厂';

-- User Data Permission - Work Center association table
CREATE TABLE IF NOT EXISTS user_data_permission_work_center (
    id                   BIGINT       NOT NULL AUTO_INCREMENT,
    userPermissionId     BIGINT       NOT NULL,
    workCenterId         BIGINT       NOT NULL,
    source               VARCHAR(10)  NOT NULL COMMENT 'GROUP/DIRECT',
    createdBy            VARCHAR(64)  NULL,
    createdAt            DATETIME     NULL,
    updatedBy            VARCHAR(64)  NULL,
    updatedAt            DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_up_work_center (userPermissionId, workCenterId),
    INDEX idx_upw_wc (workCenterId),
    INDEX idx_upw_source (userPermissionId, source),
    CONSTRAINT fk_upw_perm FOREIGN KEY (userPermissionId) REFERENCES user_data_permission(id),
    CONSTRAINT fk_upw_wc FOREIGN KEY (workCenterId) REFERENCES work_center(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户数据权限-工作中心';

-- User Data Permission - Process association table
CREATE TABLE IF NOT EXISTS user_data_permission_process (
    id                   BIGINT       NOT NULL AUTO_INCREMENT,
    userPermissionId     BIGINT       NOT NULL,
    processId            BIGINT       NOT NULL,
    source               VARCHAR(10)  NOT NULL COMMENT 'GROUP/DIRECT',
    createdBy            VARCHAR(64)  NULL,
    createdAt            DATETIME     NULL,
    updatedBy            VARCHAR(64)  NULL,
    updatedAt            DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_up_process (userPermissionId, processId),
    INDEX idx_upp_process (processId),
    INDEX idx_upp_source (userPermissionId, source),
    CONSTRAINT fk_upp_perm FOREIGN KEY (userPermissionId) REFERENCES user_data_permission(id),
    CONSTRAINT fk_upp_process FOREIGN KEY (processId) REFERENCES process(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户数据权限-工序';
