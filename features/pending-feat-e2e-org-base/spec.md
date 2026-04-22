# Feature: feat-e2e-org-base 组织架构基础 E2E 测试

## Basic Information
- **ID**: feat-e2e-org-base
- **Name**: 组织架构基础 E2E 测试（公司/工厂 + 公共 Fixture）
- **Priority**: 75
- **Size**: S
- **Dependencies**: []
- **Parent**: feat-e2e-org
- **Children**: []
- **Created**: 2026-04-22

## Description

对组织架构基础模块 2 个页面进行 Playwright E2E 测试：公司管理、工厂管理。同时负责创建所有 E2E 测试共享的公共基础设施（认证 fixture + 工具函数）。

### 测试页面
| 页面 | 路由 | 测试文件 |
|------|------|---------|
| 公司管理 | /companies | e2e/org/company.spec.ts |
| 工厂管理 | /factories | e2e/org/factory.spec.ts |

### 公共基础设施
| 文件 | 说明 |
|------|------|
| e2e/fixtures/auth.ts | Playwright test.extend 认证 fixture |
| e2e/helpers/common.ts | 公共工具函数（表格断言、对话框操作等）|

## User Value Points
1. 创建所有 E2E 测试共享的认证 fixture，消除登录代码重复
2. 验证公司管理完整 CRUD（创建/列表/编辑/删除/状态切换）
3. 验证工厂管理 CRUD 及公司级联关联

## Context Analysis

### Reference Code
- 现有冒烟测试：`frontend/e2e/auth.spec.ts`, `frontend/e2e/company-crud.spec.ts`（登录 + 公司新建模式参考）
- Playwright 配置：`frontend/playwright.config.ts`（baseURL: localhost:3000, timeout: 30000）
- 公司视图：`frontend/src/views/company/CompanyList.vue` — 字段：编码/名称/简码/状态
- 工厂视图：`frontend/src/views/factory/FactoryList.vue` — 字段：编码/名称/简称/公司选择器/状态

### Related Documents
- 父 Feature spec：`features/pending-feat-e2e-org/spec.md`

### Related Features
- feat-e2e-smoke-test（已完成）— 登录 + 公司冒烟测试模式
- feat-e2e-org-advanced（兄弟，依赖本 Feature）— 需要 auth fixture

## Technical Solution
- 创建 Playwright `test.extend()` 认证 fixture，封装完整登录流程
- 提取公共工具函数：findTableRow、confirmDialog、deleteWithConfirm、waitForToast
- 测试数据前缀：`E2E_ORG_`
- 执行顺序：公司（独立）→ 工厂（依赖公司）

## Acceptance Criteria (Gherkin)

```gherkin
Feature: 组织架构基础 E2E 测试

  Background:
    Given 用户已通过 auth fixture 自动登录

  # ── 公司管理 ──

  Scenario: 公司列表加载
    When 导航到 /companies 页面
    Then 页面加载完成，.el-table 可见
    And 分页组件 .el-pagination 可见

  Scenario: 新建公司成功
    When 点击"新建公司"按钮
    And 填写公司编码 "E2E_ORG_CO_001"
    And 填写公司名称 "E2E测试公司"
    And 填写简码 "E2E"
    And 点击对话框"确定"按钮
    Then 表格中出现 "E2E_ORG_CO_001"
    And 表格中出现 "E2E测试公司"

  Scenario: 公司编码必填校验
    When 点击"新建公司"按钮
    And 不填写公司编码直接点击"确定"
    Then 表单显示编码必填校验提示

  Scenario: 编辑公司
    When 点击 "E2E_ORG_CO_001" 所在行的"编辑"按钮
    And 修改公司名称为 "E2E测试公司_编辑"
    And 点击对话框"确定"按钮
    Then 表格中 "E2E测试公司_编辑" 可见
    And 公司编码字段为只读

  Scenario: 删除公司
    When 点击 "E2E_ORG_CO_001" 所在行的"删除"按钮
    And 在确认对话框中点击"删除"
    Then 表格中不再出现 "E2E_ORG_CO_001"

  # ── 工厂管理 ──

  Scenario: 工厂列表加载
    When 导航到 /factories 页面
    Then 页面加载完成，.el-table 可见

  Scenario: 新建工厂成功（关联已有公司）
    Given 已存在测试公司 "E2E_ORG_CO_002"
    When 点击"新建工厂"按钮
    And 填写工厂编码 "E2E_ORG_FAC_001"
    And 填写工厂名称 "E2E测试工厂"
    And 在公司选择器中选择 "E2E_ORG_CO_002"
    And 点击对话框"确定"按钮
    Then 表格中出现 "E2E_ORG_FAC_001"

  Scenario: 工厂编码必填校验
    When 点击"新建工厂"按钮
    And 不填写任何字段直接点击"确定"
    Then 表单显示必填校验提示

  Scenario: 编辑工厂
    When 点击 "E2E_ORG_FAC_001" 所在行的"编辑"按钮
    And 修改工厂名称为 "E2E测试工厂_编辑"
    And 点击对话框"确定"按钮
    Then 表格中 "E2E测试工厂_编辑" 可见
    And 工厂编码字段为只读

  Scenario: 删除工厂
    When 点击 "E2E_ORG_FAC_001" 所在行的"删除"按钮
    And 在确认对话框中点击"删除"
    Then 表格中不再出现 "E2E_ORG_FAC_001"
```

### General Checklist
- [ ] auth fixture 可被其他 spec 文件导入使用
- [ ] 公共工具函数封装完整
- [ ] 每个页面覆盖列表加载 + 新建 + 编辑 + 删除
- [ ] 表单必填校验场景
- [ ] 公司→工厂级联关系验证
- [ ] 测试数据使用 E2E_ORG_ 前缀
- [ ] 测试后清理测试数据
