# Feature: feat-equipment-type 设备类型

## 基本信息
- **ID**: feat-equipment-type
- **名称**: 设备类型
- **优先级**: 73
- **规模**: S
- **依赖**: feat-enterprise-org
- **父模块**: feat-equipment-master
- **创建时间**: 2026-04-21

## 需求来源
- docx/设备主数据_功能设计_V1.0.docx — 第3章 设备类型

## 需求描述
定义设备类型的基础数据，包括设备类型编码、名称和状态管理。设备类型是设备型号的上游分类，为设备型号选择提供下拉数据源。

### 核心实体
- **EquipmentType** — 设备类型

### 字段定义

#### 查询条件
| 字段 | 类型 | 必录 | 备注 |
|------|------|------|------|
| 设备类型编码 | 文本框 | N | 支持模糊查询 |
| 设备类型名称 | 文本框 | N | 支持模糊查询 |
| 状态 | 下拉框 | N | 启用/禁用 |

#### 主表字段
| 字段 | 类型 | 必录 | 编辑 | 备注 |
|------|------|------|------|------|
| 设备类型编码 | 字符串(50) | Y | N | 唯一 |
| 设备类型名称 | 字符串(50) | Y | Y | |
| 状态 | 布尔 | N | N | 默认启用，禁用/启用 |
| 创建人 | 字符串 | N | N | 系统登录人 |
| 创建时间 | 日期 | N | N | yyyy/MM/dd HH:mm:ss |
| 修改人 | 字符串 | N | N | 系统登录人 |
| 修改时间 | 日期 | N | N | yyyy/MM/dd HH:mm:ss |

### 功能逻辑
| 功能 | 逻辑说明 |
|------|---------|
| 创建 | 校验编码和名称必填；校验编码唯一性 |
| 编辑 | 编码不可修改 |
| 删除 | 已被引用的数据，删除按钮置灰 |
| 查询 | 筛选条件查询数据列表 |
| 重置 | 重置筛选条件 |
| 导入 | Excel 导入，编码唯一性校验 |
| 导出 | 导出查询结果到 Excel |
| 日志 | 查看变更履历 |

## 用户价值点

1. **标准化设备分类体系** — 统一定义 PCB 制造中的设备类型（钻孔、测试、压合、电镀等），建立清晰的设备分类标准
2. **设备型号数据源** — 为设备型号管理提供类型下拉数据源，确保型号归属类型的一致性
3. **数据引用安全** — 已被设备型号引用的类型不可删除，保障设备分类体系完整性
4. **批量数据维护** — 支持 Excel 导入导出，快速初始化设备类型基础数据

## 上下文分析

### 关联 Feature
- **feat-enterprise-org**（前置）— 提供审计基类 BaseEntity/SoftDeleteEntity 模式、全局异常处理、通用分页模式
- **feat-equipment-model**（下游）— EquipmentModel 通过 equipment_type_id 引用 EquipmentType，是本 feature 的主要消费者
- **feat-factory**（feat-enterprise-org 子模块）— EquipmentLedger 通过 factory_id 引用 Factory，间接依赖本 feature 的分类体系

### 参考代码模式
- `com.litemes.domain.entity.BaseEntity` — 审计字段基类（created_by, created_at, updated_by, updated_at）
- `com.litemes.domain.entity.SoftDeleteEntity` — 软删除基类（继承 BaseEntity + deleted 字段）
- `com.litemes.web.*Resource` — JAX-RS Resource 模板（分页查询、CRUD、导入导出）
- `com.litemes.application.*Service` — 应用服务层（DTO 转换、事务编排、唯一性校验）
- `com.litemes.infrastructure.persistence.*Mapper` — MyBatis-Plus Mapper 模式

## 数据库模型设计

### 基类继承

| 实体 | 基类 | 审计字段 | 软删除 |
|------|------|---------|--------|
| EquipmentType | SoftDeleteEntity | ✓ | ✓ |

### EquipmentType（设备类型）

