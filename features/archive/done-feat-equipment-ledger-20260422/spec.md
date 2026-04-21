# Feature: feat-equipment-ledger 设备台账

## 基本信息
- **ID**: feat-equipment-ledger
- **名称**: 设备台账
- **优先级**: 71
- **规模**: M
- **依赖**: feat-equipment-model
- **父模块**: feat-equipment-master
- **创建时间**: 2026-04-21

## 需求来源
- docx/设备主数据_功能设计_V1.0.docx — 第5章 设备台账

## 需求描述
管理设备台账信息，是设备主数据的核心业务实体。每台设备拥有唯一编码，关联设备型号（含类型），归属工厂，并维护运行状态和管理状态。设备台账为生产调度、设备维护、质量追溯提供设备数据基础。

### 核心实体
- **EquipmentLedger** — 设备台账

### 字段定义

#### 查询条件
| 字段 | 类型 | 必录 | 备注 |
|------|------|------|------|
| 设备编码 | 文本框 | N | 支持模糊查询 |
| 设备名称 | 文本框 | N | 支持模糊查询 |
| 设备类型 | 下拉框 | N | 取设备类型数据 |
| 设备型号 | 下拉框 | N | 取设备型号数据 |
| 运行状态 | 下拉框 | N | 运行/故障/停机/维修保养 |
| 管理状态 | 下拉框 | N | 使用中/闲置/报废 |
| 工厂 | 下拉框 | N | 取工厂数据 |

#### 主表字段
| 字段 | 类型 | 必录 | 编辑 | 备注 |
|------|------|------|------|------|
| 设备编码 | 字符串(50) | Y | N | 唯一 |
| 设备名称 | 字符串(50) | Y | Y | |
| 状态 | 布尔 | N | N | 默认启用，禁用/启用 |
| 设备类型编码 | 字符串(50) | Y | Y | 根据设备型号自动带出 |
| 设备类型名称 | 字符串(50) | Y | N | 根据类型编码自动带出 |
| 设备型号编码 | 字符串(50) | Y | Y | 取设备型号表数据 |
| 设备型号名称 | 字符串(50) | Y | Y | 取设备型号表数据 |
| 运行状态 | 下拉 | Y | Y | 运行/故障/停机/维修保养 |
| 管理状态 | 下拉 | Y | Y | 使用中/闲置/报废 |
| 工厂编码 | 字符串(50) | Y | Y | 取工厂表数据 |
| 工厂名称 | 字符串(50) | Y | Y | 根据工厂编码自动带出 |
| 生产厂家 | 字符串(50) | N | Y | |
| 入场时间 | 日期 | Y | Y | |
| 创建人 | 字符串 | N | N | 系统登录人 |
| 创建时间 | 日期 | N | N | yyyy/MM/dd HH:mm:ss |
| 修改人 | 字符串 | N | N | 系统登录人 |
| 修改时间 | 日期 | N | N | yyyy/MM/dd HH:mm:ss |

### 功能逻辑
| 功能 | 逻辑说明 |
|------|---------|
| 创建 | 校验编码、名称、型号、运行状态、管理状态、工厂、入场日期必填；校验编码唯一性 |
| 编辑 | 设备编码和设备类型名称不可修改 |
| 删除 | 已被引用的数据，删除按钮置灰 |
| 查询 | 筛选条件查询数据列表 |
| 重置 | 重置筛选条件 |
| 导入 | Excel 导入，编码唯一性校验 |
| 导出 | 导出查询结果到 Excel |
| 日志 | 查看设备变更履历 |

### 枚举定义
- **运行状态**: 运行 / 故障 / 停机 / 维修保养
- **管理状态**: 使用中 / 闲置 / 报废

## 用户价值点

