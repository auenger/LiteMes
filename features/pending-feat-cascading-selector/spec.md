# Feature: feat-cascading-selector 级联选择器

## 基本信息
- **ID**: feat-cascading-selector
- **名称**: 级联选择器（公司→工厂→部门）
- **优先级**: 70
- **规模**: S
- **依赖**: feat-frontend-ui-lib
- **父模块**: feat-frontend-overhaul
- **创建时间**: 2026-04-22

## 需求来源
- feat-enterprise-common 要求「公司→工厂→部门级联查询接口」
- 当前 dropdown.ts 已有独立的下拉 API，但无级联组件
- 多个模块（工厂管理、部门管理、数据权限等）需要级联选择

## 需求描述
创建公司→工厂→部门三级级联选择器组件，基于 Element Plus 的 el-cascader 或三个联动 el-select 实现。选择公司后自动加载工厂选项，选择工厂后自动加载部门选项。

## 用户价值点

### VP1: 级联数据选择
用户通过级联选择器一次性完成公司→工厂→部门的选择，避免多次独立下拉查询。

### VP2: 数据过滤联动
上级选择自动过滤下级选项，减少无效选择。

## 技术方案

### 后端 API（已存在于 DropdownResource）
- GET /api/dropdown/companies — 公司列表
- GET /api/dropdown/factories?companyId={id} — 按公司过滤工厂
- GET /api/dropdown/departments?factoryId={id} — 按工厂过滤部门

### 组件设计
```vue
<!-- components/CascadingSelector.vue -->
<template>
  <div class="flex gap-2">
    <el-select v-model="companyId" placeholder="选择公司" @change="onCompanyChange">
      <el-option v-for="c in companies" :key="c.id" :label="c.name" :value="c.id" />
    </el-select>
    <el-select v-model="factoryId" placeholder="选择工厂" @change="onFactoryChange">
      <el-option v-for="f in factories" :key="f.id" :label="f.name" :value="f.id" />
    </el-select>
    <el-select v-model="departmentId" placeholder="选择部门">
      <el-option v-for="d in departments" :key="d.id" :label="d.name" :value="d.id" />
    </el-select>
  </div>
</template>
```

### 需要创建的文件
- `components/CascadingSelector.vue` — 级联选择器组件
  - Props: modelValue (companyId, factoryId, departmentId)
  - Emits: update:modelValue, change
  - 支持只选择部分层级（如仅公司+工厂）

### 需要更新的文件
- 各模块的搜索表单中可使用此组件替代独立下拉

## 验收标准 (Gherkin)

```gherkin
Feature: 级联选择器

  Scenario: 级联联动
    Given 级联选择器组件已加载
    When 选择某个公司
    Then 工厂下拉框自动加载该公司下的工厂
    And 部门下拉框清空
    When 选择某个工厂
    Then 部门下拉框自动加载该工厂下的部门

  Scenario: 上级变更清空下级
    Given 已选择公司A、工厂B、部门C
    When 将公司切换为公司D
    Then 工厂下拉清空并加载公司D的工厂
    And 部门下拉清空
```
