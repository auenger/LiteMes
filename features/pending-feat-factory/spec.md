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

### Factory
| 字段 | 类型 | 约束 |
|------|------|------|
| id | UUID | PK |
| factory_code | VARCHAR(50) | UNIQUE, NOT NULL |
| name | VARCHAR(50) | NOT NULL |
| short_name | VARCHAR(50) | |
| company_id | UUID | FK -> Company, NOT NULL |
| status | BOOLEAN | DEFAULT true |
| created_by | VARCHAR(50) | |
| created_at | DATETIME | |
| updated_by | VARCHAR(50) | |
| updated_at | DATETIME | |

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
