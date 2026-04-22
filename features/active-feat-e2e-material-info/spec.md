# Feature: feat-e2e-material-info 物料信息与免检 E2E 测试

## Basic Information
- **ID**: feat-e2e-material-info
- **Name**: 物料信息与免检清单 E2E 测试
- **Priority**: 70
- **Size**: S
- **Dependencies**: [feat-e2e-material-category]
- **Parent**: feat-e2e-material
- **Children**: []
- **Created**: 2026-04-22

## Description

对物料信息和免检清单 2 个页面进行 Playwright E2E 测试。物料信息包含复杂表单（PCB 属性区 + 扩展字段），免检清单需关联物料和供应商。

### 测试页面
| 页面 | 路由 | 测试文件 |
|------|------|---------|
| 物料信息 | /materials | e2e/material/material.spec.ts |
| 免检清单 | /inspection-exemptions | e2e/material/inspection-exemption.spec.ts |

## User Value Points
1. 验证物料信息完整 CRUD（含分类关联、计量单位关联、PCB 属性、扩展字段）
2. 验证免检清单配置（关联物料和供应商）

## Context Analysis

### Reference Code
- 公共 fixture：`e2e/fixtures/auth.ts`（由 feat-e2e-org-base 创建）
- 公共工具函数：`e2e/helpers/common.ts`（由 feat-e2e-org-base 创建）
- 物料信息视图：`frontend/src/views/material/MaterialList.vue` — 复杂表单：编码/名称/基本分类/物料分类/属性分类/计量单位 + PCB 属性区（尺寸/长度/宽度/型号/规格/厚度/颜色/TG值/铜厚/含铜/直径/刃长/总长） + 扩展字段（Ext1-Ext5）
- 免检清单视图：`frontend/src/views/inspection-exemption/InspectionExemptionList.vue` — 字段：物料选择器/供应商选择器/状态

### Related Documents
- 父 Feature spec：`features/pending-feat-e2e-material/spec.md`

### Related Features
- feat-e2e-material-category（兄弟，前置依赖）— 提供物料分类测试数据
- feat-e2e-supply-chain（兄弟，依赖本 Feature）— 供应商关联物料

## Technical Solution
- 复用 auth fixture 和公共工具函数
- 测试数据前缀：`E2E_MAT_`
- 物料信息测试依赖分类数据（由 feat-e2e-material-category 提供）
- 免检清单测试依赖物料 + 供应商数据

## Acceptance Criteria (Gherkin)

```gherkin
Feature: 物料信息与免检 E2E 测试

  Background:
    Given 用户已通过 auth fixture 自动登录

  # ── 物料信息 ──

  Scenario: 物料信息列表加载
    When 导航到 /materials 页面
    Then 页面加载完成，.el-table 可见

  Scenario: 新建物料（基本信息）
    Given 已存在物料分类 "E2E_MAT_CAT_001"
    And 已存在计量单位
    When 点击"新建物料"按钮
    And 填写物料编码 "E2E_MAT_001"
    And 填写物料名称 "E2E测试物料"
    And 选择基本分类
    And 选择物料分类 "E2E_MAT_CAT_001"
    And 选择属性分类
    And 选择计量单位
    And 点击对话框"确定"按钮
    Then 表格中出现 "E2E_MAT_001"

  Scenario: 新建物料含 PCB 属性
    When 点击"新建物料"按钮
    And 填写基本字段 "E2E_MAT_002"
    And 展开或填写 PCB 属性区域（尺寸/长度/宽度/型号等）
    And 点击对话框"确定"按钮
    Then 物料创建成功

  Scenario: 物料编码必填校验
    When 点击"新建物料"按钮
    And 不填写必填字段直接点击"确定"
    Then 表单显示校验提示

  Scenario: 编辑物料
    When 编辑 "E2E_MAT_001"
    And 修改物料名称为 "E2E测试物料_编辑"
    And 点击对话框"确定"按钮
    Then 名称更新成功
    And 物料编码字段为只读

  Scenario: 删除物料
    When 删除 "E2E_MAT_001"
    And 确认删除
    Then 物料从列表消失

  # ── 免检清单 ──

  Scenario: 免检清单列表加载
    When 导航到 /inspection-exemptions 页面
    Then 页面加载完成，.el-table 可见

  Scenario: 新建免检记录
    Given 已存在物料 "E2E_MAT_003"
    And 已存在供应商
    When 点击新建免检按钮
    And 选择物料 "E2E_MAT_003"
    And 选择供应商
    And 点击对话框"确定"按钮
    Then 免检记录创建成功

  Scenario: 删除免检记录
    When 删除刚创建的免检记录
    And 确认删除
    Then 免检记录从列表消失
```

### General Checklist
- [x] 物料信息多字段表单验证
- [x] PCB 属性区域填写和保存
- [x] 分类/计量单位下拉选择验证
- [x] 免检清单物料关联
- [x] 测试数据使用 E2E_MAT_ 前缀
- [x] 测试后清理测试数据
