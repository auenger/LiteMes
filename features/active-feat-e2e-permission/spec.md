# Feature: feat-e2e-permission 数据权限 E2E 测试

## Basic Information
- **ID**: feat-e2e-permission
- **Name**: 数据权限 E2E 测试（权限组/用户权限）
- **Priority**: 70
- **Size**: S
- **Dependencies**: [feat-e2e-org]
- **Parent**: feat-e2e-master-data
- **Children**: []
- **Created**: 2026-04-22

## Description

对数据权限模块 2 个页面进行 Playwright E2E 测试：权限组管理、用户权限。覆盖 Tab 标签页关联操作（工厂/工作中心/工序多选）。

### 测试页面
| 页面 | 路由 | 测试文件 |
|------|------|---------|
| 权限组管理 | /data-permission-groups | e2e/permission/permission-group.spec.ts |
| 用户权限 | /user-data-permissions | e2e/permission/user-permission.spec.ts |

## User Value Points
1. 验证权限组 CRUD（含 Tab 标签页关联：工厂/工作中心/工序多选）
2. 验证用户权限绑定（关联权限组和用户）

## Context Analysis

### Reference Code
- 公共登录 fixture：`e2e/fixtures/auth.ts`
- 权限组视图：`frontend/src/views/data-permission-group/DataPermissionGroupList.vue` — 字段：名称/备注 + Tab 关联对话框（工厂/工作中心/工序三个 Tab 页，每个 Tab 内多选 checkbox + 全选 toggle）
- 用户权限视图：`frontend/src/views/user-data-permission/UserDataPermissionList.vue` — 字段：权限组选择器/用户选择器

### Related Documents
- 父 Feature spec：`features/pending-feat-e2e-master-data/spec.md`
- 兄弟 Feature：`features/pending-feat-e2e-org/spec.md`（提供工厂/工作中心/工序数据）

### Related Features
- feat-e2e-org（兄弟，前置依赖）— 提供工厂/工作中心/工序数据
- feat-permission-group（已完成）— 权限组实现参考，含 Tab 关联模式
- feat-user-permission（已完成）— 用户权限实现参考

## Technical Solution
- 测试文件目录：`e2e/permission/`
- 复用公共登录 fixture
- 测试数据前缀：`E2E_PERM_`
- 权限组测试需已有工厂/工作中心/工序数据（由 feat-e2e-org 提供）

## Acceptance Criteria (Gherkin)

```gherkin
Feature: 数据权限 E2E 测试

  Background:
    Given 用户已登录 admin 账号

  # ── 权限组管理 ──

  Scenario: 权限组列表加载
    When 导航到 /data-permission-groups 页面
    Then 页面加载完成，.el-table 可见
    And 表格列包含：名称/备注/工厂数/工作中心数/工序数

  Scenario: 新建权限组
    When 点击"新建权限组"按钮
    And 填写名称 "E2E_PERM_GROUP_001"
    And 填写备注 "E2E测试权限组"
    And 点击对话框"确定"按钮
    Then 表格中出现 "E2E_PERM_GROUP_001"
    And 工厂数/工作中心数/工序数均为 0

  Scenario: 权限组名称必填校验
    When 点击"新建权限组"按钮
    And 不填写名称直接点击"确定"
    Then 表单显示校验提示

  Scenario: 关联工厂（Tab 标签页操作）
    Given 已存在权限组 "E2E_PERM_GROUP_001"
    And 已存在测试工厂
    When 点击权限组的"关联"操作
    Then 关联对话框打开，显示三个 Tab（工厂/工作中心/工序）
    When 切换到"工厂"Tab
    And 勾选测试工厂的 checkbox
    And 点击"确定"
    Then 权限组工厂数更新为 1

  Scenario: 关联工作中心（Tab 切换）
    Given 已关联工厂的权限组 "E2E_PERM_GROUP_001"
    When 打开关联对话框
    And 切换到"工作中心"Tab
    And 勾选工作中心 checkbox
    And 点击"确定"
    Then 工作中心数更新

  Scenario: 编辑权限组基本信息
    When 编辑 "E2E_PERM_GROUP_001"
    And 修改备注为 "E2E测试权限组_编辑"
    And 点击对话框"确定"按钮
    Then 备注更新成功

  Scenario: 删除权限组
    When 删除 "E2E_PERM_GROUP_001"
    And 确认删除
    Then 权限组从列表消失

  # ── 用户权限 ──

  Scenario: 用户权限列表加载
    When 导航到 /user-data-permissions 页面
    Then 页面加载完成，.el-table 可见

  Scenario: 新建用户权限（关联权限组和用户）
    Given 已存在权限组 "E2E_PERM_GROUP_002"
    When 点击新建用户权限按钮
    And 选择权限组 "E2E_PERM_GROUP_002"
    And 选择用户 admin
    And 点击对话框"确定"按钮
    Then 用户权限创建成功

  Scenario: 删除用户权限
    When 删除刚创建的用户权限
    And 确认删除
    Then 用户权限从列表消失
```

### General Checklist
- [ ] 每个页面覆盖列表加载 + 新建 + 编辑 + 删除
- [ ] Tab 标签页切换和关联操作验证
- [ ] 多选 checkbox + 全选 toggle 交互
- [ ] 关联数据计数验证（工厂数/工作中心数/工序数）
- [ ] 测试数据使用 E2E_PERM_ 前缀
- [ ] 测试后清理测试数据
