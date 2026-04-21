# Feature: feat-supplier 供应商管理

## 基本信息
- **ID**: feat-supplier
- **名称**: 供应商管理
- **优先级**: 61
- **规模**: S
- **依赖**: feat-material-master
- **父模块**: feat-supply-chain
- **创建时间**: 2026-04-21

## 需求来源
- 父模块: feat-supply-chain（供应链主数据）
- 文档: 供应链主数据_功能设计_V1.0 → 第4章 供应商

## 需求描述
管理 PCB 制造的供应商基础信息，包括供应商编码、名称、简称、联系人、电话、地址、邮箱、描述，并支持关联供货物料，用于采购、入库和质量追溯。

### 功能清单
| 功能 | 说明 |
|------|------|
| 创建 | 填写供应商信息，校验编码和名称必填，校验编码唯一性 |
| 编辑 | 修改供应商信息（编码不可修改） |
| 删除 | 已被引用的供应商不可删除（按钮置灰） |
| 查询 | 按编码、名称（模糊）、状态筛选 |
| 启用/禁用 | 切换供应商状态 |
| 选择物料 | 关联供货物料，支持多选 |
| 导入 | Excel 导入供应商数据（含编码唯一性校验） |
| 导出 | 导出查询结果到 Excel |
| 日志 | 查看变更履历 |

## 用户价值点

1. **供应商信息集中管理** — 统一维护供应商编码、名称、联系方式、描述等基础信息，一个入口管理所有供应商
2. **物料关联精准采购** — 关联供货物料，采购下单和入库时快速定位可供应的物料及供应商
3. **质量追溯数据基础** — 为免检清单（InspectionExemption）提供供应商维度，支持按供应商定义免检规则
4. **数据引用安全** — 已被免检清单等业务单据引用的供应商不可删除，保障采购/质量数据完整性
5. **批量维护** — 支持 Excel 导入导出，快速初始化供应商数据

## 上下文分析

### 关联 Feature
- **feat-material-master**（前置）— 提供物料主数据，SupplierMaterial 通过 material_id 引用 material_master 表
- **feat-customer**（兄弟 feature）— 同属供应链模块，结构高度对称（编码+名称+联系人+物料关联），代码模式可完全复用
- **feat-inspection-exemption**（下游引用）— 免检清单通过 supplier_id 逻辑引用供应商表（当前无 DB FK，供应商模块完成后可添加）
- **feat-enterprise-org**（间接依赖）— 提供审计基类 SoftDeleteEntity、全局异常处理 BusinessException、通用分页模式

### 参考代码模式
- `com.litemes.domain.entity.SoftDeleteEntity` — 软删除基类
- `com.litemes.domain.entity.BaseEntity` — 审计字段基类
- 企业管理模块 `Company` — 类似的编码唯一 + 软删除 CRUD 模式
- **feat-customer**（兄弟 feature）— 结构高度对称，Customer/CustomerResource/CustomerService 可直接镜像为 Supplier/SupplierResource/SupplierService

### 关联文档
- [供应链主数据_功能设计_V1.0](../../docx/供应链主数据_功能设计_V1.0/供应链主数据_功能设计_V1.0.md) — 第4章 供应商

## 数据模型

### Supplier（供应商）— 概念模型
| 字段 | 类型 | 必填 | 可编辑 | 备注 |
|------|------|------|--------|------|
| supplierCode | String(50) | Y | N | 唯一 |
| supplierName | String(255) | Y | Y | 列宽显示20个中文长度 |
| status | Boolean | N | N | 默认启用，禁用/启用 |
| shortName | String(50) | N | Y | 简称 |
| contactPerson | String(50) | N | Y | 联系人 |
| phone | String(50) | N | Y | 电话 |
| address | String(50) | N | Y | 地址 |
| email | String(50) | N | Y | 邮箱 |
| description | String(50) | N | Y | 描述 |
| createdBy | String | N | N | 系统登录人 |
| createdTime | DateTime | N | N | yyyy/MM/dd HH:mm:ss |
| modifiedBy | String | N | N | 系统登录人 |
| modifiedTime | DateTime | N | N | yyyy/MM/dd HH:mm:ss |

### SupplierMaterial（供应商物料关联）— 概念模型
| 字段 | 类型 | 备注 |
|------|------|------|
| supplierId | Long | 供应商 ID |
| materialId | Long | 物料 ID |

## 数据库模型设计

### 基类继承

