# Feature: feat-e2e-org-advanced 组织架构高级 E2E 测试

## Basic Information
- **ID**: feat-e2e-org-advanced
- **Name**: 组织架构高级 E2E 测试（部门/班制班次）
- **Priority**: 70
- **Size**: S
- **Dependencies**: [feat-e2e-org-base]
- **Parent**: feat-e2e-org
- **Children**: []
- **Created**: 2026-04-22

## Description

对组织架构高级模块 2 个页面进行 Playwright E2E 测试：部门管理（树形结构）、班制班次（嵌套对话框）。复用 feat-e2e-org-base 创建的公共 fixture。

### 测试页面
| 页面 | 路由 | 测试文件 |
|------|------|---------|
| 部门管理 | /departments | e2e/org/department.spec.ts |
| 班制班次 | /shift-schedule | e2e/org/shift-schedule.spec.ts |

## User Value Points
1. 验证部门管理 CRUD 及工厂级联关联 + 树形结构展示
2. 验证班制班次 CRUD 及嵌套班次管理（时间/跨天）

## Context Analysis

### Reference Code
- 公共 fixture：`e2e/fixtures/auth.ts`（由 feat-e2e-org-base 创建）
- 公共工具函数：`e2e/helpers/common.ts`（由 feat-e2e-org-base 创建）
- 部门视图：`frontend/src/views/department/DepartmentList.vue` — 字段：编码/名称/上级部门/工厂选择器/排序/状态，树形结构展示
- 班制视图：`frontend/src/views/shift-schedule/ShiftScheduleList.vue` — 字段：编码/名称/默认班制 + 嵌套班次管理对话框（班次编码/名称/开始时间/结束时间/跨天复选框）

### Related Documents
- 父 Feature spec：`features/pending-feat-e2e-org/spec.md`

### Related Features
- feat-e2e-org-base（兄弟，前置依赖）— 提供 auth fixture

## Technical Solution
- 复用 auth fixture 和公共工具函数
- 测试数据前缀：`E2E_ORG_`
- 部门测试需先创建工厂数据（可在 beforeEach 中通过 UI 或 API 创建）
- 班制测试独立于其他模块

## Acceptance Criteria (Gherkin)

```gherkin
Feature: 组织架构高级 E2E 测试

  Background:
    Given 用户已通过 auth fixture 自动登录

  # ── 部门管理 ──

  Scenario: 部门列表加载（树形结构）
    When 导航到 /departments 页面
    Then 页面加载完成，树形表格可见

  Scenario: 新建顶级部门（关联已有工厂）
    Given 已存在测试工厂
    When 点击"新建部门"按钮
    And 填写部门编码 "E2E_ORG_DEPT_001"
    And 填写部门名称 "E2E测试部门"
    And 在工厂选择器中选择对应工厂
    And 点击对话框"确定"按钮
    Then 树形结构中出现 "E2E_ORG_DEPT_001"

  Scenario: 新建子部门（选择上级部门）
    Given 已存在部门 "E2E_ORG_DEPT_001"
    When 点击"新建部门"按钮
    And 填写部门编码 "E2E_ORG_DEPT_002"
    And 填写部门名称 "E2E子部门"
    And 在上级部门选择器中选择 "E2E_ORG_DEPT_001"
    And 点击对话框"确定"按钮
    Then 子部门挂在父部门下方

  Scenario: 编辑部门
    When 点击 "E2E_ORG_DEPT_001" 所在行的"编辑"按钮
    And 修改部门名称为 "E2E测试部门_编辑"
    And 点击对话框"确定"按钮
    Then 部门名称更新成功

  Scenario: 删除子部门
    When 点击 "E2E_ORG_DEPT_002" 所在行的"删除"按钮
    And 在确认对话框中点击"删除"
    Then 子部门从树中消失

  # ── 班制班次 ──

  Scenario: 班制列表加载
    When 导航到 /shift-schedule 页面
    Then 页面加载完成，.el-table 可见

  Scenario: 新建班制
    When 点击"新增班制"按钮
    And 填写班制编码 "E2E_ORG_SHIFT_001"
    And 填写班制名称 "E2E白班"
    And 点击对话框"确定"按钮
    Then 表格中出现 "E2E_ORG_SHIFT_001"

  Scenario: 管理班次（嵌套对话框）
    Given 已存在班制 "E2E_ORG_SHIFT_001"
    When 点击班制的"管理班次"操作
    Then 班次管理对话框打开
    When 新增班次，填写班次编码/名称/开始时间/结束时间
    And 点击班次对话框"确定"
    Then 班次添加成功

  Scenario: 删除班制
    When 点击 "E2E_ORG_SHIFT_001" 所在行的"删除"按钮
    And 在确认对话框中点击"删除"
    Then 表格中不再出现 "E2E_ORG_SHIFT_001"
```

### General Checklist
- [ ] 复用 auth fixture（不重复创建）
- [ ] 树形结构验证（父子部门层级）
- [ ] 嵌套对话框交互（班次管理）
- [ ] 工厂级联关联验证
- [ ] 测试数据使用 E2E_ORG_ 前缀
- [ ] 测试后清理测试数据

## Merge Record

- **Completed**: 2026-04-22
- **Branch**: feature/e2e-org-advanced
- **Merge commit**: f9593eb
- **Archive tag**: feat-e2e-org-advanced-20260422
- **Conflicts**: none
- **Verification**: 9/9 scenarios passed
- **Stats**: 1 commit, 4 files changed (271 insertions, 6 deletions)
- **Duration**: ~1 hour
