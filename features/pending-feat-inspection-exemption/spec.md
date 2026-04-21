# Feature: feat-inspection-exemption 免检清单

## 基本信息
- **ID**: feat-inspection-exemption
- **名称**: 免检清单
- **优先级**: 81
- **规模**: S
- **依赖**: feat-material-info
- **父模块**: feat-material-master
- **创建时间**: 2026-04-21

## 需求来源
- [物料主数据功能设计 V1.0](../../docx/物料主数据_功能设计_V1.0/物料主数据_功能设计_V1.0.md) — 第7节 免检清单

## 需求描述
定义物料免检规则，通过供应商+物料+有效时间范围组合来确定哪些物料可以免于来料检验，减少不必要的检验流程。

### 子功能
| 子功能 | 核心实体 | 关键规则 |
|--------|---------|---------|
| 免检清单管理 | `InspectionExemption` | 供应商+物料+有效期定义免检规则，支持永久免检 |

## 数据模型

### InspectionExemption（免检清单）
| 字段 | 类型 | 必录 | 编辑 | 备注 |
|------|------|------|------|------|
| 物料编码 | String(50) | Y | N | 取【物料信息】表数据 |
| 物料名称 | String(50) | Y | Y | 根据物料编码自动带出 |
| 供应商编码 | String(50) | N | Y | 取【供应商】表数据 |
| 供应商名称 | String(50) | N | Y | 根据供应商编码自动带出 |
| 状态 | Boolean | N | N | 默认启用，启用/禁用 |
| 有效开始日期 | Date | N | Y | |
| 有效结束日期 | Date | N | Y | |
| 创建人 | String | N | N | 系统登录人 |
| 创建时间 | DateTime | N | N | yyyy/MM/dd HH:mm:ss |
| 修改人 | String | N | N | 系统登录人 |
| 修改时间 | DateTime | N | N | yyyy/MM/dd HH:mm:ss |

## 用户价值点

1. **免检规则灵活配置** — 通过供应商+物料+有效期三个维度组合定义免检规则，覆盖「指定供应商免检」「全局永久免检」等多种业务场景
2. **减少不必要检验** — 合格供应商的物料自动跳过来料检验环节，缩短入库周期、降低检验成本
3. **有效期自动管控** — 超过有效结束日期的免检规则自动失效，无需人工干预，保障质量管控时效性
4. **批量维护** — 支持 Excel 导入导出，快速配置免检规则

## 上下文分析

### 关联 Feature
- **feat-material-info**（前置）— 提供物料主数据，InspectionExemption 通过 material_id 引用 MaterialMaster 表
- **feat-supplier**（外部依赖，尚未开发）— 提供供应商数据，InspectionExemption 通过 supplier_id 引用供应商表
- **feat-uom**（间接依赖）— 通过物料信息间接关联单位数据

### 跨模块引用处理

**供应商表（Supplier）在 feat-supplier 模块中定义，而 feat-supplier 依赖 feat-material-master（本模块），形成依赖链：**

```
feat-material-master → feat-inspection-exemption → (引用) → feat-supplier → (依赖) → feat-material-master
```

**处理策略：**
- supplier_id 设为 **nullable**，不设 DB 级 FOREIGN KEY 约束
- 在 Application 层做逻辑校验：如果 supplier_id 非空，通过 API 检查供应商是否存在
- feat-supplier 完成后，可选择性添加 DB 级 FK 约束

### 参考代码模式
- `com.litemes.domain.entity.SoftDeleteEntity` — 软删除基类
- `com.litemes.application.*Service` — 应用服务层（DTO 转换、事务编排）
- Quarkus Scheduler — 可用于定时扫描过期免检规则并自动失效

## 数据库模型设计

### 基类继承

| 实体 | 基类 | 审计字段 | 软删除 |
|------|------|---------|--------|
| InspectionExemption | SoftDeleteEntity | ✓ | ✓ |

### InspectionExemption（免检清单）

| 列名 | 数据类型 | 必录 | 可编辑 | 默认值 | 约束 | 备注 |
|------|---------|------|--------|--------|------|------|
| id | BIGINT | - | - | AUTO_INCREMENT | PK | |
| material_id | BIGINT | Y | Y | - | FK → material_master.id, NOT NULL | 物料ID |
| material_code | VARCHAR(50) | Y | N | - | NOT NULL | 物料编码（冗余，用于展示） |
| material_name | VARCHAR(255) | Y | Y | - | NOT NULL | 物料名称（冗余，自动带出） |
| supplier_id | BIGINT | N | Y | NULL | 无DB级FK，逻辑引用 | 供应商ID（可空） |
| supplier_code | VARCHAR(50) | N | Y | NULL | - | 供应商编码（冗余，可空） |
| supplier_name | VARCHAR(50) | N | Y | NULL | - | 供应商名称（冗余，可空） |
| status | TINYINT | N | N | 1 | NOT NULL | 1=启用, 0=禁用 |
| valid_from | DATE | N | Y | NULL | - | 有效开始日期 |
| valid_to | DATE | N | Y | NULL | - | 有效结束日期 |
| created_by | VARCHAR(50) | - | - | 系统登录人 | - | |
| created_at | DATETIME | - | - | 系统时间 | - | yyyy/MM/dd HH:mm:ss |
| updated_by | VARCHAR(50) | - | - | 系统登录人 | - | |
| updated_at | DATETIME | - | - | 系统时间 | - | yyyy/MM/dd HH:mm:ss |
| deleted | TINYINT | - | - | 0 | NOT NULL | 0=未删除, 1=已删除 |