1. **设备资产全生命周期管理** — 从入场到报废，统一记录每台设备的编码、型号、归属工厂、生产厂家等完整档案信息
2. **设备状态实时管控** — 通过运行状态（运行/故障/停机/维修保养）和管理状态（使用中/闲置/报废）双维度跟踪设备当前状况，支撑生产调度和维保计划
3. **多维度快速检索** — 按设备类型、型号、工厂、运行/管理状态等多条件组合筛选，快速定位目标设备
4. **跨模块数据联动** — 与设备类型/型号（上游）、工厂（企业管理模块）自动级联，录入时自动带出关联名称，减少人工输入错误
5. **批量数据维护** — 支持 Excel 导入导出，快速初始化设备台账数据

## 上下文分析

### 关联 Feature
- **feat-equipment-type**（前置）— EquipmentLedger 通过 equipment_type_id 冗余引用 EquipmentType，类型信息随型号自动带出
- **feat-equipment-model**（前置）— EquipmentLedger 通过 equipment_model_id 引用 EquipmentModel，是设备选型的直接上游
- **feat-enterprise-org/feat-factory**（前置）— EquipmentLedger 通过 factory_id 引用 Factory，设备归属工厂
- **feat-supply-chain**（下游）— 供应商管理中的供应商可能与设备台账的生产厂家关联（未来扩展）
- **feat-data-permission**（下游）— 设备台账可能纳入数据权限控制范围（按工厂隔离）

### 参考代码模式
- `com.litemes.domain.entity.BaseEntity` — 审计字段基类
- `com.litemes.domain.entity.SoftDeleteEntity` — 软删除基类
- `com.litemes.web.*Resource` — JAX-RS Resource 模板（分页查询、CRUD、导入导出）
- `com.litemes.application.*Service` — 应用服务层（DTO 转换、事务编排、唯一性校验）
- `com.litemes.infrastructure.persistence.*Mapper` — MyBatis-Plus Mapper 模式
- feat-equipment-model 的 EquipmentModel 实体 — 本 feature 的 FK 引用目标
- feat-factory 的 Factory 实体 — 本 feature 的跨模块 FK 引用目标

## 数据库模型设计

### 基类继承

| 实体 | 基类 | 审计字段 | 软删除 |
|------|------|---------|--------|
| EquipmentLedger | SoftDeleteEntity | ✓ | ✓ |

### 枚举类型

#### RunningStatus（运行状态）
| 值 | 含义 |
|----|------|
| RUNNING | 运行 |
| FAULT | 故障 |
| SHUTDOWN | 停机 |
| MAINTENANCE | 维修保养 |

#### ManageStatus（管理状态）
| 值 | 含义 |
|----|------|
| IN_USE | 使用中 |
| IDLE | 闲置 |
| SCRAPPED | 报废 |

### EquipmentLedger（设备台账）

