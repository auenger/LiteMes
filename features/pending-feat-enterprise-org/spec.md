# Feature: feat-enterprise-org 企业管理模块

## 基本信息
- **ID**: feat-enterprise-org
- **名称**: 企业管理模块
- **优先级**: 90
- **规模**: L（模块级）
- **依赖**: feat-project-scaffold
- **创建时间**: 2026-04-20

## 需求来源
- `docx/企业管理_功能设计_V1.0/企业管理_功能设计_V1.0.md`

## 模块概述
企业管理模块是 MES 系统的基础数据底座，统一维护公司、工厂、部门、用户关系及班制等基础组织与时间数据。

## 子 Feature 列表

| ID | 名称 | 优先级 | 规模 | 依赖 |
|----|------|--------|------|------|
| [feat-company](../pending-feat-company/spec.md) | 公司管理 | 95 | M | feat-project-scaffold |
| [feat-factory](../pending-feat-factory/spec.md) | 工厂管理 | 94 | M | feat-company |
| [feat-department](../pending-feat-department/spec.md) | 部门管理 | 93 | M | feat-factory |
| [feat-department-user](../pending-feat-department-user/spec.md) | 部门用户关系 | 92 | S | feat-department |
| [feat-shift-schedule](../pending-feat-shift-schedule/spec.md) | 班制班次管理 | 91 | M | feat-project-scaffold |
| [feat-work-center](../pending-feat-work-center/spec.md) | 工作中心管理 | 89 | M | feat-factory |
| [feat-process](../pending-feat-process/spec.md) | 工序管理 | 88 | M | feat-work-center |
| [feat-enterprise-common](../pending-feat-enterprise-common/spec.md) | 通用功能 | 90 | S | 以上全部 |

## 依赖关系图

```
feat-enterprise-org (模块)
├── feat-company ──→ feat-factory ──→ feat-department ──→ feat-department-user
│                     │
│                     └──→ feat-work-center ──→ feat-process
├── feat-shift-schedule (可并行)
└── feat-enterprise-common (收尾)
```

## 整体数据模型关系

```
Company (公司) — SoftDeleteEntity
  │  id: BIGINT PK
  │  company_code: VARCHAR(50) UNIQUE
  │  name, short_code, status, deleted, 审计字段
  │
  └── Factory (工厂) — SoftDeleteEntity
        │  id: BIGINT PK
        │  factory_code: VARCHAR(50) UNIQUE
        │  company_id: FK -> Company
        │  name, short_name, status, deleted, 审计字段
        │
        ├── Department (部门) — SoftDeleteEntity
        │     │  id: BIGINT PK
        │     │  department_code: VARCHAR(50) UNIQUE
        │     │  factory_id: FK -> Factory
        │     │  parent_id: FK -> Department (自引用，树形结构)
        │     │  name, sort_order, status, deleted, 审计字段
        │     │
        │     └── DepartmentUser (部门用户关系) — BaseEntity
        │           │  id: BIGINT PK
        │           │  department_id: FK -> Department
        │           │  user_id: FK -> User
        │           │  UK: (department_id, user_id)
        │           │  created_by, created_at
        │
        └── WorkCenter (工作中心) — SoftDeleteEntity
              │  id: BIGINT PK
              │  work_center_code: VARCHAR(50) UNIQUE
              │  factory_id: FK -> Factory
              │  name, status, deleted, 审计字段
              │
              └── Process (工序) — SoftDeleteEntity
                    │  id: BIGINT PK
                    │  process_code: VARCHAR(50) UNIQUE
                    │  work_center_id: FK -> WorkCenter
                    │  name, status, deleted, 审计字段

ShiftSchedule (班制) — SoftDeleteEntity
  │  id: BIGINT PK
  │  shift_code: VARCHAR(50) UNIQUE
  │  name, is_default, status, deleted, 审计字段
  │
  └── Shift (班次) — SoftDeleteEntity
        │  id: BIGINT PK
        │  shift_schedule_id: FK -> ShiftSchedule
        │  shift_code: VARCHAR(50) UNIQUE
        │  name, start_time, end_time, cross_day, status, deleted, 审计字段

AuditLog (变更日志) — BaseEntity（横切关注点）
     id: BIGINT PK
     table_name, record_id, action (CREATE/UPDATE/DELETE)
     old_value (JSON), new_value (JSON), changed_fields
     operator_id, operator_name, created_at
```

### 实体基类映射

| 基类 | 字段 | 适用实体 |
|------|------|---------|
| BaseEntity | created_by, created_at | 所有实体 |
| SoftDeleteEntity (extends BaseEntity) | + updated_by, updated_at, deleted | Company, Factory, Department, WorkCenter, Process, ShiftSchedule, Shift |
| BaseEntity only | 仅 created_by, created_at | DepartmentUser, AuditLog |

## 模块进度

| 子 Feature | 状态 | 备注 |
|-----------|------|------|
| feat-company | pending | |
| feat-factory | pending | |
| feat-department | pending | |
| feat-department-user | pending | |
| feat-shift-schedule | pending | |
| feat-work-center | pending | 新增，支撑数据权限 |
| feat-process | pending | 新增，支撑数据权限 |
| feat-enterprise-common | pending | |