```sql
CREATE TABLE inspection_exemption (
    id              BIGINT       AUTO_INCREMENT PRIMARY KEY,
    material_id     BIGINT       NOT NULL COMMENT '物料ID',
    material_code   VARCHAR(50)  NOT NULL COMMENT '物料编码(冗余)',
    material_name   VARCHAR(255) NOT NULL COMMENT '物料名称(冗余)',
    supplier_id     BIGINT       DEFAULT NULL COMMENT '供应商ID(可空, 逻辑引用)',
    supplier_code   VARCHAR(50)  DEFAULT NULL COMMENT '供应商编码(冗余,可空)',
    supplier_name   VARCHAR(50)  DEFAULT NULL COMMENT '供应商名称(冗余,可空)',
    status          TINYINT      NOT NULL DEFAULT 1 COMMENT '1-启用, 0-禁用',
    valid_from      DATE         DEFAULT NULL COMMENT '有效开始日期',
    valid_to        DATE         DEFAULT NULL COMMENT '有效结束日期',
    created_by      VARCHAR(50)  DEFAULT NULL,
    created_at      DATETIME     DEFAULT NULL,
    updated_by      VARCHAR(50)  DEFAULT NULL,
    updated_at      DATETIME     DEFAULT NULL,
    deleted         TINYINT      NOT NULL DEFAULT 0 COMMENT '0-未删除, 1-已删除',
    INDEX idx_material (material_id),
    INDEX idx_supplier (supplier_id),
    INDEX idx_status (status),
    INDEX idx_valid_date (valid_from, valid_to),
    CONSTRAINT fk_exemption_material FOREIGN KEY (material_id) REFERENCES material_master(id)
) COMMENT '免检清单';
```

### 设计决策

| 决策 | 原因 |
|------|------|
| supplier_id 无 DB 级 FK | 供应商表在 feat-supplier 中，尚未创建；避免跨模块物理依赖 |
| supplier 相关字段全部可空 | 业务允许仅指定物料（永久免检），不绑定供应商 |
| 冗余 material_code/name, supplier_code/name | 列表展示避免 JOIN，冗余数据保持只读（自动带出） |
| valid_from + valid_to 双日期索引 | 支持按有效期范围查询和过期扫描 |
| 无唯一约束 | 同一物料+供应商可有多条不同有效期的免检规则 |

### 免检规则业务矩阵

| 供应商 | 有效期 | 含义 | 示例 |
|--------|--------|------|------|
| 空 | 空 | 物料全局永久免检 | 所有供应商的 MAT-001 都免检 |
| 有值 | 空 | 指定供应商永久免检 | 供应商 S001 的 MAT-001 永久免检 |
| 有值 | 有值 | 指定供应商+有效期免检 | S001 的 MAT-001 在 2026 年内免检 |
| 空 | 有值 | 物料全局有效期免检 | 所有供应商的 MAT-001 在有效期内免检 |

## 业务逻辑
- 创建：物料必填，供应商和有效日期允许为空
  - 供应商和有效日期都为空 → 物料永久免检
  - 供应商有值，有效期为空 → 该供应商的此物料永久有效
  - 都有值 → 指定供应商+有效期的免检规则
  - 物料名称根据物料编码自动带出，供应商名称根据供应商编码自动带出
- 编辑：可修改物料、供应商和有效期
- 删除：已被引用的数据不可删除（按钮置灰）
- 启用/禁用：支持手动禁用免检规则
- 导入：唯一性校验导入
- 导出：导出查询结果到 Excel
- 查询：物料/供应商下拉筛选，状态下拉筛选
- 过期规则：超过有效结束日期的规则自动失效（可通过 Quarkus Scheduler 定时扫描，或查询时动态判断）
- 日志：查看变更履历

## 验收标准 (Gherkin)

### 场景 1: 免检规则生效
```gherkin
Given 供应商 S001 和物料 MAT-001 已存在
When 创建免检规则（供应商 S001 + 物料 MAT-001 + 有效期 2026-01-01 至 2026-12-31）
Then 免检规则生效
And 在有效期内，该供应商的此物料免检
```

### 场景 2: 永久免检
```gherkin
Given 物料 MAT-001 已存在
When 创建免检规则（物料 MAT-001，供应商和有效期均为空）
Then 物料 MAT-001 永久免检
```

### 场景 3: 过期自动失效
```gherkin
Given 免检规则的有效结束日期为 2026-03-01
When 当前日期超过 2026-03-01
Then 该免检规则自动失效
```
