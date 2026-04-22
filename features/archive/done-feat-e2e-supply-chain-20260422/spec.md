# Feature: feat-e2e-supply-chain 供应链 E2E 测试

## Basic Information
- **ID**: feat-e2e-supply-chain
- **Name**: 供应链 E2E 测试（客户/供应商）
- **Priority**: 70
- **Size**: S
- **Dependencies**: [feat-e2e-material]
- **Parent**: feat-e2e-master-data
- **Children**: []
- **Created**: 2026-04-22

## Description

对供应链模块 2 个页面进行 Playwright E2E 测试：客户管理、供应商管理。覆盖基础 CRUD 和物料关联功能。

### 测试页面
| 页面 | 路由 | 测试文件 |
|------|------|---------|
| 客户管理 | /customers | e2e/supply-chain/customer.spec.ts |
| 供应商管理 | /suppliers | e2e/supply-chain/supplier.spec.ts |

## User Value Points
1. 验证客户信息 CRUD（含类型枚举和物料关联）
2. 验证供应商信息 CRUD（含物料关联）

## Context Analysis

### Reference Code
- 公共登录 fixture：`e2e/fixtures/auth.ts`
- 客户视图：`frontend/src/views/customer/CustomerList.vue` — 字段：编码/名称/类型枚举/物料关联
- 供应商视图：`frontend/src/views/supplier/SupplierList.vue` — 字段：编码/名称/物料关联

### Related Documents
- 父 Feature spec：`features/pending-feat-e2e-master-data/spec.md`
- 兄弟 Feature：`features/pending-feat-e2e-material/spec.md`（提供物料数据依赖）

### Related Features
- feat-e2e-material（兄弟，前置依赖）— 提供物料测试数据
- feat-customer（已完成）— 客户管理实现参考
- feat-supplier（已完成）— 供应商管理实现参考

## Technical Solution
- 测试文件目录：`e2e/supply-chain/`
- 复用公共登录 fixture
- 测试数据前缀：`E2E_SC_`
- 客户/供应商均需关联物料（由 feat-e2e-material 提供测试数据）

## Acceptance Criteria (Gherkin)

```gherkin
Feature: 供应链 E2E 测试

  Background:
    Given 用户已登录 admin 账号

  # ── 客户管理 ──

  Scenario: 客户列表加载
    When 导航到 /customers 页面
    Then 页面加载完成，.el-table 可见

  Scenario: 新建客户
    When 点击"新建客户"按钮
    And 填写客户编码 "E2E_SC_CUST_001"
    And 填写客户名称 "E2E测试客户"
    And 选择客户类型
    And 点击对话框"确定"按钮
    Then 表格中出现 "E2E_SC_CUST_001"

  Scenario: 客户编码必填校验
    When 点击"新建客户"按钮
    And 不填写必填字段直接点击"确定"
    Then 表单显示校验提示

  Scenario: 编辑客户
    When 编辑 "E2E_SC_CUST_001"
    And 修改名称为 "E2E测试客户_编辑"
    And 点击对话框"确定"按钮
    Then 名称更新成功
    And 编码字段为只读

  Scenario: 删除客户
    When 删除 "E2E_SC_CUST_001"
    And 确认删除
    Then 客户从列表消失

  # ── 供应商管理 ──

  Scenario: 供应商列表加载
    When 导航到 /suppliers 页面
    Then 页面加载完成，.el-table 可见

  Scenario: 新建供应商
    When 点击"新建供应商"按钮
    And 填写供应商编码 "E2E_SC_SUP_001"
    And 填写供应商名称 "E2E测试供应商"
    And 点击对话框"确定"按钮
    Then 表格中出现 "E2E_SC_SUP_001"

  Scenario: 供应商编码必填校验
    When 点击"新建供应商"按钮
    And 不填写必填字段直接点击"确定"
    Then 表单显示校验提示

  Scenario: 编辑供应商
    When 编辑 "E2E_SC_SUP_001"
    And 修改名称为 "E2E测试供应商_编辑"
    And 点击对话框"确定"按钮
    Then 名称更新成功
    And 编码字段为只读

  Scenario: 删除供应商
    When 删除 "E2E_SC_SUP_001"
    And 确认删除
    Then 供应商从列表消失
```

### General Checklist
- [ ] 每个页面覆盖列表加载 + 新建 + 编辑 + 删除
- [ ] 表单必填校验场景
- [ ] 编码不可编辑验证
- [ ] 测试数据使用 E2E_SC_ 前缀
- [ ] 测试后清理测试数据

## Merge Record

- **Completed:** 2026-04-22T01:00:00+08:00
- **Merged Branch:** feature/e2e-supply-chain
- **Merge Commit:** de8c2b789f24b4068682da6a18ee04a1f111597d
- **Archive Tag:** feat-e2e-supply-chain-20260422
- **Conflicts:** None
- **Verification:** 10/10 E2E tests passed (47.1s)
- **Stats:** 3 commits, 2 new files (customer.spec.ts, supplier.spec.ts)