| 列名 | 数据类型 | 必录 | 可编辑 | 默认值 | 约束 | 备注 |
|------|---------|------|--------|--------|------|------|
| id | BIGINT | - | - | AUTO_INCREMENT | PK | |
| type_code | VARCHAR(50) | Y | N | - | UNIQUE, NOT NULL | 设备类型编码，创建后不可修改 |
| type_name | VARCHAR(50) | Y | Y | - | NOT NULL | 设备类型名称 |
| status | TINYINT | N | N | 1 | NOT NULL | 1=启用, 0=禁用 |
| created_by | VARCHAR(50) | - | - | 系统登录人 | - | |
| created_at | DATETIME | - | - | 系统时间 | - | yyyy/MM/dd HH:mm:ss |
| updated_by | VARCHAR(50) | - | - | 系统登录人 | - | |
| updated_at | DATETIME | - | - | 系统时间 | - | yyyy/MM/dd HH:mm:ss |
| deleted | TINYINT | - | - | 0 | NOT NULL | 0=未删除, 1=已删除 |

```sql
CREATE TABLE equipment_type (
    id           BIGINT       AUTO_INCREMENT PRIMARY KEY,
    type_code    VARCHAR(50)  NOT NULL,
    type_name    VARCHAR(50)  NOT NULL,
    status       TINYINT      NOT NULL DEFAULT 1 COMMENT '1-启用, 0-禁用',
    created_by   VARCHAR(50)  DEFAULT NULL,
    created_at   DATETIME     DEFAULT NULL,
    updated_by   VARCHAR(50)  DEFAULT NULL,
    updated_at   DATETIME     DEFAULT NULL,
    deleted      TINYINT      NOT NULL DEFAULT 0 COMMENT '0-未删除, 1-已删除',
    UNIQUE KEY uk_type_code (type_code)
) COMMENT '设备类型';
```

### 设计决策

| 决策 | 原因 |
|------|------|
| 软删除而非物理删除 | 设备类型可能被设备型号引用，需保留历史完整性 |
| type_code UNIQUE | 编码全局唯一，作为业务主键标识 |
| 编码创建后不可修改 | 业务主键稳定性要求，被下游型号表引用 |

## 验收标准 (Gherkin)

### 场景 1: 创建设备类型
```gherkin
Given 用户在设备类型管理页面
When 填写设备类型编码 "DRILL" 和名称 "钻孔设备" 并提交
Then 设备类型创建成功
And 编码 "DRILL" 唯一
```

### 场景 2: 编辑设备类型
```gherkin
Given 设备类型 "DRILL" 已存在
When 修改名称为 "钻孔设备(更新)" 并提交
Then 名称更新成功
And 编码 "DRILL" 不可修改
```

### 场景 3: 删除约束
```gherkin
Given 设备类型 "DRILL" 已被设备型号引用
When 用户尝试删除该类型
Then 删除按钮置灰，不可点击
```

### 场景 4: 编码唯一性校验
```gherkin
Given 设备类型编码 "DRILL" 已存在
When 再次创建编码为 "DRILL" 的设备类型
Then 返回错误 "设备类型编码已存在"
```

### 场景 5: Excel 批量导入
```gherkin
Given 用户上传包含设备类型数据的 Excel 文件
When 文件中有 5 条有效数据，其中 1 条编码已存在
Then 4 条导入成功，1 条返回编码重复错误
And 返回导入结果报告
```

## Merge Record

- **Completed**: 2026-04-22
- **Merged Branch**: feature/feat-equipment-type
- **Merge Commit**: 7a2c213
- **Archive Tag**: feat-equipment-type-20260422
- **Conflicts**: Yes - router/index.ts and schema.sql (resolved: kept both parallel additions)
- **Verification**: PASSED (14/14 tests, 5 Gherkin scenarios: 3 PASS, 1 PARTIAL, 1 DEFERRED)
- **Stats**: 21 files changed, 1687 insertions, 1 commit, ~1 hour duration
