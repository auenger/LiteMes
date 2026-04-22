# Tasks: feat-cascading-selector

## 任务分解

### 1. 级联选择器组件
- [x] 创建 components/CascadingSelector.vue
  - 三级联动 el-select（公司→工厂→部门）
  - 级联加载逻辑（选择上级后自动加载下级选项）
  - v-model 双向绑定
  - 支持部分层级选择（level prop: 1/2/3）
  - 支持清空

### 2. API 对接
- [x] 确认 dropdown.ts 中已有级联所需的 API 方法
  - getCompanyDropdown(), getFactoryDropdown(companyId), getDepartmentDropdown(factoryId) 已存在
  - 无需补充

### 3. 集成
- [ ] 在需要级联选择的模块中引入组件（可选，可在后续迭代中完成）

## 进度日志
| 日期 | 进度 | 备注 |
|------|------|------|
| 2026-04-22 | 创建 | 任务分解完成 |
| 2026-04-22 | 完成 | Task 1 + Task 2 完成，CascadingSelector.vue 已创建 |