| 列名 | 数据类型 | 必录 | 可编辑 | 默认值 | 约束 | 备注 |
|------|---------|------|--------|--------|------|------|
| id | BIGINT | - | - | AUTO_INCREMENT | PK | |
| equipment_code | VARCHAR(50) | Y | N | - | UNIQUE, NOT NULL | 设备编码，创建后不可修改 |
| equipment_name | VARCHAR(50) | Y | Y | - | NOT NULL | 设备名称 |
| equipment_model_id | BIGINT | Y | Y | - | FK → equipment_model.id, NOT NULL | 设备型号ID |
| model_code | VARCHAR(50) | Y | N | - | NOT NULL | 设备型号编码（冗余，用于展示） |
| model_name | VARCHAR(50) | Y | N | - | NOT NULL | 设备型号名称（冗余，用于展示） |
| equipment_type_id | BIGINT | Y | N | - | NOT NULL | 设备类型ID（冗余，随型号自动带出） |
| type_code | VARCHAR(50) | Y | N | - | NOT NULL | 设备类型编码（冗余，用于展示） |
| type_name | VARCHAR(50) | Y | N | - | NOT NULL | 设备类型名称（冗余，用于展示，不可修改） |
| running_status | VARCHAR(20) | Y | Y | 'SHUTDOWN' | NOT NULL | 运行状态枚举 |
| manage_status | VARCHAR(20) | Y | Y | 'IN_USE' | NOT NULL | 管理状态枚举 |
| factory_id | BIGINT | Y | Y | - | FK → factory.id, NOT NULL | 工厂ID |
| factory_code | VARCHAR(50) | Y | N | - | NOT NULL | 工厂编码（冗余，用于展示） |
| factory_name | VARCHAR(50) | Y | N | - | NOT NULL | 工厂名称（冗余，用于展示） |
| manufacturer | VARCHAR(100) | N | Y | NULL | - | 生产厂家 |
| commissioning_date | DATE | Y | Y | - | NOT NULL | 入场时间 |
| status | TINYINT | N | N | 1 | NOT NULL | 1=启用, 0=禁用 |
| created_by | VARCHAR(50) | - | - | 系统登录人 | - | |
| created_at | DATETIME | - | - | 系统时间 | - | yyyy/MM/dd HH:mm:ss |
| updated_by | VARCHAR(50) | - | - | 系统登录人 | - | |
| updated_at | DATETIME | - | - | 系统时间 | - | yyyy/MM/dd HH:mm:ss |
| deleted | TINYINT | - | - | 0 | NOT NULL | 0=未删除, 1=已删除 |

```sql
CREATE TABLE equipment_ledger (
    id                    BIGINT        AUTO_INCREMENT PRIMARY KEY,
    equipment_code        VARCHAR(50)   NOT NULL,
    equipment_name        VARCHAR(50)   NOT NULL,
    equipment_model_id    BIGINT        NOT NULL COMMENT '设备型号ID',
    model_code            VARCHAR(50)   NOT NULL COMMENT '设备型号编码(冗余)',
    model_name            VARCHAR(50)   NOT NULL COMMENT '设备型号名称(冗余)',
    equipment_type_id     BIGINT        NOT NULL COMMENT '设备类型ID(冗余,随型号带出)',
    type_code             VARCHAR(50)   NOT NULL COMMENT '设备类型编码(冗余)',
    type_name             VARCHAR(50)   NOT NULL COMMENT '设备类型名称(冗余)',
    running_status        VARCHAR(20)   NOT NULL DEFAULT 'SHUTDOWN' COMMENT '运行状态: RUNNING/FAULT/SHUTDOWN/MAINTENANCE',
    manage_status         VARCHAR(20)   NOT NULL DEFAULT 'IN_USE' COMMENT '管理状态: IN_USE/IDLE/SCRAPPED',
    factory_id            BIGINT        NOT NULL COMMENT '工厂ID',
    factory_code          VARCHAR(50)   NOT NULL COMMENT '工厂编码(冗余)',
    factory_name          VARCHAR(50)   NOT NULL COMMENT '工厂名称(冗余)',
    manufacturer          VARCHAR(100)  DEFAULT NULL COMMENT '生产厂家',
    commissioning_date    DATE          NOT NULL COMMENT '入场时间',
    status                TINYINT       NOT NULL DEFAULT 1 COMMENT '1-启用, 0-禁用',
    created_by            VARCHAR(50)   DEFAULT NULL,
    created_at            DATETIME      DEFAULT NULL,
    updated_by            VARCHAR(50)   DEFAULT NULL,
    updated_at            DATETIME      DEFAULT NULL,
    deleted               TINYINT       NOT NULL DEFAULT 0 COMMENT '0-未删除, 1-已删除',
    UNIQUE KEY uk_equipment_code (equipment_code),
    INDEX idx_equipment_model (equipment_model_id),
    INDEX idx_equipment_type (equipment_type_id),
    INDEX idx_factory (factory_id),
    INDEX idx_running_status (running_status),
    INDEX idx_manage_status (manage_status),
    CONSTRAINT fk_ledger_model FOREIGN KEY (equipment_model_id) REFERENCES equipment_model(id),
    CONSTRAINT fk_ledger_factory FOREIGN KEY (factory_id) REFERENCES factory(id)
) COMMENT '设备台账';
```

