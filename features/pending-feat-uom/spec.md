# Feature: feat-uom 计量单位与换算

## 基本信息
- **ID**: feat-uom
- **名称**: 计量单位与换算
- **优先级**: 84
- **规模**: M
- **依赖**: feat-enterprise-org
- **父模块**: feat-material-master
- **创建时间**: 2026-04-21

## 需求来源
- [物料主数据功能设计 V1.0](../../docx/物料主数据_功能设计_V1.0/物料主数据_功能设计_V1.0.md) — 第3节 单位、第4节 单位换算比例

## 需求描述
管理 PCB 制造过程中的计量单位及其换算关系，为物料信息、生产计量等提供统一的单位基础数据。

### 子功能
| 子功能 | 核心实体 | 关键规则 |
|--------|---------|---------|
| 单位管理 | `Uom` | 编码与名称唯一，支持精度设置，编码创建后不可修改 |
| 单位换算比例 | `UomConversion` | 原单位+目标单位唯一，支持多级换算 |

## 数据模型

### Uom（计量单位）
| 字段 | 类型 | 必录 | 编辑 | 备注 |
|------|------|------|------|------|
| 单位编码 | String(50) | Y | N | 唯一 |
| 单位名称 | String(50) | Y | Y | 唯一 |
| 状态 | Boolean | N | N | 默认启用，启用/禁用 |
| 精度 | Decimal | N | N | 支持小数 |
| 创建人 | String | N | N | 系统登录人 |
| 创建时间 | DateTime | N | N | yyyy/MM/dd HH:mm:ss |
| 修改人 | String | N | N | 系统登录人 |
| 修改时间 | DateTime | N | N | yyyy/MM/dd HH:mm:ss |

### UomConversion（单位换算比例）
| 字段 | 类型 | 必录 | 编辑 | 备注 |
|------|------|------|------|------|
| 原单位编码 | String(50) | Y | N | 取【单位】表 |
| 原单位名称 | String(50) | Y | N | 取【单位】表 |
| 目标单位编码 | String(50) | Y | N | 取【单位】表 |
| 目标单位名称 | String(50) | Y | N | 取【单位】表 |
| 换算率 | Decimal | Y | Y | 支持小数 |
| 状态 | Boolean | N | N | 默认启用 |
| 创建人 | String | N | N | 系统登录人 |
| 创建时间 | DateTime | N | N | yyyy/MM/dd HH:mm:ss |
| 修改人 | String | N | N | 系统登录人 |
| 修改时间 | DateTime | N | N | yyyy/MM/dd HH:mm:ss |

## 用户价值点

1. **标准化计量体系** — 统一定义 PCB 制造过程中的计量单位（PCS、KG、M 等），避免不同业务环节因单位不一致导致的数据混乱
2. **灵活单位换算** — 配置原单位↔目标单位的换算比例，支撑物料采购（KG→PCS）、生产领用（M→PCS）等跨单位业务场景
3. **数据引用安全** — 已被物料信息引用的单位不可删除，保障基础数据完整性
4. **批量数据维护** — 支持 Excel 导入导出，快速初始化和迁移单位数据

## 上下文分析

### 关联 Feature
- **feat-enterprise-org**（前置）— 提供审计基类 BaseEntity/SoftDeleteEntity 模式、全局异常处理、通用分页模式
- **feat-material-info**（下游）— MaterialMaster 通过 uom_id 引用 Uom 表，是本 feature 的主要消费者
- **feat-equipment-ledger**（下游）— 设备台账可能引用 Uom 用于设备计量

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
| Uom | SoftDeleteEntity | ✓ | ✓ |
| UomConversion | SoftDeleteEntity | ✓ | ✓ |

### Uom（计量单位）

| 列名 | 数据类型 | 必录 | 可编辑 | 默认值 | 约束 | 备注 |
|------|---------|------|--------|--------|------|------|
| id | BIGINT | - | - | AUTO_INCREMENT | PK | |
| uom_code | VARCHAR(50) | Y | N | - | UNIQUE, NOT NULL | 单位编码，创建后不可修改 |
| uom_name | VARCHAR(50) | Y | Y | - | UNIQUE, NOT NULL | 单位名称 |
| status | TINYINT | N | N | 1 | NOT NULL | 1=启用, 0=禁用 |
| precision | DECIMAL(10,4) | N | Y | NULL | - | 计算精度，支持小数 |
| created_by | VARCHAR(50) | - | - | 系统登录人 | - | |
| created_at | DATETIME | - | - | 系统时间 | - | yyyy/MM/dd HH:mm:ss |
| updated_by | VARCHAR(50) | - | - | 系统登录人 | - | |
| updated_at | DATETIME | - | - | 系统时间 | - | yyyy/MM/dd HH:mm:ss |
| deleted | TINYINT | - | - | 0 | NOT NULL | 0=未删除, 1=已删除 |

```sql
CREATE TABLE uom (
    id           BIGINT       AUTO_INCREMENT PRIMARY KEY,
    uom_code     VARCHAR(50)  NOT NULL,
    uom_name     VARCHAR(50)  NOT NULL,
    status       TINYINT      NOT NULL DEFAULT 1 COMMENT '1-启用, 0-禁用',
    precision    DECIMAL(10,4) DEFAULT NULL COMMENT '计算精度',
    created_by   VARCHAR(50)  DEFAULT NULL,
    created_at   DATETIME     DEFAULT NULL,
    updated_by   VARCHAR(50)  DEFAULT NULL,
    updated_at   DATETIME     DEFAULT NULL,
    deleted      TINYINT      NOT NULL DEFAULT 0 COMMENT '0-未删除, 1-已删除',
    UNIQUE KEY uk_uom_code (uom_code),
    UNIQUE KEY uk_uom_name (uom_name)
) COMMENT '计量单位';
```

