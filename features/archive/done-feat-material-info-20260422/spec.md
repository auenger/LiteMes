# Feature: feat-material-info 物料基本信息

## 基本信息
- **ID**: feat-material-info
- **名称**: 物料基本信息
- **优先级**: 82
- **规模**: M
- **依赖**: feat-uom, feat-material-category
- **父模块**: feat-material-master
- **创建时间**: 2026-04-21

## 需求来源
- [物料主数据功能设计 V1.0](../../docx/物料主数据_功能设计_V1.0/物料主数据_功能设计_V1.0.md) — 第6节 物料信息

## 需求描述
管理物料的基础信息，包括编码、名称、分类、单位及 PCB 特有属性（尺寸、型号、规格、厚度、铜厚等），支持版本管理追踪物料规格变更。

### 子功能
| 子功能 | 核心实体 | 关键规则 |
|--------|---------|---------|
| 物料信息管理 | `MaterialMaster` | 编码/名称唯一，编码不可修改，支持版本管理 |
| 物料版本管理 | `MaterialVersion` | 版本号（A.1→A.2），关联到同一物料 |

## 数据模型

### MaterialMaster（物料信息）
| 字段 | 类型 | 必录 | 编辑 | 备注 |
|------|------|------|------|------|
| 物料编码 | String(50) | Y | N | 唯一，创建后不可修改 |
| 物料名称 | String(255) | Y | Y | 列宽显示20个中文长度 |
| 状态 | Boolean | N | N | 默认启用，启用/禁用 |
| 基本分类 | String(50) | Y | Y | 枚举：成品、半成品、原材料、备件 |
| 物料分类ID | Long | Y | Y | 关联【物料分类】表 |
| 属性分类 | String(50) | Y | Y | 枚举：采购件、自制件、采购&自制件 |
| 单位ID | Long | Y | Y | 关联【单位】表 |
| 尺寸 | Decimal | N | Y | 支持小数 |
| 长 | Decimal | N | Y | 支持小数 |
| 宽 | Decimal | N | Y | 支持小数 |
| 型号 | String(50) | N | Y | |
| 规格 | String(50) | N | Y | |
| 厚度 | Decimal | N | Y | 支持小数 |
| 颜色 | String(50) | N | Y | |
| TG值 | String(50) | N | Y | |
| 铜厚 | String(50) | N | Y | |
| 是否含铜 | Boolean | N | Y | 是/否 |
| 直径 | Decimal | N | Y | 支持小数 |
| 刃长 | Decimal | N | Y | 支持小数 |
| 总长 | Decimal | N | Y | 支持小数 |
| 扩展1~5 | String(50) | N | Y | 5个扩展字段 |
| 创建人 | String | N | N | 系统登录人 |
| 创建时间 | DateTime | N | N | yyyy/MM/dd HH:mm:ss |
| 修改人 | String | N | N | 系统登录人 |
| 修改时间 | DateTime | N | N | yyyy/MM/dd HH:mm:ss |

### MaterialVersion（物料版本）
| 字段 | 类型 | 必录 | 备注 |
|------|------|------|------|
| 物料ID | Long | Y | 关联 MaterialMaster |
| 版本号 | String(20) | Y | 如 A.1, A.2 |

## 用户价值点

1. **统一物料信息管理** — 集中维护 PCB 制造所需全部物料的编码、名称、分类、单位及 PCB 特有属性（尺寸、型号、铜厚等），一个入口管理所有物料
2. **PCB 专属属性** — 内置尺寸、长宽、厚度、铜厚、TG值、是否含铜等 PCB 行业专属字段，无需自定义扩展即可覆盖核心业务
3. **版本管理追踪** — 支持物料规格变更追踪（A.1→A.2），确保生产过程使用正确的物料版本
4. **灵活扩展能力** — 预留 5 个扩展字段（ext1~ext5），适配不同客户的个性化属性需求
5. **数据引用安全** — 已被免检清单等业务单据引用的物料不可删除，保障下游业务数据完整性
6. **批量数据维护** — 支持 Excel 导入导出，快速初始化和迁移物料数据

## 上下文分析

