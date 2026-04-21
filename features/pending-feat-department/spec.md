# Feature: feat-department 部门管理

## 基本信息
- **ID**: feat-department
- **名称**: 部门管理
- **优先级**: 90
- **规模**: M
- **依赖**: feat-factory
- **创建时间**: 2026-04-20

## 需求来源
- `docx/企业管理_功能设计_V1.0/企业管理_功能设计_V1.0.md` — 部门

## 需求描述
维护部门基础数据，包括部门的增删改查、状态管理，部门隶属于工厂，支持多级树形结构。

### 功能清单
| 功能 | 说明 |
|------|------|
| 部门查询 | 按部门编码、部门名称模糊查询，按状态筛选 |
| 部门创建 | 填写部门编码、部门名称、所属工厂、上级部门（可选） |
| 部门编辑 | 修改部门名称（部门编码不可修改） |
| 部门删除 | 未被引用时可删除，被引用时不可删除（含子部门引用检查） |
| 状态管理 | 启用/禁用部门 |
| 树形结构 | 支持多级部门树，通过 parent_id 自引用实现 |

## 数据模型

### Department（继承 SoftDeleteEntity）
| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| department_code | VARCHAR(50) | UNIQUE, NOT NULL | 部门编码，创建后不可修改 |
| name | VARCHAR(50) | NOT NULL | 部门名称 |
| factory_id | BIGINT | FK -> Factory, NOT NULL | 所属工厂 |
| parent_id | BIGINT | FK -> Department, NULL | 上级部门（NULL 表示顶级） |
| sort_order | INT | DEFAULT 0 | 同级排序号 |
| status | TINYINT | NOT NULL, DEFAULT 1 | 状态：1=启用，0=禁用 |
| deleted | TINYINT | NOT NULL, DEFAULT 0 | 软删除标记（@TableLogic） |
| created_by | VARCHAR(64) | AUTO FILL | 创建人 |
| created_at | DATETIME | AUTO FILL | 创建时间 |
| updated_by | VARCHAR(64) | AUTO FILL | 修改人 |
| updated_at | DATETIME | AUTO FILL | 修改时间 |

### 数据库索引
| 索引名 | 类型 | 字段 | 说明 |
|--------|------|------|------|
| uk_department_code | UNIQUE | department_code | 编码唯一性（WHERE deleted = 0） |
| idx_dept_factory | INDEX | factory_id | 按工厂查询部门 |
| idx_dept_parent | INDEX | parent_id | 查询子部门（树形遍历） |

### 数据库表 DDL（参考）

```sql
CREATE TABLE department (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    department_code VARCHAR(50)  NOT NULL,
    name            VARCHAR(50)  NOT NULL,
    factory_id      BIGINT       NOT NULL,
    parent_id       BIGINT       NULL COMMENT '上级部门ID，NULL表示顶级部门',
    sort_order      INT          NOT NULL DEFAULT 0,
    status          TINYINT      NOT NULL DEFAULT 1 COMMENT '1=启用,0=禁用',
    deleted         TINYINT      NOT NULL DEFAULT 0,
    created_by      VARCHAR(64)  NULL,
    created_at      DATETIME     NULL,
    updated_by      VARCHAR(64)  NULL,
    updated_at      DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_department_code (department_code, deleted),
    INDEX idx_dept_factory (factory_id),
    INDEX idx_dept_parent (parent_id),
    CONSTRAINT fk_dept_factory FOREIGN KEY (factory_id) REFERENCES factory(id),
    CONSTRAINT fk_dept_parent FOREIGN KEY (parent_id) REFERENCES department(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门';
```

### 引用检查规则
- 被删除前需检查：`DepartmentUser.department_id` 是否有引用、`Department.parent_id` 是否有子部门
- 禁用父部门时，子部门状态不受级联影响（独立管理）

## 验收标准

### Gherkin 场景

```gherkin
Feature: 部门管理

Scenario: 创建部门
  Given 工厂"FAC001"已存在
  When 创建部门，填写部门编码"DEP001"、部门名称"研发部"
  Then 部门创建成功
  And 部门编码"DEP001"已存在

Scenario: 创建子部门
  Given 部门"DEP001"（研发部）已存在
  When 创建部门，填写编码"DEP002"、名称"前端组"、上级部门选择"研发部"
  Then 部门"DEP002"创建成功
  And "DEP002"的 parent_id 指向"DEP001"

Scenario: 创建顶级部门
  Given 工厂"FAC001"已存在
  When 创建部门，填写编码"DEP003"、名称"质量部"、上级部门留空
  Then 部门创建成功
  And parent_id 为 NULL

Scenario: 编辑部门
  Given 部门"DEP001"已存在
  When 编辑部门，将名称改为"产品部"
  Then 部门名称已更新
  And 部门编码保持为"DEP001"

Scenario: 删除被引用部门
  Given 部门"DEP001"已被其他数据引用
  When 删除部门"DEP001"
  Then 删除失败，提示"该部门已被引用，无法删除"

Scenario: 删除有子部门的部门
  Given 部门"DEP001"下有子部门"DEP002"
  When 删除部门"DEP001"
  Then 删除失败，提示"该部门下存在子部门，无法删除"
```
