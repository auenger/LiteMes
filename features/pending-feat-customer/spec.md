# Feature: feat-customer 客户管理

## 基本信息
- **ID**: feat-customer
- **名称**: 客户管理
- **优先级**: 62
- **规模**: S
- **依赖**: feat-material-master
- **父模块**: feat-supply-chain
- **创建时间**: 2026-04-21

## 需求来源
- 父模块: feat-supply-chain（供应链主数据）
- 文档: 供应链主数据_功能设计_V1.0 → 第3章 客户

## 需求描述
管理 PCB 制造的客户基础信息，包括客户编码、名称、类型（外贸/内贸）、简称、联系人、电话、地址、邮箱，并支持关联成品物料，用于出库确认。

### 功能清单
| 功能 | 说明 |
|------|------|
| 创建 | 填写客户信息，校验编码和名称必填，校验编码唯一性 |
| 编辑 | 修改客户信息（编码不可修改） |
| 删除 | 已被引用的客户不可删除（按钮置灰） |
| 查询 | 按编码、名称（模糊）、状态筛选 |
| 启用/禁用 | 切换客户状态 |
| 选择物料 | 关联成品物料，支持多选 |
| 导入 | Excel 导入客户数据 |
| 导出 | 导出查询结果到 Excel |
| 日志 | 查看变更履历 |

## 用户价值点

1. **客户信息集中管理** — 统一维护客户编码、名称、类型、联系方式等基础信息，一个入口管理所有客户
2. **客户类型区分** — 通过外贸/内贸类型区分客户，支持按类型筛选和统计
3. **物料关联精准出库** — 关联成品物料，出库确认时快速定位客户对应的产品
4. **数据引用安全** — 已被业务单据引用的客户不可删除，保障订单/出库数据完整性
5. **批量维护** — 支持 Excel 导入导出，快速初始化客户数据

## 上下文分析

### 关联 Feature
- **feat-material-master**（前置）— 提供物料主数据，CustomerMaterial 通过 material_id 引用 material_master 表
- **feat-supplier**（兄弟 feature）— 同属供应链模块，结构相似（编码+名称+联系人+物料关联），可复用相同的代码模式
- **feat-enterprise-org**（间接依赖）— 提供审计基类 SoftDeleteEntity、全局异常处理 BusinessException、通用分页模式

### 参考代码模式
- `com.litemes.domain.entity.SoftDeleteEntity` — 软删除基类
- `com.litemes.domain.entity.BaseEntity` — 审计字段基类
- 企业管理模块 `Company` — 类似的编码唯一 + 软删除 CRUD 模式，可参照 Resource/Service/Repository 结构
- 物料模块 `MaterialMaster` — 关联表设计模式（MaterialVersion 的 FK 模式），参照 CustomerMaterial 设计

### 关联文档
- [供应链主数据_功能设计_V1.0](../../docx/供应链主数据_功能设计_V1.0/供应链主数据_功能设计_V1.0.md) — 第3章 客户

## 数据模型

### Customer（客户）— 概念模型
| 字段 | 类型 | 必填 | 可编辑 | 备注 |
|------|------|------|--------|------|
| customerCode | String(50) | Y | N | 唯一 |
| customerName | String(50) | Y | Y | |
| status | Boolean | N | N | 默认启用，禁用/启用 |
| type | String(50) | N | Y | 枚举：外贸客户、内贸客户 |
| shortName | String(50) | N | Y | 简称 |
| contactPerson | String(50) | N | Y | 联系人 |
| phone | String(50) | N | Y | 电话 |
| address | String(50) | N | Y | 地址 |
| email | String(50) | N | Y | 邮箱 |
| createdBy | String | N | N | 系统登录人 |
| createdTime | DateTime | N | N | yyyy/MM/dd HH:mm:ss |
| modifiedBy | String | N | N | 系统登录人 |
| modifiedTime | DateTime | N | N | yyyy/MM/dd HH:mm:ss |

### CustomerMaterial（客户物料关联）— 概念模型
| 字段 | 类型 | 备注 |
|------|------|------|
| customerId | Long | 客户 ID |
| materialId | Long | 物料 ID |