### 关联 Feature
- **feat-uom**（前置）— 提供计量单位数据，MaterialMaster 通过 uom_id 引用 Uom 表
- **feat-material-category**（前置）— 提供物料分类体系，MaterialMaster 通过 category_id 引用 MaterialCategory 表
- **feat-inspection-exemption**（下游）— 免检清单通过 material_id 引用 MaterialMaster 表
- **feat-enterprise-org**（前置）— 提供审计基类、全局异常处理、通用分页模式
- **feat-supplier**（外部模块）— 物料信息可被供应商关联，但本 feature 不直接依赖供应商

### 参考代码模式
- `com.litemes.domain.entity.SoftDeleteEntity` — 软删除基类
- 企业管理模块 `Company` — 类似的编码唯一+软删除模式
- 企业管理模块 `Department` — 树形结构查询模式，用于物料分类下拉选择

## 数据库模型设计

### 基类继承

| 实体 | 基类 | 审计字段 | 软删除 |
|------|------|---------|--------|
| MaterialMaster | SoftDeleteEntity | ✓ | ✓ |
| MaterialVersion | SoftDeleteEntity | ✓ | ✓ |

### MaterialMaster（物料信息）

| 列名 | 数据类型 | 必录 | 可编辑 | 默认值 | 约束 | 备注 |
|------|---------|------|--------|--------|------|------|
| id | BIGINT | - | - | AUTO_INCREMENT | PK | |
| material_code | VARCHAR(50) | Y | N | - | UNIQUE, NOT NULL | 物料编码，创建后不可修改 |
| material_name | VARCHAR(255) | Y | Y | - | UNIQUE, NOT NULL | 物料名称，列宽显示20个中文长度 |
| status | TINYINT | N | N | 1 | NOT NULL | 1=启用, 0=禁用 |
| basic_category | VARCHAR(50) | Y | Y | - | NOT NULL | 基本分类枚举：成品/半成品/原材料/备件 |
| category_id | BIGINT | Y | Y | - | FK → material_category.id, NOT NULL | 物料分类ID |
| attribute_category | VARCHAR(50) | Y | Y | - | NOT NULL | 属性分类枚举：采购件/自制件/采购&自制件 |
| uom_id | BIGINT | Y | Y | - | FK → uom.id, NOT NULL | 单位ID |
| size | DECIMAL(18,4) | N | Y | NULL | - | 尺寸 |
| length | DECIMAL(18,4) | N | Y | NULL | - | 长 |
| width | DECIMAL(18,4) | N | Y | NULL | - | 宽 |
| model | VARCHAR(50) | N | Y | NULL | - | 型号 |
| specification | VARCHAR(50) | N | Y | NULL | - | 规格 |
| thickness | DECIMAL(18,4) | N | Y | NULL | - | 厚度 |
| color | VARCHAR(50) | N | Y | NULL | - | 颜色 |
| tg_value | VARCHAR(50) | N | Y | NULL | - | TG值 |
| copper_thickness | VARCHAR(50) | N | Y | NULL | - | 铜厚 |
| is_copper_contained | TINYINT | N | Y | NULL | - | 是否含铜 0=否, 1=是 |
| diameter | DECIMAL(18,4) | N | Y | NULL | - | 直径 |
| blade_length | DECIMAL(18,4) | N | Y | NULL | - | 刃长 |
| total_length | DECIMAL(18,4) | N | Y | NULL | - | 总长 |
| ext1 | VARCHAR(50) | N | Y | NULL | - | 扩展字段1 |
| ext2 | VARCHAR(50) | N | Y | NULL | - | 扩展字段2 |
| ext3 | VARCHAR(50) | N | Y | NULL | - | 扩展字段3 |
| ext4 | VARCHAR(50) | N | Y | NULL | - | 扩展字段4 |
| ext5 | VARCHAR(50) | N | Y | NULL | - | 扩展字段5 |
| created_by | VARCHAR(50) | - | - | 系统登录人 | - | |
| created_at | DATETIME | - | - | 系统时间 | - | yyyy/MM/dd HH:mm:ss |
| updated_by | VARCHAR(50) | - | - | 系统登录人 | - | |
| updated_at | DATETIME | - | - | 系统时间 | - | yyyy/MM/dd HH:mm:ss |
| deleted | TINYINT | - | - | 0 | NOT NULL | 0=未删除, 1=已删除 |

