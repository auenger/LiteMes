# Feature: feat-equipment-model 设备型号

## 基本信息
- **ID**: feat-equipment-model
- **名称**: 设备型号
- **优先级**: 72
- **规模**: S
- **依赖**: feat-equipment-type
- **父模块**: feat-equipment-master
- **创建时间**: 2026-04-21

## 需求来源
- docx/设备主数据_功能设计_V1.0.docx — 第4章 设备型号

## 需求描述
定义设备型号的基础数据，关联设备类型。设备型号是设备台账的上游数据，为台账中的设备选型提供数据源。

### 核心实体
- **EquipmentModel** — 设备型号

### 字段定义

#### 查询条件
| 字段 | 类型 | 必录 | 备注 |
|------|------|------|------|
| 设备型号编码 | 文本框 | N | 支持模糊查询 |
| 设备型号名称 | 文本框 | N | 支持模糊查询 |
| 设备类型 | 下拉框 | N | 取设备类型表数据 |
| 状态 | 下拉框 | N | 启用/禁用 |

#### 主表字段
| 字段 | 类型 | 必录 | 编辑 | 备注 |
|------|------|------|------|------|
| 设备型号编码 | 字符串(50) | Y | N | 唯一 |
| 设备型号名称 | 字符串(50) | Y | Y | |
| 状态 | 布尔 | N | N | 默认启用，禁用/启用 |
| 设备类型编码 | 字符串(50) | Y | Y | 取设备类型表数据 |
| 设备类型名称 | 字符串(50) | Y | Y | 根据类型编码自动带出 |
| 创建人 | 字符串 | N | N | 系统登录人 |
| 创建时间 | 日期 | N | N | yyyy/MM/dd HH:mm:ss |
| 修改人 | 字符串 | N | N | 系统登录人 |
| 修改时间 | 日期 | N | N | yyyy/MM/dd HH:mm:ss |

### 功能逻辑
| 功能 | 逻辑说明 |
|------|---------|
| 创建 | 校验编码和名称必填；校验编码唯一性；选择设备类型 |
| 编辑 | 编码不可修改；可修改名称和设备类型 |
| 删除 | 已被设备台账引用的数据，删除按钮置灰 |
| 查询 | 筛选条件查询数据列表 |
| 重置 | 重置筛选条件 |
| 导入 | Excel 导入，编码唯一性校验 |
| 导出 | 导出查询结果到 Excel |
| 日志 | 查看变更履历 |

## 用户价值点

1. **规范化设备型号管理** — 为每种设备类型下的具体型号建立标准档案，统一设备选型依据
2. **类型-型号层级联动** — 通过设备类型筛选快速定位型号，提升设备台账录入效率
3. **数据引用安全** — 已被设备台账引用的型号不可删除，保障设备档案完整性
4. **批量数据维护** — 支持 Excel 导入导出，快速初始化型号数据

## 上下文分析

### 关联 Feature
- **feat-equipment-type**（前置）— EquipmentModel 通过 equipment_type_id 引用 EquipmentType，设备类型下拉数据源来自本 feature
- **feat-equipment-ledger**（下游）— EquipmentLedger 通过 equipment_model_id 引用 EquipmentModel，是本 feature 的主要消费者
- **feat-enterprise-org**（前置）— 提供审计基类、全局异常处理、通用分页模式

### 参考代码模式
- `com.litemes.domain.entity.BaseEntity` — 审计字段基类
- `com.litemes.domain.entity.SoftDeleteEntity` — 软删除基类
- `com.litemes.web.*Resource` — JAX-RS Resource 模板（分页查询、CRUD、导入导出）
- `com.litemes.application.*Service` — 应用服务层（DTO 转换、事务编排、唯一性校验）
- `com.litemes.infrastructure.persistence.*Mapper` — MyBatis-Plus Mapper 模式
- feat-equipment-type 的 EquipmentType 实体 — 本 feature 的 FK 引用目标

## 数据库模型设计

### 基类继承

| 实体 | 基类 | 审计字段 | 软删除 |
|------|------|---------|--------|
| EquipmentModel | SoftDeleteEntity | ✓ | ✓ |

### EquipmentModel（设备型号）

