# Feature: feat-company 公司管理

## 基本信息
- **ID**: feat-company
- **名称**: 公司管理
- **优先级**: 90
- **规模**: M
- **依赖**: feat-project-scaffold
- **创建时间**: 2026-04-20

## 需求来源
- `docx/企业管理_功能设计_V1.0/企业管理_功能设计_V1.0.md` — 公司

## 需求描述
维护公司基础数据，包括公司的增删改查、状态管理。

### 功能清单
| 功能 | 说明 |
|------|------|
| 公司查询 | 按公司编码、公司名称模糊查询，按状态筛选 |
| 公司创建 | 填写公司编码、公司名称、简码 |
| 公司编辑 | 修改公司名称和简码（公司编码不可修改） |
| 公司删除 | 未被引用时可删除，被引用时不可删除 |
| 状态管理 | 启用/禁用公司 |

## 数据模型

### Company（继承 SoftDeleteEntity）
| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| company_code | VARCHAR(50) | UNIQUE, NOT NULL | 公司编码，创建后不可修改 |
| name | VARCHAR(50) | NOT NULL | 公司名称 |
| short_code | VARCHAR(50) | | 简码 |
| status | TINYINT | NOT NULL, DEFAULT 1 | 状态：1=启用，0=禁用 |
| deleted | TINYINT | NOT NULL, DEFAULT 0 | 软删除标记（@TableLogic） |
| created_by | VARCHAR(64) | AUTO FILL | 创建人（MetaObjectHandler 自动填充） |
| created_at | DATETIME | AUTO FILL | 创建时间 |
| updated_by | VARCHAR(64) | AUTO FILL | 修改人 |
| updated_at | DATETIME | AUTO FILL | 修改时间 |

### 数据库索引
| 索引名 | 类型 | 字段 | 说明 |
|--------|------|------|------|
| uk_company_code | UNIQUE | company_code | 编码唯一性（WHERE deleted = 0） |

### 数据库表 DDL（参考）

```sql
CREATE TABLE company (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    company_code  VARCHAR(50)  NOT NULL,
    name          VARCHAR(50)  NOT NULL,
    short_code    VARCHAR(50)  NULL,
    status        TINYINT      NOT NULL DEFAULT 1 COMMENT '1=启用,0=禁用',
    deleted       TINYINT      NOT NULL DEFAULT 0,
    created_by    VARCHAR(64)  NULL,
    created_at    DATETIME     NULL,
    updated_by    VARCHAR(64)  NULL,
    updated_at    DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_company_code (company_code, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公司';
```

### 引用检查规则
- 被删除前需检查 `Factory.company_id` 是否有引用
- 被禁用前需检查关联工厂是否仍有启用状态

## 验收标准

### Gherkin 场景

```gherkin
Feature: 公司管理

Scenario: 创建公司
  Given 用户已登录并有管理员权限
  When 创建公司，填写公司编码"COM001"、公司名称"测试公司"、简码"CS"
  Then 公司创建成功
  And 公司编码"COM001"已存在

Scenario: 创建公司编码重复
  Given 公司"COM001"已存在
  When 创建公司，填写公司编码"COM001"
  Then 创建失败，提示"公司编码已存在"

Scenario: 编辑公司
  Given 公司"COM001"已存在
  When 编辑公司，将名称改为"新测试公司"
  Then 公司名称已更新
  And 公司编码保持为"COM001"

Scenario: 公司编码不可修改
  Given 公司"COM001"已存在
  When 编辑公司
  Then 公司编码输入框为只读

Scenario: 删除未引用公司
  Given 公司"COM001"未被任何工厂引用
  When 删除公司"COM001"
  Then 删除成功

Scenario: 删除被引用公司
  Given 公司"COM001"已被工厂引用
  When 删除公司"COM001"
  Then 删除失败，提示"该公司已被引用，无法删除"
  And 删除按钮保持禁用状态

Scenario: 模糊查询公司
  Given 公司"COM001"和"COM002"已存在
  When 按公司编码"COM"模糊查询
  Then 返回"COM001"和"COM002"

Scenario: 按状态筛选
  Given 启用的公司"COM001"和禁用的公司"COM002"已存在
  When 按状态"禁用"筛选
  Then 只返回"COM002"
```
