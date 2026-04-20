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
| [feat-enterprise-common](../pending-feat-enterprise-common/spec.md) | 通用功能 | 90 | S | 以上全部 |

## 依赖关系图

```
feat-enterprise-org (模块)
├── feat-company ──→ feat-factory ──→ feat-department ──→ feat-department-user
├── feat-shift-schedule (可并行)
└── feat-enterprise-common (收尾)
```

## 整体数据模型关系

```
Company (公司)
  └── Factory (工厂)
        └── Department (部门)
              └── DepartmentUser (部门用户关系)

ShiftSchedule (班制)
  └── Shift (班次)
```

## 模块进度

| 子 Feature | 状态 | 备注 |
|-----------|------|------|
| feat-company | pending | |
| feat-factory | pending | |
| feat-department | pending | |
| feat-department-user | pending | |
| feat-shift-schedule | pending | |
| feat-enterprise-common | pending | |