| 实体 | 基类 | 审计字段 | 软删除 |
|------|------|---------|--------|
| Supplier | SoftDeleteEntity | ✓ | ✓ |
| SupplierMaterial | SoftDeleteEntity | ✓ | ✓ |

### Supplier（供应商）

| 列名 | 数据类型 | 必录 | 可编辑 | 默认值 | 约束 | 备注 |
|------|---------|------|--------|--------|------|------|
| id | BIGINT | - | - | AUTO_INCREMENT | PK | |
| supplier_code | VARCHAR(50) | Y | N | - | UNIQUE, NOT NULL | 供应商编码，创建后不可修改 |
| supplier_name | VARCHAR(255) | Y | Y | - | NOT NULL | 供应商名称，列宽显示20个中文长度 |
| status | TINYINT | N | N | 1 | NOT NULL | 1=启用, 0=禁用 |
| short_name | VARCHAR(50) | N | Y | NULL | - | 简称 |
| contact_person | VARCHAR(50) | N | Y | NULL | - | 联系人 |
| phone | VARCHAR(50) | N | Y | NULL | - | 电话 |
| address | VARCHAR(50) | N | Y | NULL | - | 地址 |
| email | VARCHAR(50) | N | Y | NULL | - | 邮箱 |
| description | VARCHAR(50) | N | Y | NULL | - | 描述 |
| created_by | VARCHAR(50) | - | - | 系统登录人 | - | |
| created_at | DATETIME | - | - | 系统时间 | - | yyyy/MM/dd HH:mm:ss |
| updated_by | VARCHAR(50) | - | - | 系统登录人 | - | |
| updated_at | DATETIME | - | - | 系统时间 | - | yyyy/MM/dd HH:mm:ss |
| deleted | TINYINT | - | - | 0 | NOT NULL | 0=未删除, 1=已删除 |

```sql
CREATE TABLE supplier (
    id              BIGINT       AUTO_INCREMENT PRIMARY KEY,
    supplier_code   VARCHAR(50)  NOT NULL,
    supplier_name   VARCHAR(255) NOT NULL,
    status          TINYINT      NOT NULL DEFAULT 1 COMMENT '1-启用, 0-禁用',
    short_name      VARCHAR(50)  DEFAULT NULL COMMENT '简称',
    contact_person  VARCHAR(50)  DEFAULT NULL COMMENT '联系人',
    phone           VARCHAR(50)  DEFAULT NULL COMMENT '电话',
    address         VARCHAR(50)  DEFAULT NULL COMMENT '地址',
    email           VARCHAR(50)  DEFAULT NULL COMMENT '邮箱',
    description     VARCHAR(50)  DEFAULT NULL COMMENT '描述',
    created_by      VARCHAR(50)  DEFAULT NULL,
    created_at      DATETIME     DEFAULT NULL,
    updated_by      VARCHAR(50)  DEFAULT NULL,
    updated_at      DATETIME     DEFAULT NULL,
    deleted         TINYINT      NOT NULL DEFAULT 0 COMMENT '0-未删除, 1-已删除',
    UNIQUE KEY uk_supplier_code (supplier_code),
    INDEX idx_status (status)
) COMMENT '供应商';
```

### SupplierMaterial（供应商物料关联）

| 列名 | 数据类型 | 必录 | 可编辑 | 默认值 | 约束 | 备注 |
|------|---------|------|--------|--------|------|------|
| id | BIGINT | - | - | AUTO_INCREMENT | PK | |
| supplier_id | BIGINT | Y | N | - | FK → supplier.id, NOT NULL | 供应商ID |
| material_id | BIGINT | Y | N | - | FK → material_master.id, NOT NULL | 物料ID |
| created_by | VARCHAR(50) | - | - | 系统登录人 | - | |
| created_at | DATETIME | - | - | 系统时间 | - | |
| updated_by | VARCHAR(50) | - | - | 系统登录人 | - | |
| updated_at | DATETIME | - | - | 系统时间 | - | |
| deleted | TINYINT | - | - | 0 | NOT NULL | 0=未删除, 1=已删除 |

