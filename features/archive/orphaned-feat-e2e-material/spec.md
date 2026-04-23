# Feature: feat-e2e-material 物料管理 E2E 测试（模块索引）

## Basic Information
- **ID**: feat-e2e-material
- **Name**: 物料管理 E2E 测试（物料分类/物料信息/免检清单）
- **Priority**: 70
- **Size**: M (module-level)
- **Dependencies**: [feat-e2e-production-base]
- **Parent**: feat-e2e-master-data
- **Children**: [feat-e2e-material-category, feat-e2e-material-info]
- **Created**: 2026-04-22

## Description

对物料管理模块 3 个页面进行 Playwright E2E 测试，按复杂度拆分为 2 个子 Feature。

### 覆盖页面
| 页面 | 路由 | 所属子 Feature |
|------|------|---------------|
| 物料分类 | /material-categories | feat-e2e-material-category |
| 物料信息 | /materials | feat-e2e-material-info |
| 免检清单 | /inspection-exemptions | feat-e2e-material-info |

## Sub-Feature List

| ID | Name | Priority | Size | Dependencies |
|----|------|----------|------|--------------|
| [feat-e2e-material-category](../pending-feat-e2e-material-category/spec.md) | 物料分类 E2E（树形结构） | 70 | S | [feat-e2e-production-base] |
| [feat-e2e-material-info](../pending-feat-e2e-material-info/spec.md) | 物料信息与免检 E2E | 70 | S | [feat-e2e-material-category] |

## Dependency Graph

```
feat-e2e-material-category (树形分类)
  └── feat-e2e-material-info (物料信息 + 免检清单)
```

## Module Progress
| Sub-Feature | Status | Notes |
|-------------|--------|-------|
| feat-e2e-material-category | pending | 树形结构测试 |
| feat-e2e-material-info | pending | 复杂表单 + PCB 属性 |
