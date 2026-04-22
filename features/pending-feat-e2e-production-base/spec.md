# Feature: feat-e2e-production-base 生产基础 E2E 测试

## Basic Information
- **ID**: feat-e2e-production-base
- **Name**: 生产基础 E2E 测试（工作中心/计量单位/单位换算）
- **Priority**: 70
- **Size**: S
- **Dependencies**: [feat-e2e-org]
- **Parent**: feat-e2e-master-data
- **Children**: []
- **Created**: 2026-04-22

## Description

对生产基础模块 3 个页面进行 Playwright E2E 测试：工作中心、计量单位、单位换算。覆盖完整 CRUD 生命周期、表单校验和级联关联。

### 测试页面
| 页面 | 路由 | 测试文件 |
|------|------|---------|
| 工作中心 | /work-centers | e2e/production/work-center.spec.ts |
| 计量单位 | /uoms | e2e/production/uom.spec.ts |
| 单位换算 | /uom-conversions | e2e/production/uom-conversion.spec.ts |

## User Value Points
1. 验证工作中心 CRUD 及工厂级联关联
2. 验证计量单位管理（含精度字段）
3. 验证单位换算关系配置（源单位→目标单位→换算比例）

## Context Analysis

### Reference Code
- 公共登录 fixture：`e2e/fixtures/auth.ts`（feat-e2e-org 中创建）
- 工作中心视图：`frontend/src/views/work-center/WorkCenterList.vue` — 字段：编码/名称/工厂选择器/状态
- 计量单位视图：`frontend/src/views/uom/UomList.vue` — 字段：编码/名称/精度(number)/状态
- 单位换算视图：`frontend/src/views/uom/UomConversionList.vue` — 字段：源单位/目标单位/换算比例

### Related Documents
- 父 Feature spec：`features/pending-feat-e2e-master-data/spec.md`
- 兄弟 Feature：`features/pending-feat-e2e-org/spec.md`（提供工厂数据依赖）

### Related Features
- feat-e2e-org（兄弟，前置依赖）— 提供公司/工厂测试数据
- feat-e2e-material（兄弟，依赖本 Feature）— 需要计量单位数据
- feat-work-center（已完成）— 工作中心实现参考

## Technical Solution
- 测试文件目录：`e2e/production/`
- 复用公共登录 fixture
- 测试数据前缀：`E2E_PROD_`
- 工作中心测试需先通过 API 或前置步骤创建工厂数据
- 执行顺序：工作中心（需工厂）→ 计量单位（独立）→ 单位换算（需计量单位）

## Acceptance Criteria (Gherkin)

```gherkin
Feature: 生产基础 E2E 测试

  Background:
    Given 用户已登录 admin 账号

  # ── 工作中心 ──

  Scenario: 工作中心列表加载
    When 导航到 /work-centers 页面
    Then 页面加载完成，.el-table 可见
    And 分页组件可见

  Scenario: 新建工作中心成功（关联已有工厂）
    Given 已存在测试工厂（由 feat-e2e-org 提供）
    When 点击"新建工作中心"按钮
    And 填写编码 "E2E_PROD_WC_001"
    And 填写名称 "E2E测试工作中心"
    And 在工厂选择器中选择已有工厂
    And 点击对话框"确定"按钮
    Then 表格中出现 "E2E_PROD_WC_001"

  Scenario: 工作中心编码必填校验
    When 点击"新建工作中心"按钮
    And 不填写任何字段直接点击"确定"
    Then 表单显示必填校验提示

  Scenario: 编辑工作中心
    When 点击 "E2E_PROD_WC_001" 所在行的"编辑"按钮
    And 修改名称为 "E2E测试工作中心_编辑"
    And 点击对话框"确定"按钮
    Then 表格中 "E2E测试工作中心_编辑" 可见
    And 编码字段为只读

  Scenario: 删除工作中心
    When 点击 "E2E_PROD_WC_001" 所在行的"删除"按钮
    And 在确认对话框中点击"删除"
    Then 表格中不再出现 "E2E_PROD_WC_001"

  # ── 计量单位 ──

  Scenario: 计量单位列表加载
    When 导航到 /uoms 页面
    Then 页面加载完成，.el-table 可见

  Scenario: 新建计量单位成功
    When 点击"新建单位"按钮
    And 填写单位编码 "E2E_PROD_UOM_001"
    And 填写单位名称 "E2E测试单位"
    And 填写精度为 2
    And 点击对话框"确定"按钮
    Then 表格中出现 "E2E_PROD_UOM_001"
    And 精度列显示 "2"

  Scenario: 计量单位编码必填校验
    When 点击"新建单位"按钮
    And 不填写任何字段直接点击"确定"
    Then 表单显示必填校验提示

  Scenario: 编辑计量单位
    When 点击 "E2E_PROD_UOM_001" 所在行的"编辑"按钮
    And 修改名称为 "E2E测试单位_编辑"
    And 修改精度为 3
    And 点击对话框"确定"按钮
    Then 名称和精度更新成功
    And 编码字段为只读

  Scenario: 删除计量单位
    When 点击 "E2E_PROD_UOM_001" 所在行的"删除"按钮
    And 在确认对话框中点击"删除"
    Then 表格中不再出现 "E2E_PROD_UOM_001"

  # ── 单位换算 ──

  Scenario: 单位换算列表加载
    When 导航到 /uom-conversions 页面
    Then 页面加载完成，.el-table 可见

  Scenario: 新建单位换算关系
    Given 已存在计量单位 "E2E_PROD_UOM_A" 和 "E2E_PROD_UOM_B"
    When 点击新建换算按钮
    And 选择源单位 "E2E_PROD_UOM_A"
    And 选择目标单位 "E2E_PROD_UOM_B"
    And 填写换算比例为 1000
    And 点击对话框"确定"按钮
    Then 换算关系创建成功

  Scenario: 删除单位换算关系
    When 点击刚创建的换算关系所在行的"删除"按钮
    And 在确认对话框中点击"删除"
    Then 换算关系从列表中消失
```

### General Checklist
- [ ] 每个页面覆盖列表加载 + 新建 + 编辑 + 删除
- [ ] 表单必填校验场景
- [ ] 工作中心关联工厂验证
- [ ] 精度 number 输入验证
- [ ] 单位换算级联选择验证
- [ ] 测试数据使用 E2E_PROD_ 前缀
- [ ] 测试后清理测试数据
