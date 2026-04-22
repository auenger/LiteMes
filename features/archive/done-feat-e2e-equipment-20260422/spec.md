# Feature: feat-e2e-equipment 设备管理 E2E 测试

## Basic Information
- **ID**: feat-e2e-equipment
- **Name**: 设备管理 E2E 测试（设备类型/设备型号/设备台账）
- **Priority**: 70
- **Size**: S
- **Dependencies**: [feat-e2e-org]
- **Parent**: feat-e2e-master-data
- **Children**: []
- **Created**: 2026-04-22

## Description

对设备管理模块 3 个页面进行 Playwright E2E 测试：设备类型、设备型号、设备台账。覆盖级联选择器（类型→型号）、日期选择器和多种状态管理。

### 测试页面
| 页面 | 路由 | 测试文件 |
|------|------|---------|
| 设备类型 | /equipment-types | e2e/equipment/equipment-type.spec.ts |
| 设备型号 | /equipment-models | e2e/equipment/equipment-model.spec.ts |
| 设备台账 | /equipment-ledger | e2e/equipment/equipment-ledger.spec.ts |

## User Value Points
1. 验证设备类型 CRUD
2. 验证设备型号 CRUD（关联设备类型）
3. 验证设备台账 CRUD（级联类型→型号选择器、运行状态、管理状态、工厂关联、日期选择）

## Context Analysis

### Reference Code
- 公共登录 fixture：`e2e/fixtures/auth.ts`
- 设备类型视图：`frontend/src/views/equipment-type/EquipmentTypeList.vue` — 字段：编码/名称/状态
- 设备型号视图：`frontend/src/views/equipment-model/EquipmentModelList.vue` — 字段：编码/名称/设备类型选择器/状态
- 设备台账视图：`frontend/src/views/equipment-ledger/EquipmentLedgerList.vue` — 复杂表单：编码/名称/设备类型选择器（级联到型号）/运行状态(RUNNING/FAULT/SHUTDOWN/MAINTENANCE)/管理状态(IN_USE/IDLE/SCRAPPED)/工厂选择器/制造商/投用日期(date picker)

### Related Documents
- 父 Feature spec：`features/pending-feat-e2e-master-data/spec.md`
- 兄弟 Feature：`features/pending-feat-e2e-org/spec.md`（提供工厂数据）

### Related Features
- feat-e2e-org（兄弟，前置依赖）— 提供工厂测试数据
- feat-equipment-ledger（已完成）— 设备台账实现参考，含级联选择器模式

## Technical Solution
- 测试文件目录：`e2e/equipment/`
- 复用公共登录 fixture
- 测试数据前缀：`E2E_EQ_`
- 执行顺序：设备类型（独立）→ 设备型号（依赖类型）→ 设备台账（依赖型号 + 工厂）

## Acceptance Criteria (Gherkin)

```gherkin
Feature: 设备管理 E2E 测试

  Background:
    Given 用户已登录 admin 账号

  # ── 设备类型 ──

  Scenario: 设备类型列表加载
    When 导航到 /equipment-types 页面
    Then 页面加载完成，.el-table 可见

  Scenario: 新建设备类型
    When 点击"新建设备类型"按钮
    And 填写编码 "E2E_EQ_TYPE_001"
    And 填写名称 "E2E测试设备类型"
    And 点击对话框"确定"按钮
    Then 表格中出现 "E2E_EQ_TYPE_001"

  Scenario: 设备类型编码必填校验
    When 点击"新建设备类型"按钮
    And 不填写编码直接点击"确定"
    Then 表单显示校验提示

  Scenario: 编辑设备类型
    When 编辑 "E2E_EQ_TYPE_001"
    And 修改名称为 "E2E测试设备类型_编辑"
    And 点击对话框"确定"按钮
    Then 名称更新成功
    And 编码字段为只读

  Scenario: 删除设备类型
    When 删除 "E2E_EQ_TYPE_001"
    And 确认删除
    Then 类型从列表消失

  # ── 设备型号 ──

  Scenario: 设备型号列表加载
    When 导航到 /equipment-models 页面
    Then 页面加载完成，.el-table 可见

  Scenario: 新建设备型号（关联已有设备类型）
    Given 已存在设备类型 "E2E_EQ_TYPE_002"
    When 点击"新建设备型号"按钮
    And 填写编码 "E2E_EQ_MODEL_001"
    And 填写名称 "E2E测试型号"
    And 在设备类型选择器中选择 "E2E_EQ_TYPE_002"
    And 点击对话框"确定"按钮
    Then 表格中出现 "E2E_EQ_MODEL_001"

  Scenario: 编辑设备型号
    When 编辑 "E2E_EQ_MODEL_001"
    And 修改名称为 "E2E测试型号_编辑"
    And 点击对话框"确定"按钮
    Then 名称更新成功
    And 编码字段为只读

  Scenario: 删除设备型号
    When 删除 "E2E_EQ_MODEL_001"
    And 确认删除
    Then 型号从列表消失

  # ── 设备台账 ──

  Scenario: 设备台账列表加载
    When 导航到 /equipment-ledger 页面
    Then 页面加载完成，.el-table 可见

  Scenario: 新建设备（级联选择器 + 完整表单）
    Given 已存在设备类型 "E2E_EQ_TYPE_003" 及对应型号 "E2E_EQ_MODEL_002"
    And 已存在测试工厂
    When 点击"新建设备"按钮
    And 填写设备编码 "E2E_EQ_LEDGER_001"
    And 填写设备名称 "E2E测试设备"
    And 在设备类型选择器中选择 "E2E_EQ_TYPE_003"
    And 设备型号选择器自动过滤并选择 "E2E_EQ_MODEL_002"
    And 选择运行状态为 "RUNNING"
    And 选择管理状态为 "IN_USE"
    And 选择工厂
    And 填写制造商 "E2E测试厂商"
    And 选择投用日期
    And 点击对话框"确定"按钮
    Then 表格中出现 "E2E_EQ_LEDGER_001"

  Scenario: 级联选择器联动验证
    When 新建设备时选择设备类型后
    Then 设备型号下拉框仅显示该类型下的型号

  Scenario: 编辑设备台账
    When 编辑 "E2E_EQ_LEDGER_001"
    And 修改设备名称为 "E2E测试设备_编辑"
    And 修改运行状态为 "MAINTENANCE"
    And 点击对话框"确定"按钮
    Then 设备信息更新成功
    And 编码字段为只读

  Scenario: 删除设备台账
    When 删除 "E2E_EQ_LEDGER_001"
    And 确认删除
    Then 设备从列表消失
```

### General Checklist
- [x] 每个页面覆盖列表加载 + 新建 + 编辑 + 删除
- [x] 级联选择器联动验证（设备类型→型号）
- [x] 运行状态/管理状态枚举值验证
- [x] 日期选择器交互
- [x] 工厂关联验证
- [x] 测试数据使用 E2E_EQ_ 前缀
- [x] 测试后清理测试数据

## Merge Record
- **Completed**: 2026-04-22
- **Merged Branch**: feature/e2e-equipment
- **Merge Commit**: 3c273f1
- **Archive Tag**: feat-e2e-equipment-20260422
- **Conflicts**: none
- **Verification**: 14/14 E2E tests passed
- **Stats**: 2 commits, 3 files changed, 637 insertions