| 列名 | 数据类型 | 必录 | 可编辑 | 默认值 | 约束 | 备注 |
|------|---------|------|--------|--------|------|------|
| id | BIGINT | - | - | AUTO_INCREMENT | PK | |
| model_code | VARCHAR(50) | Y | N | - | UNIQUE, NOT NULL | 设备型号编码，创建后不可修改 |
| model_name | VARCHAR(50) | Y | Y | - | NOT NULL | 设备型号名称 |
| equipment_type_id | BIGINT | Y | Y | - | FK → equipment_type.id, NOT NULL | 设备类型ID |
| type_code | VARCHAR(50) | Y | N | - | NOT NULL | 设备类型编码（冗余，用于展示） |
| type_name | VARCHAR(50) | Y | N | - | NOT NULL | 设备类型名称（冗余，用于展示） |
| status | TINYINT | N | N | 1 | NOT NULL | 1=启用, 0=禁用 |
| created_by | VARCHAR(50) | - | - | 系统登录人 | - | |
| created_at | DATETIME | - | - | 系统时间 | - | yyyy/MM/dd HH:mm:ss |
| updated_by | VARCHAR(50) | - | - | 系统登录人 | - | |
| updated_at | DATETIME | - | - | 系统时间 | - | yyyy/MM/dd HH:mm:ss |
| deleted | TINYINT | - | - | 0 | NOT NULL | 0=未删除, 1=已删除 |

```sql
CREATE TABLE equipment_model (
    id                  BIGINT       AUTO_INCREMENT PRIMARY KEY,
    model_code          VARCHAR(50)  NOT NULL,
    model_name          VARCHAR(50)  NOT NULL,
    equipment_type_id   BIGINT       NOT NULL COMMENT '设备类型ID',
    type_code           VARCHAR(50)  NOT NULL COMMENT '设备类型编码(冗余)',
    type_name           VARCHAR(50)  NOT NULL COMMENT '设备类型名称(冗余)',
    status              TINYINT      NOT NULL DEFAULT 1 COMMENT '1-启用, 0-禁用',
    created_by          VARCHAR(50)  DEFAULT NULL,
    created_at          DATETIME     DEFAULT NULL,
    updated_by          VARCHAR(50)  DEFAULT NULL,
    updated_at          DATETIME     DEFAULT NULL,
    deleted             TINYINT      NOT NULL DEFAULT 0 COMMENT '0-未删除, 1-已删除',
    UNIQUE KEY uk_model_code (model_code),
    INDEX idx_equipment_type (equipment_type_id),
    CONSTRAINT fk_model_type FOREIGN KEY (equipment_type_id) REFERENCES equipment_type(id)
) COMMENT '设备型号';
```

### 设计决策

| 决策 | 原因 |
|------|------|
| 冗余 type_code/type_name | 列表展示避免 JOIN 查询；设备类型编码不可变，冗余数据一致性好 |
| equipment_type_id FK | 确保型号归属有效设备类型，数据库级引用完整性 |
| 软删除而非物理删除 | 型号可能被设备台账引用，需保留历史完整性 |
| model_code UNIQUE | 编码全局唯一，作为业务主键 |

## 验收标准 (Gherkin)

### 场景 1: 创建设备型号
```gherkin
Given 设备类型 "DRILL" 已存在
When 创建设备型号编码 "CNC-500"，名称 "CNC钻孔机"，选择类型 "DRILL"
Then 型号创建成功
And 编码 "CNC-500" 唯一
And 设备类型名称自动带出
```

### 场景 2: 编辑设备型号
```gherkin
Given 设备型号 "CNC-500" 已存在
When 修改名称为 "CNC钻孔机-Pro" 并更换设备类型
Then 更新成功
And 编码 "CNC-500" 不可修改
```

### 场景 3: 删除约束
```gherkin
Given 设备型号 "CNC-500" 已被设备台账引用
When 用户尝试删除该型号
Then 删除按钮置灰，不可点击
```

### 场景 4: 编码唯一性校验
```gherkin
Given 设备型号编码 "CNC-500" 已存在
When 再次创建编码为 "CNC-500" 的设备型号
Then 返回错误 "设备型号编码已存在"
```

### 场景 5: 设备类型下拉联动
```gherkin
Given 设备类型列表中有 "DRILL", "TEST", "PRESS" 三个启用类型
When 用户在设备型号创建页面打开设备类型下拉框
Then 下拉框显示三个选项
And 选择 "DRILL" 后，设备类型名称自动填充为 "钻孔设备"
```

### 场景 6: Excel 批量导入
```gherkin
Given 用户上传包含设备型号数据的 Excel 文件
When 文件中有 10 条有效数据，其中 2 条编码已存在
Then 8 条导入成功，2 条返回编码重复错误
And 返回导入结果报告
```