```sql
CREATE TABLE supplier_material (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    supplier_id   BIGINT NOT NULL COMMENT '供应商ID',
    material_id   BIGINT NOT NULL COMMENT '物料ID',
    created_by    VARCHAR(50) DEFAULT NULL,
    created_at    DATETIME DEFAULT NULL,
    updated_by    VARCHAR(50) DEFAULT NULL,
    updated_at    DATETIME DEFAULT NULL,
    deleted       TINYINT NOT NULL DEFAULT 0 COMMENT '0-未删除, 1-已删除',
    UNIQUE KEY uk_supplier_material (supplier_id, material_id),
    INDEX idx_supplier (supplier_id),
    INDEX idx_material (material_id),
    CONSTRAINT fk_sm_supplier FOREIGN KEY (supplier_id) REFERENCES supplier(id),
    CONSTRAINT fk_sm_material FOREIGN KEY (material_id) REFERENCES material_master(id)
) COMMENT '供应商物料关联';
```

### 设计决策

| 决策 | 原因 |
|------|------|
| supplier_code VARCHAR(50) UNIQUE | 业务主键，创建后不可修改，需 DB 级唯一约束 |
| supplier_name VARCHAR(255) | 设计文档要求显示 20 个中文字符长度，255 足够 |
| description 字段仅 VARCHAR(50) | 与设计文档保持一致，描述内容简短 |
| 关联表独立而非 JSON/逗号分隔 | 支持按物料反查供应商、引用完整性校验 |
| (supplier_id, material_id) 联合唯一 | 同一供应商不允许重复关联同一物料 |
| material_id FK → material_master | 确保物料引用完整性，级联查询效率高 |
| supplier_id FK → supplier | 供应商删除时关联数据通过软删除自动隔离 |
| InspectionExemption.supplier_id 不在本模块设 FK | 免检清单在 feat-material-master 模块中，跨模块仅做逻辑引用；本模块完成后可选择性在其表上添加 FK |

### 下游引用影响（feat-inspection-exemption）

免检清单表 `inspection_exemption` 通过 `supplier_id` 逻辑引用 `supplier` 表：
- 当前状态：无 DB FK，`supplier_id BIGINT DEFAULT NULL`
- 本模块完成后：可执行 `ALTER TABLE inspection_exemption ADD CONSTRAINT fk_exemption_supplier FOREIGN KEY (supplier_id) REFERENCES supplier(id)`
- 此操作列入 feat-inspection-exemption 的跨模块集成任务中

## 业务逻辑
- **创建**：供应商编码、名称必填；校验编码唯一性；创建后状态默认启用
- **编辑**：编码不可修改，可修改名称、简称、联系方式、描述等属性
- **删除**：已被引用的供应商不可删除（按钮置灰）— 引用方包括免检清单（InspectionExemption）
- **启用/禁用**：切换供应商状态，已禁用的供应商不可被新创建的业务单据引用
- **物料关联**：选择物料时支持多选，关联后可在供应商详情中查看已关联的供货物料列表
- **导入**：唯一性校验，已存在编码不允许再次导入
- **导出**：导出查询结果到 Excel
- **查询**：编码/名称模糊查询，状态下拉筛选
- **日志**：查看变更履历

## 验收标准 (Gherkin)

```gherkin
Feature: 供应商管理

  Scenario: 创建供应商
    Given 用户在供应商管理页面点击创建
    When 填写供应商编码 "S001"、供应商名称 "测试供应商"
    And 点击确认
    Then 供应商创建成功，状态为启用

  Scenario: 供应商编码唯一性校验
    Given 已存在供应商编码 "S001"
    When 创建供应商时输入编码 "S001"
    Then 提示供应商编码已存在

  Scenario: 编辑供应商
    Given 已存在供应商 "S001"
    When 修改供应商名称为 "新名称"
    Then 供应商名称更新成功，供应商编码不可修改

  Scenario: 删除被引用的供应商
    Given 供应商 "S001" 已被其他业务引用
    Then 删除按钮置灰，不可点击

  Scenario: 供应商关联物料
    Given 供应商 "S001" 已创建
    When 选择物料 "MAT001"
    Then 供应商可供货物料清单更新成功

  Scenario: 供应商重复关联物料
    Given 供应商 "S001" 已关联物料 "MAT001"
    When 再次关联物料 "MAT001"
    Then 提示物料已关联，不可重复添加

  Scenario: 导入供应商（编码重复）
    Given 已存在供应商编码 "S001"
    When 导入包含编码 "S001" 的 Excel
    Then 提示编码已存在，拒绝导入

  Scenario: 查询供应商
    Given 存在多个供应商记录
    When 按名称模糊查询 "测试"
    Then 返回名称包含 "测试" 的供应商列表

  Scenario: 禁用供应商
    Given 供应商 "S001" 状态为启用
    When 点击禁用
    Then 供应商状态变为禁用，不可被新业务单据引用
```