```sql
CREATE TABLE material_master (
    id                  BIGINT        AUTO_INCREMENT PRIMARY KEY,
    material_code       VARCHAR(50)   NOT NULL,
    material_name       VARCHAR(255)  NOT NULL,
    status              TINYINT       NOT NULL DEFAULT 1 COMMENT '1-启用, 0-禁用',
    basic_category      VARCHAR(50)   NOT NULL COMMENT '基本分类: 成品/半成品/原材料/备件',
    category_id         BIGINT        NOT NULL COMMENT '物料分类ID',
    attribute_category  VARCHAR(50)   NOT NULL COMMENT '属性分类: 采购件/自制件/采购&自制件',
    uom_id              BIGINT        NOT NULL COMMENT '单位ID',
    size                DECIMAL(18,4) DEFAULT NULL,
    length              DECIMAL(18,4) DEFAULT NULL,
    width               DECIMAL(18,4) DEFAULT NULL,
    model               VARCHAR(50)   DEFAULT NULL,
    specification       VARCHAR(50)   DEFAULT NULL,
    thickness           DECIMAL(18,4) DEFAULT NULL,
    color               VARCHAR(50)   DEFAULT NULL,
    tg_value            VARCHAR(50)   DEFAULT NULL,
    copper_thickness    VARCHAR(50)   DEFAULT NULL,
    is_copper_contained TINYINT       DEFAULT NULL COMMENT '是否含铜 0-否, 1-是',
    diameter            DECIMAL(18,4) DEFAULT NULL,
    blade_length        DECIMAL(18,4) DEFAULT NULL,
    total_length        DECIMAL(18,4) DEFAULT NULL,
    ext1                VARCHAR(50)   DEFAULT NULL COMMENT '扩展字段1',
    ext2                VARCHAR(50)   DEFAULT NULL COMMENT '扩展字段2',
    ext3                VARCHAR(50)   DEFAULT NULL COMMENT '扩展字段3',
    ext4                VARCHAR(50)   DEFAULT NULL COMMENT '扩展字段4',
    ext5                VARCHAR(50)   DEFAULT NULL COMMENT '扩展字段5',
    created_by          VARCHAR(50)   DEFAULT NULL,
    created_at          DATETIME      DEFAULT NULL,
    updated_by          VARCHAR(50)   DEFAULT NULL,
    updated_at          DATETIME      DEFAULT NULL,
    deleted             TINYINT       NOT NULL DEFAULT 0 COMMENT '0-未删除, 1-已删除',
    UNIQUE KEY uk_material_code (material_code),
    UNIQUE KEY uk_material_name (material_name),
    INDEX idx_category (category_id),
    INDEX idx_uom (uom_id),
    INDEX idx_basic_category (basic_category),
    INDEX idx_status (status),
    CONSTRAINT fk_material_category FOREIGN KEY (category_id) REFERENCES material_category(id),
    CONSTRAINT fk_material_uom FOREIGN KEY (uom_id) REFERENCES uom(id)
) COMMENT '物料信息';
```

### MaterialVersion（物料版本）

| 列名 | 数据类型 | 必录 | 可编辑 | 默认值 | 约束 | 备注 |
|------|---------|------|--------|--------|------|------|
| id | BIGINT | - | - | AUTO_INCREMENT | PK | |
| material_id | BIGINT | Y | N | - | FK → material_master.id, NOT NULL | 物料ID |
| version_no | VARCHAR(20) | Y | N | - | NOT NULL | 版本号，如 A.1, A.2 |
| status | TINYINT | N | N | 1 | NOT NULL | 1=启用, 0=禁用 |
| created_by | VARCHAR(50) | - | - | 系统登录人 | - | |
| created_at | DATETIME | - | - | 系统时间 | - | yyyy/MM/dd HH:mm:ss |
| updated_by | VARCHAR(50) | - | - | 系统登录人 | - | |
| updated_at | DATETIME | - | - | 系统时间 | - | yyyy/MM/dd HH:mm:ss |
| deleted | TINYINT | - | - | 0 | NOT NULL | 0=未删除, 1=已删除 |

