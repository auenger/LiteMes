# Feature: feat-department-user 部门用户关系

## 基本信息
- **ID**: feat-department-user
- **名称**: 部门用户关系管理
- **优先级**: 85
- **规模**: S
- **依赖**: feat-department
- **创建时间**: 2026-04-20

## 需求来源
- `docx/企业管理_功能设计_V1.0/企业管理_功能设计_V1.0.md` — 部门与用户关系

## 需求描述
管理部门与用户的关联关系，支持将用户分配到部门或从部门移除。

### 功能清单
| 功能 | 说明 |
|------|------|
| 查看部门用户 | 查看某部门下已分配的用户列表 |
| 选择用户 | 弹出用户选择窗口，查询并勾选用户 |
| 分配用户 | 将选中用户分配到部门 |
| 移除用户 | 从部门移除已分配的用户 |

## 数据模型

### DepartmentUser
| 字段 | 类型 | 约束 |
|------|------|------|
| id | UUID | PK |
| department_id | UUID | FK -> Department, NOT NULL |
| user_id | UUID | FK -> User, NOT NULL |
| created_by | VARCHAR(50) | |
| created_at | DATETIME | |

## 验收标准

### Gherkin 场景

```gherkin
Feature: 部门用户关系

Scenario: 分配用户到部门
  Given 部门"DEP001"已存在
  And 用户"张三"已存在且未分配到任何部门
  When 在部门"DEP001"下点击"选择用户"
  Then 弹出用户选择窗口

Scenario: 查询用户
  Given 用户选择窗口已打开
  When 输入用户名"张"并点击查询
  Then 返回所有用户名包含"张"的用户

Scenario: 确认分配用户
  Given 用户选择窗口已打开
  When 勾选用户"张三"并点击确认
  Then 用户"张三"已分配到部门"DEP001"
  And 部门用户列表显示"张三"

Scenario: 移除部门用户
  Given 用户"张三"已分配到部门"DEP001"
  When 在部门用户列表中移除"张三"
  Then "张三"已从部门"DEP001"移除
```