## 数据库模型设计

### 基类继承

| 实体 | 基类 | 审计字段 | 软删除 |
|------|------|---------|--------|
| Customer | SoftDeleteEntity | ✓ | ✓ |
| CustomerMaterial | SoftDeleteEntity | ✓ | ✓ |

### Customer（客户）

| 列名 | 数据类型 | 必录 | 可编辑 | 默认值 | 约束 | 备注 |
|------|---------|------|--------|--------|------|------|
| id | BIGINT | - | - | AUTO_INCREMENT | PK | |
| customer_code | VARCHAR(50) | Y | N | - | UNIQUE, NOT NULL | 客户编码，创建后不可修改 |
| customer_name | VARCHAR(50) | Y | Y | - | NOT NULL | 客户名称 |
| status | TINYINT | N | N | 1 | NOT NULL | 1=启用, 0=禁用 |
| type | VARCHAR(50) | N | Y | NULL | - | 类型枚举：外贸客户/内贸客户 |
| short_name | VARCHAR(50) | N | Y | NULL | - | 简称 |
| contact_person | VARCHAR(50) | N | Y | NULL | - | 联系人 |
| phone | VARCHAR(50) | N | Y | NULL | - | 电话 |
| address | VARCHAR(50) | N | Y | NULL | - | 地址 |
| email | VARCHAR(50) | N | Y | NULL | - | 邮箱 |
| created_by | VARCHAR(50) | - | - | 系统登录人 | - | |
| created_at | DATETIME | - | - | 系统时间 | - | yyyy/MM/dd HH:mm:ss |
| updated_by | VARCHAR(50) | - | - | 系统登录人 | - | |
| updated_at | DATETIME | - | - | 系统时间 | - | yyyy/MM/dd HH:mm:ss |
| deleted | TINYINT | - | - | 0 | NOT NULL | 0=未删除, 1=已删除 |

```sql
CREATE TABLE customer (
    id              BIGINT      AUTO_INCREMENT PRIMARY KEY,
    customer_code   VARCHAR(50) NOT NULL,
    customer_name   VARCHAR(50) NOT NULL,
    status          TINYINT     NOT NULL DEFAULT 1 COMMENT '1-启用, 0-禁用',
    type            VARCHAR(50) DEFAULT NULL COMMENT '类型: 外贸客户/内贸客户',
    short_name      VARCHAR(50) DEFAULT NULL COMMENT '简称',
    contact_person  VARCHAR(50) DEFAULT NULL COMMENT '联系人',
    phone           VARCHAR(50) DEFAULT NULL COMMENT '电话',
    address         VARCHAR(50) DEFAULT NULL COMMENT '地址',
    email           VARCHAR(50) DEFAULT NULL COMMENT '邮箱',
    created_by      VARCHAR(50) DEFAULT NULL,
    created_at      DATETIME    DEFAULT NULL,
    updated_by      VARCHAR(50) DEFAULT NULL,
    updated_at      DATETIME    DEFAULT NULL,
    deleted         TINYINT     NOT NULL DEFAULT 0 COMMENT '0-未删除, 1-已删除',
    UNIQUE KEY uk_customer_code (customer_code),
    INDEX idx_status (status),
    INDEX idx_type (type)
) COMMENT '客户';
```

### CustomerMaterial（客户物料关联）

| 列名 | 数据类型 | 必录 | 可编辑 | 默认值 | 约束 | 备注 |
|------|---------|------|--------|--------|------|------|
| id | BIGINT | - | - | AUTO_INCREMENT | PK | |
| customer_id | BIGINT | Y | N | - | FK → customer.id, NOT NULL | 客户ID |
| material_id | BIGINT | Y | N | - | FK → material_master.id, NOT NULL | 物料ID |
| created_by | VARCHAR(50) | - | - | 系统登录人 | - | |
| created_at | DATETIME | - | - | 系统时间 | - | |
| updated_by | VARCHAR(50) | - | - | 系统登录人 | - | |
| updated_at | DATETIME | - | - | 系统时间 | - | |
| deleted | TINYINT | - | - | 0 | NOT NULL | 0=未删除, 1=已删除 |

