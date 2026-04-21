# Feature: feat-factory 工厂管理

## 基本信息
- **ID**: feat-factory
- **名称**: 工厂管理
- **优先级**: 90
- **规模**: M
- **依赖**: feat-company
- **创建时间**: 2026-04-20

## 需求来源
- `docx/企业管理_功能设计_V1.0/企业管理_功能设计_V1.0.md` — 工厂

## 需求描述
维护工厂基础数据，包括工厂的增删改查、状态管理，工厂隶属于公司。

### 功能清单
| 功能 | 说明 |
|------|------|
| 工厂查询 | 按工厂编码、工厂名称、公司模糊查询，按状态筛选 |
| 工厂创建 | 填写工厂编码、工厂名称、简称、所属公司 |
| 工厂编辑 | 修改工厂名称、简称、公司（工厂编码不可修改） |
| 工厂删除 | 未被引用时可删除，被引用时不可删除 |
| 状态管理 | 启用/禁用工厂 |

## 数据模型

### Factory（继承 SoftDeleteEntity）
| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| factory_code | VARCHAR(50) | UNIQUE, NOT NULL | 工厂编码，创建后不可修改 |
| name | VARCHAR(50) | NOT NULL | 工厂名称 |
| short_name | VARCHAR(50) | | 简称 |
| company_id | BIGINT | FK -> Company, NOT NULL | 所属公司 |
| status | TINYINT | NOT NULL, DEFAULT 1 | 状态：1=启用，0=禁用 |
| deleted | TINYINT | NOT NULL, DEFAULT 0 | 软删除标记（@TableLogic） |
| created_by | VARCHAR(64) | AUTO FILL | 创建人 |
| created_at | DATETIME | AUTO FILL | 创建时间 |
| updated_by | VARCHAR(64) | AUTO FILL | 修改人 |
| updated_at | DATETIME | AUTO FILL | 修改时间 |

### 数据库索引
| 索引名 | 类型 | 字段 | 说明 |
|--------|------|------|------|
| uk_factory_code | UNIQUE | factory_code | 编码唯一性（WHERE deleted = 0） |
| idx_factory_company | INDEX | company_id | 按公司查询工厂 |

### 数据库表 DDL（参考）

```sql
CREATE TABLE factory (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    factory_code  VARCHAR(50)  NOT NULL,
    name          VARCHAR(50)  NOT NULL,
    short_name    VARCHAR(50)  NULL,
    company_id    BIGINT       NOT NULL,
    status        TINYINT      NOT NULL DEFAULT 1 COMMENT '1=启用,0=禁用',
    deleted       TINYINT      NOT NULL DEFAULT 0,
    created_by    VARCHAR(64)  NULL,
    created_at    DATETIME     NULL,
    updated_by    VARCHAR(64)  NULL,
    updated_at    DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_factory_code (factory_code, deleted),
    INDEX idx_factory_company (company_id),
    CONSTRAINT fk_factory_company FOREIGN KEY (company_id) REFERENCES company(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工厂';
```

### 引用检查规则
- 被删除前需检查 `Department.factory_id` 是否有引用
- 被禁用前需检查关联部门是否仍有启用状态

## 验收标准

### Gherkin 场景

```gherkin
Feature: 工厂管理

Scenario: 创建工厂
  Given 公司"COM001"已存在
  When 创建工厂，填写工厂编码"FAC001"、工厂名称"测试工厂"、简称"测试"、所属公司"COM001"
  Then 工厂创建成功
  And 工厂编码"FAC001"已存在

Scenario: 工厂必须选择公司
  Given 公司未创建
  When 创建工厂
  Then 提示"请选择所属公司"

Scenario: 编辑工厂
  Given 工厂"FAC001"已存在
  When 编辑工厂，将名称改为"新测试工厂"
  Then 工厂名称已更新
  And 工厂编码保持为"FAC001"

Scenario: 删除被引用工厂
  Given 工厂"FAC001"已被部门引用
  When 删除工厂"FAC001"
  Then 删除失败，提示"该工厂已被引用，无法删除"

Scenario: 工厂查询显示公司信息
  Given 工厂"FAC001"属于公司"COM001"
  When 查询工厂列表
  Then 每条记录显示所属公司名称
```

## Merge Record

- **Completed**: 2026-04-22
- **Branch**: feature/feat-factory
- **Merge Commit**: fb1fd11
- **Archive Tag**: feat-factory-20260422
- **Conflicts**: none
- **Verification**: passed (4/5 scenarios passed, 1 partial - department reference deferred)
- **Files Changed**: 20
- **Duration**: less than 1 day
