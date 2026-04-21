# Feature: feat-material-category 物料分类

## 基本信息
- **ID**: feat-material-category
- **名称**: 物料分类
- **优先级**: 83
- **规模**: S
- **依赖**: feat-enterprise-org
- **父模块**: feat-material-master
- **创建时间**: 2026-04-21

## 需求来源
- [物料主数据功能设计 V1.0](../../docx/物料主数据_功能设计_V1.0/物料主数据_功能设计_V1.0.md) — 第5节 物料分类

## 需求描述
管理物料的分类体系，支持多级树形分类结构，为物料信息提供分类基础。支持标记是否为质量分类，用于质量管理场景。

### 子功能
| 子功能 | 核心实体 | 关键规则 |
|--------|---------|---------|
| 物料分类管理 | `MaterialCategory` | 多级分类树形结构，编码/名称唯一，支持质量分类标记 |

## 数据模型

### MaterialCategory（物料分类）
| 字段 | 类型 | 必录 | 编辑 | 备注 |
|------|------|------|------|------|
| 物料分类编码 | String(50) | Y | N | 唯一，创建后不可修改 |
| 物料分类名称 | String(50) | Y | Y | 唯一 |
| 是否质量分类 | Boolean | N | Y | 默认否，是/否 |
| 父分类ID | Long | N | Y | 多级树形结构 |
| 状态 | Boolean | N | N | 默认启用，启用/禁用 |
| 创建人 | String | N | N | 系统登录人 |
| 创建时间 | DateTime | N | N | yyyy/MM/dd HH:mm:ss |
| 修改人 | String | N | N | 系统登录人 |
| 修改时间 | DateTime | N | N | yyyy/MM/dd HH:mm:ss |

## 用户价值点

1. **结构化物料分类体系** — 通过多级树形分类组织物料（如：原材料→板材→FR-4），快速定位和管理物料
2. **质量分类标记** — 标记质量相关分类，为来料检验、质量追溯等场景提供分类维度筛选
3. **分类引用安全** — 已被物料信息引用的分类不可删除，保障分类体系稳定性
4. **批量维护与迁移** — 支持 Excel 导入导出，快速初始化分类体系

## 上下文分析

### 关联 Feature
- **feat-enterprise-org**（前置）— 提供审计基类 BaseEntity/SoftDeleteEntity 模式、全局异常处理
- **feat-material-info**（下游）— MaterialMaster 通过 category_id 引用 MaterialCategory 表
- **feat-inspection-exemption**（间接下游）— 通过物料信息间接引用分类用于筛选

### 参考代码模式
- `com.litemes.domain.entity.SoftDeleteEntity` — 软删除基类
- 企业管理模块 `Department` — 类似的树形结构（parent_id 自引用），可复用树形查询模式
- 企业管理模块 `feat-department` — 左侧树形 + 右侧列表的 UI 模式可复用

## 数据库模型设计

### 基类继承

| 实体 | 基类 | 审计字段 | 软删除 |
|------|------|---------|--------|
| MaterialCategory | SoftDeleteEntity | ✓ | ✓ |

### MaterialCategory（物料分类）

| 列名 | 数据类型 | 必录 | 可编辑 | 默认值 | 约束 | 备注 |
|------|---------|------|--------|--------|------|------|
| id | BIGINT | - | - | AUTO_INCREMENT | PK | |
| category_code | VARCHAR(50) | Y | N | - | UNIQUE, NOT NULL | 分类编码，创建后不可修改 |
| category_name | VARCHAR(50) | Y | Y | - | UNIQUE, NOT NULL | 分类名称 |
| is_quality_category | TINYINT | N | Y | 0 | NOT NULL | 是否质量分类 0=否, 1=是 |
| parent_id | BIGINT | N | Y | NULL | FK → material_category.id | 父分类ID，NULL 表示顶级分类 |
| status | TINYINT | N | N | 1 | NOT NULL | 1=启用, 0=禁用 |
| created_by | VARCHAR(50) | - | - | 系统登录人 | - | |
| created_at | DATETIME | - | - | 系统时间 | - | yyyy/MM/dd HH:mm:ss |
| updated_by | VARCHAR(50) | - | - | 系统登录人 | - | |
| updated_at | DATETIME | - | - | 系统时间 | - | yyyy/MM/dd HH:mm:ss |
| deleted | TINYINT | - | - | 0 | NOT NULL | 0=未删除, 1=已删除 |

```sql
CREATE TABLE material_category (
    id                  BIGINT      AUTO_INCREMENT PRIMARY KEY,
    category_code       VARCHAR(50) NOT NULL,
    category_name       VARCHAR(50) NOT NULL,
    is_quality_category TINYINT     NOT NULL DEFAULT 0 COMMENT '是否质量分类 0-否, 1-是',
    parent_id           BIGINT      DEFAULT NULL COMMENT '父分类ID',
    status              TINYINT     NOT NULL DEFAULT 1 COMMENT '1-启用, 0-禁用',
    created_by          VARCHAR(50) DEFAULT NULL,
    created_at          DATETIME    DEFAULT NULL,
    updated_by          VARCHAR(50) DEFAULT NULL,
    updated_at          DATETIME    DEFAULT NULL,
    deleted             TINYINT     NOT NULL DEFAULT 0 COMMENT '0-未删除, 1-已删除',
    UNIQUE KEY uk_category_code (category_code),
    UNIQUE KEY uk_category_name (category_name),
    INDEX idx_parent (parent_id),
    CONSTRAINT fk_category_parent FOREIGN KEY (parent_id) REFERENCES material_category(id)
) COMMENT '物料分类';
```

### 设计决策

| 决策 | 原因 |
|------|------|
| parent_id 自引用实现树形 | 与 Department 表一致的设计模式，支持无限层级 |
| 编码和名称分别唯一 | 业务要求编码和名称都不能重复 |
| 软删除 | 分类被物料引用后不可物理删除 |
| is_quality_category 标记 | 为 QMS 系统提供分类维度，后续可扩展更多标记字段 |

## 业务逻辑
- 创建：编码和名称必填，校验编码、名称分别唯一
- 编辑：编码不可修改，仅可修改名称和是否质量分类
- 删除：已被引用的分类不可删除（按钮置灰）
- 启用/禁用：已禁用的分类不可被新创建的物料引用
- 导入：唯一性校验，已存在编码不允许再次导入
- 导出：导出查询结果到 Excel
- 查询：编码/名称模糊查询，状态下拉筛选
- 树形展示：左侧树形结构，右侧显示选中节点的分类详情
- 日志：查看变更履历

## 验收标准 (Gherkin)

### 场景 1: 分类编码唯一性
```gherkin
Given 物料分类编码 "CAT-PCB" 已存在
When 再次创建编码为 "CAT-PCB" 的分类
Then 返回错误 "物料分类编码已存在"
```

### 场景 2: 分类编码不可修改
```gherkin
Given 物料分类 "CAT-PCB" 已存在
When 编辑该分类
Then 分类编码字段为只读，不可修改
```

### 场景 3: 已引用分类不可删除
```gherkin
Given 物料分类 "CAT-PCB" 已被物料信息引用
When 尝试删除该分类
Then 删除按钮置灰，不可点击
```

## Merge Record
- **Completed**: 2026-04-22
- **Branch**: feature/feat-material-category
- **Merge Commit**: ffdd980
- **Archive Tag**: feat-material-category-20260422
- **Conflicts**: none
- **Verification**: warning (core implementation complete, integration tests require MySQL)
- **Stats**: 21 files changed, 2401 insertions, 1 commit