### 设计决策

| 决策 | 原因 |
|------|------|
| 冗余 model_code/model_name | 列表展示避免 JOIN equipment_model；型号编码不可变，冗余一致性好 |
| 冗余 type_code/type_name/type_id | 型号→类型级联展示；型号更换时自动同步类型冗余字段 |
| 冗余 factory_code/factory_name | 列表展示避免 JOIN factory；工厂编码不可变，冗余一致性好 |
| running_status/manage_status 用 VARCHAR 枚举 | 业务语义清晰，数据库可读性好；Java 端用 enum 映射 |
| 默认 running_status='SHUTDOWN', manage_status='IN_USE' | 新建设备默认停机+使用中，符合设备入场流程 |
| factory_id FK 跨模块 | 设备台账强依赖工厂归属，DB级FK确保数据一致性 |
| 多索引 | 按型号、类型、工厂、运行状态、管理状态均为常用查询条件 |

## 验收标准 (Gherkin)

### 场景 1: 创建设备台账
```gherkin
Given 设备型号 "CNC-500" 已存在且属于类型 "DRILL"
And 工厂 "F001" 已存在
When 创建设备编码 "EQ-001"，名称 "1号钻孔机"，选择型号 "CNC-500"
And 运行状态 "运行"，管理状态 "使用中"，工厂 "F001"，入场日期 "2025-01-01"
Then 设备台账创建成功
And 设备编码 "EQ-001" 唯一
And 设备类型根据型号自动带出为 "DRILL/钻孔设备"
And 工厂名称根据编码自动带出
```

### 场景 2: 编辑设备台账
```gherkin
Given 设备 "EQ-001" 已存在
When 修改运行状态为 "故障"，管理状态为 "维修保养"
Then 更新成功
And 设备编码 "EQ-001" 不可修改
And 设备类型名称不可修改
```

### 场景 3: 删除约束
```gherkin
Given 设备 "EQ-001" 已被其他业务引用
When 用户尝试删除
Then 删除按钮置灰，不可点击
```

### 场景 4: 编码唯一性校验
```gherkin
Given 设备编码 "EQ-001" 已存在
When 再次创建编码为 "EQ-001" 的设备台账
Then 返回错误 "设备编码已存在"
```

### 场景 5: 型号-类型级联联动
```gherkin
Given 设备型号 "CNC-500" 属于类型 "DRILL"
When 用户在设备台账创建页面选择型号 "CNC-500"
Then 设备类型自动填充为 "DRILL/钻孔设备"
And 设备类型字段为只读，不可手动修改
```

### 场景 6: 工厂下拉联动
```gherkin
Given 工厂列表中有 "F001-总厂", "F002-分厂"
When 用户在设备台账创建页面选择工厂 "F001"
Then 工厂名称自动填充为 "总厂"
```

### 场景 7: Excel 批量导入
```gherkin
Given 用户上传包含设备台账数据的 Excel 文件
When 文件中有 20 条有效数据，其中 3 条编码已存在，2 条型号编码不存在
Then 15 条导入成功，5 条返回错误（3 条编码重复 + 2 条型号不存在）
And 返回导入结果报告
```

## Merge Record

- **Completed**: 2026-04-22
- **Branch**: feature/feat-equipment-ledger
- **Merge Commit**: a7b3296
- **Archive Tag**: feat-equipment-ledger-20260422
- **Conflicts**: Yes (frontend/src/router/index.ts, src/main/resources/db/schema.sql) - resolved by keeping both parallel additions
- **Verification**: passed (20/20 tests, 6/7 Gherkin scenarios, Excel import/export deferred)
- **Stats**: 1 commit, 23 files changed, 3472 insertions
