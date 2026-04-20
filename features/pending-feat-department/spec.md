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
维护部门基础数据，包括部门的增删改查、状态管理，部门隶属于工厂。

### 功能清单
| 功能 | 说明 |
|------|------|
| 部门查询 | 按部门编码、部门名称模糊查询，按状态筛选 |
| 部门创建 | 填写部门编码、部门名称 |
| 部门编辑 | 修改部门名称（部门编码不可修改） |
| 部门删除 | 未被引用时可删除，被引用时不可删除 |
| 状态管理 | 启用/禁用部门 |

## 数据模型

### Department
| 字段 | 类型 | 约束 |
|------|------|------|
| id | UUID | PK |
| department_code | VARCHAR(50) | UNIQUE, NOT NULL |
| name | VARCHAR(50) | NOT NULL |
| factory_id | UUID | FK -> Factory, NOT NULL |
| status | BOOLEAN | DEFAULT true |
| created_by | VARCHAR(50) | |
| created_at | DATETIME | |
| updated_by | VARCHAR(50) | |
| updated_at | DATETIME | |

## 验收标准

### Gherkin 场景

```gherkin
Feature: 部门管理

Scenario: 创建部门
  Given 工厂"FAC001"已存在
  When 创建部门，填写部门编码"DEP001"、部门名称"研发部"
  Then 部门创建成功
  And 部门编码"DEP001"已存在

Scenario: 编辑部门
  Given 部门"DEP001"已存在
  When 编辑部门，将名称改为"产品部"
  Then 部门名称已更新
  And 部门编码保持为"DEP001"

Scenario: 删除被引用部门
  Given 部门"DEP001"已被其他数据引用
  When 删除部门"DEP001"
  Then 删除失败，提示"该部门已被引用，无法删除"
```
