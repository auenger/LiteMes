# Feature: feat-frontend-ui-lib UI 组件库引入与样式重构

## 基本信息
- **ID**: feat-frontend-ui-lib
- **名称**: UI 组件库引入与样式重构
- **优先级**: 95
- **规模**: L
- **依赖**: 无
- **父模块**: feat-frontend-overhaul
- **创建时间**: 2026-04-22

## 需求来源
- 前端审计发现所有 View 文件含约 200 行重复 CSS
- 参考 demo/litemes 实现：Element Plus + Tailwind CSS v4
- 清理脚手架文件（api/example.ts, components/HelloWorld.vue）

## 需求描述
引入 Element Plus UI 组件库和 Tailwind CSS v4，配置自动导入插件，将所有现有视图从手写 HTML/CSS 重构为 Element Plus 组件 + Tailwind 工具类。清理脚手架示例文件。此 Feature 是所有其他前端子 Feature 的前置依赖。

## 用户价值点

### VP1: 统一 UI 风格
所有页面使用 Element Plus 组件（el-table, el-form, el-dialog, el-button, el-pagination, el-tag 等），消除样式不一致问题。

### VP2: 开发效率提升
通过 unplugin-auto-import + unplugin-vue-components 实现组件自动导入，开发新页面时无需手动 import 组件。Tailwind 工具类替代手写 CSS。

### VP3: 代码量精简
消除每个 View 文件中约 200 行重复 CSS（按钮、表格、对话框、分页、状态标签等），预估总体减少 3000+ 行 CSS。

## 技术方案

### 依赖安装
```bash
npm install element-plus @element-plus/icons-vue
npm install -D tailwindcss @tailwindcss/vite unplugin-auto-import unplugin-vue-components
```

### vite.config.ts 配置
```typescript
import tailwindcss from '@tailwindcss/vite'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
```

### 重构范围（19 个 View 文件）
| 模块 | View 文件 | 重构要点 |
|------|----------|---------|
| 公司管理 | CompanyList.vue | el-table + el-dialog + el-form |
| 工厂管理 | FactoryList.vue | el-table + 下拉选择 |
| 部门管理 | DepartmentList.vue | el-table + el-tree-select |
| 部门用户 | DepartmentUserList.vue | el-table + el-transfer |
| 班制班次 | ShiftScheduleList.vue | el-table + el-time-picker |
| 工作中心 | WorkCenterList.vue | el-table + 下拉选择 |
| 计量单位 | UomList.vue | el-table |
| 单位换算 | UomConversionList.vue | el-table + 下拉选择 |
| 物料分类 | MaterialCategoryList.vue | el-table + el-tree |
| 物料信息 | MaterialList.vue | el-table + el-tabs(版本) |
| 免检清单 | InspectionExemptionList.vue | el-table + el-date-picker |
| 设备类型 | EquipmentTypeList.vue | el-table |
| 设备型号 | EquipmentModelList.vue | el-table + 下拉选择 |
| 设备台账 | EquipmentLedgerList.vue | el-table + 下拉选择 |
| 客户管理 | CustomerList.vue | el-table + el-dialog |
| 供应商管理 | SupplierList.vue | el-table + el-dialog |
| 权限组 | DataPermissionGroupList.vue | el-table + el-tabs |
| 用户权限 | UserDataPermissionList.vue | el-table + el-dialog |
| Dashboard | Dashboard.vue | el-card + el-statistic |

### 清理文件
- 删除 `api/example.ts`
- 删除 `components/HelloWorld.vue`

### 参考模式（来自 demo/litemes/src/views/Customer.vue）
```vue
<el-form :inline="true" :model="filters" size="default">
  <el-form-item label="客户编码">
    <el-input v-model="filters.code" placeholder="请输入" clearable />
  </el-form-item>
  <el-button type="primary" :icon="Search" @click="handleQuery">查询</el-button>
</el-form>

<el-table :data="tableData" border stripe>
  <el-table-column prop="code" label="编码" width="120" />
  <el-table-column label="操作" fixed="right">
    <template #default>
      <el-button link type="primary" size="small">编辑</el-button>
    </template>
  </el-table-column>
</el-table>

<el-pagination layout="total, sizes, prev, pager, next" :total="total" background />
```

## 验收标准 (Gherkin)

```gherkin
Feature: UI 组件库引入与样式重构

  Background:
    Given 前端项目已安装 Element Plus 和 Tailwind CSS
    And vite.config.ts 已配置自动导入插件

  Scenario: 组件库正确加载
    Given 启动前端开发服务器
    When 访问任意页面
    Then Element Plus 组件样式正确渲染
    And Tailwind 工具类生效

  Scenario: 无重复 CSS
    Given 所有 View 文件已完成重构
    When 检查任意 View 文件的 <style> 块
    Then 不包含 .btn, .data-table, .dialog-overlay, .pagination, .status-tag 等通用样式
    And 仅包含页面特有的自定义样式

  Scenario: 脚手架文件已清理
    Given 重构已完成
    Then api/example.ts 文件不存在
    And components/HelloWorld.vue 文件不存在

  Scenario: 现有功能不受影响
    Given 重构已完成
    When 执行每个模块的 CRUD 操作
    Then 所有功能与重构前行为一致
```

## 通用检查清单
- [ ] element-plus 已安装并配置
- [ ] tailwindcss v4 已安装并配置
- [ ] unplugin-auto-import 和 unplugin-vue-components 已配置
- [ ] @element-plus/icons-vue 已安装
- [ ] 所有 19 个 View 文件已重构
- [ ] 无重复 CSS
- [ ] 脚手架文件已清理
- [ ] 所有 CRUD 功能正常

## Merge Record
- **completed**: 2026-04-22T20:30:00+08:00
- **merged_branch**: feature/feat-frontend-ui-lib
- **merge_commit**: 4b734b2e66245b09df55b537c78c04e5fffdc7ed
- **archive_tag**: feat-frontend-ui-lib-20260422
- **conflicts**: none
- **verification**: build passed (vite build success, vue-tsc clean)
- **stats**: started 2026-04-22, duration < 1 day, commits 1, files_changed 31, net_lines -6071
