# Feature: feat-e2e-material-category 物料分类 E2E 测试

## Basic Information
- **ID**: feat-e2e-material-category
- **Name**: 物料分类 E2E 测试（树形结构管理）
- **Priority**: 70
- **Size**: S
- **Dependencies**: [feat-e2e-production-base]
- **Parent**: feat-e2e-material
- **Children**: []
- **Created**: 2026-04-22

## Description

对物料分类页面进行 Playwright E2E 测试，覆盖树形结构的完整 CRUD 操作。

### 测试页面
| 页面 | 路由 | 测试文件 |
|------|------|---------|
| 物料分类 | /material-categories | e2e/material/material-category.spec.ts |

## User Value Points
1. 验证物料分类树形结构管理（新建/编辑/删除分类节点）
2. 验证父子层级关系（新建子分类挂在父节点下）

## Context Analysis

### Reference Code
- 公共 fixture：`e2e/fixtures/auth.ts`（由 feat-e2e-org-base 创建）
- 公共工具函数：`e2e/helpers/common.ts`（由 feat-e2e-org-base 创建）
- 物料分类视图：`frontend/src/views/material-category/MaterialCategoryList.vue` — 树形结构，字段：编码/名称

### Related Documents
- 父 Feature spec：`features/pending-feat-e2e-material/spec.md`

### Related Features
- feat-e2e-production-base（前置依赖）— 确保基础测试完成
- feat-e2e-material-info（兄弟，依赖本 Feature）— 物料信息需要分类数据

## Technical Solution
- 复用 auth fixture 和公共工具函数
- 测试数据前缀：`E2E_MAT_CAT_`
- 树形结构交互：展开节点、在节点下新建子分类

## Acceptance Criteria (Gherkin)

```gherkin
Feature: 物料分类 E2E 测试

  Background:
    Given 用户已通过 auth fixture 自动登录

  Scenario: 物料分类列表加载（树形结构）
    When 导航到 /material-categories 页面
    Then 页面加载完成，树形结构可见

  Scenario: 新建物料分类
    When 点击新建分类按钮
    And 填写分类编码 "E2E_MAT_CAT_001"
    And 填写分类名称 "E2E测试分类"
    And 点击对话框"确定"按钮
    Then 树形结构中出现 "E2E_MAT_CAT_001"

  Scenario: 新建子分类
    Given 已存在分类 "E2E_MAT_CAT_001"
    When 在父分类节点下新建子分类
    And 填写编码 "E2E_MAT_CAT_002"
    And 填写名称 "E2E子分类"
    And 点击对话框"确定"按钮
    Then 子分类挂在父分类下方

  Scenario: 编辑物料分类
    When 编辑分类 "E2E_MAT_CAT_001"
    And 修改名称为 "E2E测试分类_编辑"
    And 点击对话框"确定"按钮
    Then 分类名称更新成功

  Scenario: 删除子分类
    When 删除 "E2E_MAT_CAT_002"
    And 确认删除
    Then 子分类从树中消失
```

### General Checklist
- [ ] 树形结构展示验证
- [ ] 新建顶级分类
- [ ] 新建子分类（层级关系验证）
- [ ] 编辑分类名称
- [ ] 删除子分类
- [ ] 测试数据使用 E2E_MAT_CAT_ 前缀
- [ ] 测试后清理测试数据