### UomConversion（单位换算比例）

| 列名 | 数据类型 | 必录 | 可编辑 | 默认值 | 约束 | 备注 |
|------|---------|------|--------|--------|------|------|
| id | BIGINT | - | - | AUTO_INCREMENT | PK | |
| from_uom_id | BIGINT | Y | Y | - | FK → uom.id, NOT NULL | 原单位ID |
| from_uom_code | VARCHAR(50) | Y | N | - | NOT NULL | 原单位编码（冗余，用于展示） |
| from_uom_name | VARCHAR(50) | Y | N | - | NOT NULL | 原单位名称（冗余，用于展示） |
| to_uom_id | BIGINT | Y | Y | - | FK → uom.id, NOT NULL | 目标单位ID |
| to_uom_code | VARCHAR(50) | Y | N | - | NOT NULL | 目标单位编码（冗余，用于展示） |
| to_uom_name | VARCHAR(50) | Y | N | - | NOT NULL | 目标单位名称（冗余，用于展示） |
| conversion_rate | DECIMAL(18,6) | Y | Y | - | NOT NULL | 换算率 |
| status | TINYINT | N | N | 1 | NOT NULL | 1=启用, 0=禁用 |
| created_by | VARCHAR(50) | - | - | 系统登录人 | - | |
| created_at | DATETIME | - | - | 系统时间 | - | yyyy/MM/dd HH:mm:ss |
| updated_by | VARCHAR(50) | - | - | 系统登录人 | - | |
| updated_at | DATETIME | - | - | 系统时间 | - | yyyy/MM/dd HH:mm:ss |
| deleted | TINYINT | - | - | 0 | NOT NULL | 0=未删除, 1=已删除 |

```sql
CREATE TABLE uom_conversion (
    id              BIGINT        AUTO_INCREMENT PRIMARY KEY,
    from_uom_id     BIGINT        NOT NULL COMMENT '原单位ID',
    from_uom_code   VARCHAR(50)   NOT NULL COMMENT '原单位编码(冗余)',
    from_uom_name   VARCHAR(50)   NOT NULL COMMENT '原单位名称(冗余)',
    to_uom_id       BIGINT        NOT NULL COMMENT '目标单位ID',
    to_uom_code     VARCHAR(50)   NOT NULL COMMENT '目标单位编码(冗余)',
    to_uom_name     VARCHAR(50)   NOT NULL COMMENT '目标单位名称(冗余)',
    conversion_rate DECIMAL(18,6) NOT NULL COMMENT '换算率',
    status          TINYINT       NOT NULL DEFAULT 1 COMMENT '1-启用, 0-禁用',
    created_by      VARCHAR(50)   DEFAULT NULL,
    created_at      DATETIME      DEFAULT NULL,
    updated_by      VARCHAR(50)   DEFAULT NULL,
    updated_at      DATETIME      DEFAULT NULL,
    deleted         TINYINT       NOT NULL DEFAULT 0 COMMENT '0-未删除, 1-已删除',
    UNIQUE KEY uk_from_to (from_uom_id, to_uom_id),
    INDEX idx_from_uom (from_uom_id),
    INDEX idx_to_uom (to_uom_id),
    CONSTRAINT fk_uom_conv_from FOREIGN KEY (from_uom_id) REFERENCES uom(id),
    CONSTRAINT fk_uom_conv_to FOREIGN KEY (to_uom_id) REFERENCES uom(id)
) COMMENT '单位换算比例';
```

### 设计决策

| 决策 | 原因 |
|------|------|
| UomConversion 冗余 code/name | 列表展示避免 JOIN 查询，提升性能；编码不可变，冗余数据一致性好 |
| from_uom_id + to_uom_id 联合唯一 | 同一对单位只允许一条换算记录 |
| 软删除而非物理删除 | 基础数据被业务单据引用，需保留历史完整性 |
| conversion_rate 精度 DECIMAL(18,6) | PCB 计量需高精度，如 1 PCS = 0.000125 KG |

## 业务逻辑
### 单位管理
- 创建：编码和名称必填，校验编码、名称分别唯一
- 编辑：编码不可修改，仅可修改名称和精度
- 删除：已被引用的单位不可删除（按钮置灰）
- 启用/禁用：已禁用的单位不可被新创建的换算比例引用
- 导入：唯一性校验，已存在编码不允许再次导入
- 导出：导出查询结果到 Excel
- 查询：编码/名称支持模糊查询，状态下拉筛选
- 日志：查看变更履历

### 单位换算比例
- 创建：原单位、目标单位、换算率必填，校验原单位+目标单位唯一
- 编辑：可修改原单位、目标单位和换算率
- 删除：已被引用的不可删除
- 查询：原单位/目标单位模糊查询，状态下拉筛选
- 日志：查看变更履历

## 验收标准 (Gherkin)

### 场景 1: 单位编码唯一性
```gherkin
Given 单位编码 "PCS" 已存在
When 再次创建编码为 "PCS" 的单位
Then 返回错误 "单位编码已存在"
```

### 场景 2: 单位编码不可修改
```gherkin
Given 单位 "PCS" 已存在
When 编辑该单位
Then 单位编码字段为只读，不可修改
```

### 场景 3: 换算比例唯一性
```gherkin
Given 原单位 "PCS" 到目标单位 "KG" 的换算比例已存在
When 再次创建相同原单位+目标单位的换算比例
Then 返回错误 "换算比例已存在"
```

### 场景 4: 已引用数据不可删除
```gherkin
Given 单位 "PCS" 已被物料信息引用
When 尝试删除单位 "PCS"
Then 删除按钮置灰，不可点击
```
