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

### DepartmentUser（继承 BaseEntity，无软删除）
| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| department_id | BIGINT | FK -> Department, NOT NULL | 部门 |
| user_id | BIGINT | FK -> User, NOT NULL | 用户 |
| created_by | VARCHAR(64) | AUTO FILL | 创建人 |
| created_at | DATETIME | AUTO FILL | 创建时间 |

### 数据库索引
| 索引名 | 类型 | 字段 | 说明 |
|--------|------|------|------|
| uk_dept_user | UNIQUE | (department_id, user_id) | 同一部门不可重复分配用户 |
| idx_dept_user_dept | INDEX | department_id | 按部门查用户 |
| idx_dept_user_user | INDEX | user_id | 按用户查部门 |

### 数据库表 DDL（参考）

```sql
CREATE TABLE department_user (
    id            BIGINT      NOT NULL AUTO_INCREMENT,
    department_id BIGINT      NOT NULL,
    user_id       BIGINT      NOT NULL,
    created_by    VARCHAR(64) NULL,
    created_at    DATETIME    NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_dept_user (department_id, user_id),
    INDEX idx_dept_user_dept (department_id),
    INDEX idx_dept_user_user (user_id),
    CONSTRAINT fk_du_department FOREIGN KEY (department_id) REFERENCES department(id),
    CONSTRAINT fk_du_user FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门用户关系';
```

### 业务规则
- 同一用户可归属多个部门（多对多关系）
- 移除用户为物理删除（非软删除），直接删除关联记录
- 部门被删除（软删除）时，其下所有 DepartmentUser 记录一并物理删除

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

Scenario: 重复分配同一用户
  Given 用户"张三"已分配到部门"DEP001"
  When 再次将"张三"分配到部门"DEP001"
  Then 分配失败，提示"该用户已在此部门中"

Scenario: 用户归属多个部门
  Given 部门"DEP001"和"DEP002"已存在
  And 用户"张三"已分配到部门"DEP001"
  When 将"张三"分配到部门"DEP002"
  Then "张三"同时归属"DEP001"和"DEP002"
```

## Merge Record

- **Completed**: 2026-04-22
- **Branch**: feature/feat-department-user
- **Merge Commit**: b9026cd
- **Archive Tag**: feat-department-user-20260422
- **Conflicts**: none
- **Verification**: passed (6/6 scenarios, 15/15 tests)
- **Files Changed**: 20 (16 new, 4 modified)
