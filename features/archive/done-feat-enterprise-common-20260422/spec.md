# Feature: feat-enterprise-common 企业管理通用功能

## 基本信息
- **ID**: feat-enterprise-common
- **名称**: 企业管理通用功能
- **优先级**: 80
- **规模**: S
- **依赖**: feat-company, feat-factory, feat-department, feat-shift-schedule, feat-work-center, feat-process
- **创建时间**: 2026-04-20

## 需求来源
- `docx/企业管理_功能设计_V1.0/企业管理_功能设计_V1.0.md` — 通用说明

## 需求描述
企业管理模块的通用功能，包括变更日志、表格设置、通用下拉接口、级联选择器接口等。

### 功能清单
| 功能 | 说明 |
|------|------|
| 变更日志 | 记录数据的增删改操作及变更详情，供用户查看变更履历 |
| 表格设置 | 设置列的显示/隐藏、列宽、顺序、对齐方式 |
| 通用下拉 | 公司/工厂/部门/班制等基础数据的下拉选项接口 |
| 级联选择器 | 公司 → 工厂 → 部门级联查询接口 |

## 数据模型

### AuditLog（变更日志，继承 BaseEntity）
| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| table_name | VARCHAR(64) | NOT NULL | 变更的表名 |
| record_id | BIGINT | NOT NULL | 变更记录的主键 |
| action | VARCHAR(16) | NOT NULL | 操作类型：CREATE / UPDATE / DELETE |
| old_value | TEXT | NULL | 变更前的值（JSON 格式，仅 UPDATE 时记录） |
| new_value | TEXT | NULL | 变更后的值（JSON 格式） |
| changed_fields | VARCHAR(512) | NULL | 变更的字段名列表（逗号分隔） |
| operator_id | BIGINT | NULL | 操作人 ID |
| operator_name | VARCHAR(64) | NULL | 操作人名称 |
| created_at | DATETIME | AUTO FILL | 操作时间 |

### 数据库索引
| 索引名 | 类型 | 字段 | 说明 |
|--------|------|------|------|
| idx_audit_table_record | INDEX | (table_name, record_id) | 按表+记录查变更履历 |
| idx_audit_created | INDEX | created_at | 按时间范围查询 |

### 数据库表 DDL（参考）

```sql
CREATE TABLE audit_log (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    table_name      VARCHAR(64)  NOT NULL,
    record_id       BIGINT       NOT NULL,
    action          VARCHAR(16)  NOT NULL COMMENT 'CREATE/UPDATE/DELETE',
    old_value       TEXT         NULL COMMENT '变更前值(JSON)',
    new_value       TEXT         NULL COMMENT '变更后值(JSON)',
    changed_fields  VARCHAR(512) NULL COMMENT '变更字段列表',
    operator_id     BIGINT       NULL,
    operator_name   VARCHAR(64)  NULL,
    created_at      DATETIME     NULL,
    PRIMARY KEY (id),
    INDEX idx_audit_table_record (table_name, record_id),
    INDEX idx_audit_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='变更日志';
```

### 技术实现要点
- **AuditLog 记录方式**：通过 MyBatis-Plus 拦截器或在 Application Service 层统一记录
- **变更内容捕获**：UPDATE 操作时，通过比较更新前后的 DTO 差异生成 `old_value` / `new_value`
- **CREATE 操作**：仅记录 `new_value`，`old_value` 和 `changed_fields` 为 NULL
- **DELETE 操作**：记录 `old_value` 为删除前完整数据，`new_value` 为 NULL
- **表格设置**：使用前端 localStorage 保存用户偏好，无需后端存储

## 验收标准

### Gherkin 场景

```gherkin
Feature: 变更日志

Scenario: 查看创建日志
  Given 公司"COM001"刚刚被创建
  When 用户点击"查看变更履历"
  Then 显示变更记录：操作类型=创建，操作人、操作时间、新值详情

Scenario: 查看编辑日志
  Given 公司"COM001"刚刚被编辑（名称从"测试公司"改为"新测试公司"）
  When 用户点击"查看变更履历"
  Then 显示变更记录：操作类型=修改
  And 变更字段显示"name"
  And 旧值="测试公司"，新值="新测试公司"

Scenario: 查看删除日志
  Given 公司"COM001"刚刚被软删除
  When 用户点击"查看变更履历"
  Then 显示变更记录：操作类型=删除
  And 记录删除前的完整数据

Scenario: 按时间范围查询日志
  Given 多条变更日志存在
  When 选择时间范围"2026-04-01"至"2026-04-30"
  Then 只返回该时间范围内的变更记录
```

```gherkin
Feature: 表格设置

Scenario: 设置列显示
  Given 用户在列表页面
  When 点击"表格设置"
  Then 弹出设置面板，可选择列的显示/隐藏

Scenario: 调整列宽
  Given 用户在列表页面
  When 拖动列边框调整列宽
  Then 列宽已调整并记住用户偏好
```

```gherkin
Feature: 通用下拉接口

Scenario: 获取公司下拉列表
  Given 公司"COM001"、"COM002"已存在且为启用状态
  When 前端请求公司下拉数据
  Then 返回 [{id, companyCode, name}] 格式数据

Scenario: 公司→工厂级联
  Given 公司"COM001"下有工厂"FAC001"和"FAC002"
  When 前端选择公司"COM001"后请求工厂下拉
  Then 只返回"FAC001"和"FAC002"

Scenario: 工厂→部门级联
  Given 工厂"FAC001"下有部门"DEP001"
  When 前端选择工厂"FAC001"后请求部门下拉
  Then 只返回"FAC001"下的部门列表（含树形结构）
```

## Merge Record

- **Completed**: 2026-04-22
- **Merged Branch**: feature/feat-enterprise-common
- **Merge Commit**: eba97f4
- **Archive Tag**: feat-enterprise-common-20260422
- **Conflicts**: None
- **Verification Status**: passed (10/10 Gherkin scenarios validated)
- **Stats**: 32 files changed, 2093 insertions(+), 58 deletions(-), 1 commit