```sql
CREATE TABLE material_version (
    id           BIGINT      AUTO_INCREMENT PRIMARY KEY,
    material_id  BIGINT      NOT NULL COMMENT '物料ID',
    version_no   VARCHAR(20) NOT NULL COMMENT '版本号, 如 A.1, A.2',
    status       TINYINT     NOT NULL DEFAULT 1 COMMENT '1-启用, 0-禁用',
    created_by   VARCHAR(50) DEFAULT NULL,
    created_at   DATETIME    DEFAULT NULL,
    updated_by   VARCHAR(50) DEFAULT NULL,
    updated_at   DATETIME    DEFAULT NULL,
    deleted      TINYINT     NOT NULL DEFAULT 0 COMMENT '0-未删除, 1-已删除',
    UNIQUE KEY uk_material_version (material_id, version_no),
    INDEX idx_material_id (material_id),
    CONSTRAINT fk_version_material FOREIGN KEY (material_id) REFERENCES material_master(id)
) COMMENT '物料版本';
```

### 设计决策

| 决策 | 原因 |
|------|------|
| material_code VARCHAR(50) UNIQUE | 业务主键，创建后不可修改，需 DB 级唯一约束 |
| material_name VARCHAR(255) UNIQUE | 设计文档要求显示 20 个中文字符长度，255 足够 |
| basic_category VARCHAR(50) 存枚举值 | 使用字符串存储枚举值（而非 TINYINT），可读性好，枚举数量少（4 个）性能影响可忽略 |
| 5 个扩展字段 ext1~ext5 | 避免频繁 ALTER TABLE 加字段，VARCHAR(50) 覆盖大部分场景 |
| PCB 专用字段均为可选 | 不同基本分类（成品 vs 备件）使用不同的属性子集，不需要的字段留空 |
| MaterialVersion 独立表 | 版本与物料主表分离，避免主表过宽，版本数据可独立查询 |
| material_id + version_no 联合唯一 | 同一物料不允许重复版本号 |
| category_id 和 uom_id 使用 FK | 确保引用完整性，级联查询效率高 |

### 枚举定义

| 枚举 | 值 | 备注 |
|------|---|------|
| BasicCategory | 成品, 半成品, 原材料, 备件 | 基本分类 |
| AttributeCategory | 采购件, 自制件, 采购&自制件 | 属性分类 |

## 业务逻辑
- 创建：物料编码、名称、基本分类、属性分类、单位必填；校验编码、名称分别唯一
- 编辑：编码不可修改，可修改名称、分类、单位等属性
- 删除：已被引用的物料不可删除（按钮置灰）
- 启用/禁用：已禁用的物料不可被新创建的业务单据引用
- 版本管理：同一物料可创建多个版本（A.1→A.2），版本号必填
- 导入：唯一性校验，已存在编码不允许再次导入
- 导出：导出查询结果到 Excel
- 查询：编码/名称模糊查询，物料分类下拉筛选，基本分类/状态下拉筛选
- 日志：查看变更履历

## 验收标准 (Gherkin)

### 场景 1: 物料编码唯一性
```gherkin
Given 物料编码 "MAT-001" 已存在
When 再次创建编码为 "MAT-001" 的物料
Then 返回错误 "物料编码已存在"
```

### 场景 2: 物料版本管理
```gherkin
Given 物料 "MAT-001" 当前版本为 A.1
When 创建新版本 A.2
Then 新版本正确关联到同一物料
And 编码创建后不可修改
```

### 场景 3: 已引用物料不可删除
```gherkin
Given 物料 "MAT-001" 已被免检清单引用
When 尝试删除该物料
Then 删除按钮置灰，不可点击
```

## Merge Record
- **Completed**: 2026-04-22
- **Merged Branch**: feature/material-info
- **Merge Commit**: a247871
- **Archive Tag**: feat-material-info-20260422
- **Conflicts**: None
- **Verification**: PASS (with warnings - Excel/AuditLog/tests deferred)
- **Files Changed**: 24
- **Duration**: ~30 minutes
