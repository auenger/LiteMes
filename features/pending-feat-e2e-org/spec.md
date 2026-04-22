# Feature: feat-e2e-org 组织架构 E2E 测试（模块索引）

## Basic Information
- **ID**: feat-e2e-org
- **Name**: 组织架构 E2E 测试（公司/工厂/部门/班制班次）
- **Priority**: 70
- **Size**: M (module-level)
- **Dependencies**: []
- **Parent**: feat-e2e-master-data
- **Children**: [feat-e2e-org-base, feat-e2e-org-advanced]
- **Created**: 2026-04-22

## Description

对组织架构模块 4 个页面进行 Playwright E2E 测试，按页面复杂度拆分为 2 个子 Feature 并行开发。

### 覆盖页面
| 页面 | 路由 | 所属子 Feature |
|------|------|---------------|
| 公司管理 | /companies | feat-e2e-org-base |
| 工厂管理 | /factories | feat-e2e-org-base |
| 部门管理 | /departments | feat-e2e-org-advanced |
| 班制班次 | /shift-schedule | feat-e2e-org-advanced |

## Sub-Feature List

| ID | Name | Priority | Size | Dependencies |
|----|------|----------|------|--------------|
| [feat-e2e-org-base](../pending-feat-e2e-org-base/spec.md) | 组织架构基础（公司/工厂 + 公共 Fixture） | 75 | S | [] |
| [feat-e2e-org-advanced](../pending-feat-e2e-org-advanced/spec.md) | 组织架构高级（部门/班制班次） | 70 | S | [feat-e2e-org-base] |

## Dependency Graph

```
feat-e2e-org-base (auth fixture + 公司/工厂)
  └── feat-e2e-org-advanced (部门/班制)
```

## Module Progress
| Sub-Feature | Status | Notes |
|-------------|--------|-------|
| feat-e2e-org-base | pending | 含公共 fixture 创建 |
| feat-e2e-org-advanced | pending | 依赖 org-base |