```sql
CREATE TABLE customer_material (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id   BIGINT NOT NULL COMMENT '客户ID',
    material_id   BIGINT NOT NULL COMMENT '物料ID',
    created_by    VARCHAR(50) DEFAULT NULL,
    created_at    DATETIME DEFAULT NULL,
    updated_by    VARCHAR(50) DEFAULT NULL,
    updated_at    DATETIME DEFAULT NULL,
    deleted       TINYINT NOT NULL DEFAULT 0 COMMENT '0-未删除, 1-已删除',
    UNIQUE KEY uk_customer_material (customer_id, material_id),
    INDEX idx_customer (customer_id),
    INDEX idx_material (material_id),
    CONSTRAINT fk_cm_customer FOREIGN KEY (customer_id) REFERENCES customer(id),
    CONSTRAINT fk_cm_material FOREIGN KEY (material_id) REFERENCES material_master(id)
) COMMENT '客户物料关联';
```

### 设计决策

| 决策 | 原因 |
|------|------|
| customer_code VARCHAR(50) UNIQUE | 业务主键，创建后不可修改，需 DB 级唯一约束 |
| type VARCHAR(50) 存枚举值 | 使用字符串存储枚举值（外贸客户/内贸客户），可读性好，枚举数量少性能影响可忽略 |
| 关联表独立而非 JSON/逗号分隔 | 支持按物料反查客户、引用完整性校验、数据权限过滤 |
| (customer_id, material_id) 联合唯一 | 同一客户不允许重复关联同一物料 |
| material_id FK → material_master | 确保物料引用完整性，级联查询效率高 |
| customer_id FK → customer | 客户删除时关联数据通过软删除自动隔离 |

### 枚举定义

| 枚举 | 值 | 备注 |
|------|---|------|
| CustomerType | 外贸客户, 内贸客户 | 客户类型 |

## 业务逻辑
- **创建**：客户编码、名称必填；校验编码唯一性；创建后状态默认启用
- **编辑**：编码不可修改，可修改名称、类型、简称、联系方式等属性
- **删除**：已被引用的客户不可删除（按钮置灰）
- **启用/禁用**：切换客户状态，已禁用的客户不可被新创建的业务单据引用
- **物料关联**：选择物料时支持多选，关联后可在客户详情中查看已关联的物料列表
- **导入**：唯一性校验，已存在编码不允许再次导入
- **导出**：导出查询结果到 Excel
- **查询**：编码/名称模糊查询，类型/状态下拉筛选
- **日志**：查看变更履历

## 验收标准 (Gherkin)

```gherkin
Feature: 客户管理

  Scenario: 创建客户
    Given 用户在客户管理页面点击创建
    When 填写客户编码 "C001"、客户名称 "测试客户"
    And 点击确认
    Then 客户创建成功，状态为启用

  Scenario: 客户编码唯一性校验
    Given 已存在客户编码 "C001"
    When 创建客户时输入编码 "C001"
    Then 提示客户编码已存在

  Scenario: 编辑客户
    Given 已存在客户 "C001"
    When 修改客户名称为 "新名称"
    Then 客户名称更新成功，客户编码不可修改

  Scenario: 删除被引用的客户
    Given 客户 "C001" 已被其他业务引用
    Then 删除按钮置灰，不可点击

  Scenario: 客户关联物料
    Given 客户 "C001" 已创建
    When 选择物料 "MAT001"
    Then 客户与物料关联成功

  Scenario: 客户重复关联物料
    Given 客户 "C001" 已关联物料 "MAT001"
    When 再次关联物料 "MAT001"
    Then 提示物料已关联，不可重复添加

  Scenario: 查询客户
    Given 存在多个客户记录
    When 按编码模糊查询 "C00"
    Then 返回编码包含 "C00" 的客户列表

  Scenario: 导入客户（编码重复）
    Given 已存在客户编码 "C001"
    When 导入包含编码 "C001" 的 Excel
    Then 提示编码已存在，拒绝导入

  Scenario: 禁用客户
    Given 客户 "C001" 状态为启用
    When 点击禁用
    Then 客户状态变为禁用，不可被新业务单据引用
```
