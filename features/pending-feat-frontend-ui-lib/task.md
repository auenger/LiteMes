# Tasks: feat-frontend-ui-lib

## 任务分解

### 1. 基础设施配置
- [ ] 安装 Element Plus 及相关依赖
- [ ] 安装 Tailwind CSS v4 及相关依赖
- [ ] 配置 vite.config.ts（自动导入 + Tailwind 插件）
- [ ] 配置 main.ts（引入 Element Plus 样式）
- [ ] 创建全局 CSS 变量和主题配置（参考 demo）

### 2. 通用组件提取
- [ ] 提取通用搜索栏组件（SearchBar）
- [ ] 提取通用表格操作栏组件（TableToolbar）
- [ ] 保留并优化 AuditLogDialog.vue（改用 el-dialog）
- [ ] 保留并优化 TableSettingsPanel.vue（改用 Element Plus 组件）
- [ ] 保留并优化 TreeNode.vue（改用 el-tree-node 或 el-tree）

### 3. 企业模块重构
- [ ] CompanyList.vue — el-table + el-dialog + el-form
- [ ] FactoryList.vue — el-table + el-select(公司下拉)
- [ ] DepartmentList.vue — el-table + el-tree-select(上级部门)
- [ ] DepartmentUserList.vue — el-table + el-transfer 或多选
- [ ] ShiftScheduleList.vue — el-table + el-time-picker + 嵌套班次管理
- [ ] WorkCenterList.vue — el-table + el-select(工厂下拉)

### 4. 物料模块重构
- [ ] UomList.vue — el-table
- [ ] UomConversionList.vue — el-table + el-select(单位下拉)
- [ ] MaterialCategoryList.vue — el-table + el-tree(分类树)
- [ ] MaterialList.vue — el-table + el-tabs(版本管理)
- [ ] InspectionExemptionList.vue — el-table + el-date-picker

### 5. 设备模块重构
- [ ] EquipmentTypeList.vue — el-table
- [ ] EquipmentModelList.vue — el-table + el-select(类型下拉)
- [ ] EquipmentLedgerList.vue — el-table + el-select(型号/工厂下拉)

### 6. 供应链模块重构
- [ ] CustomerList.vue — el-table + el-dialog(物料关联)
- [ ] SupplierList.vue — el-table + el-dialog(物料关联)

### 7. 权限模块重构
- [ ] DataPermissionGroupList.vue — el-table + el-tabs(工厂/工作中心/工序)
- [ ] UserDataPermissionList.vue — el-table + el-dialog(批量授权)

### 8. 其他页面重构
- [ ] Dashboard.vue — el-card + el-statistic 或空状态提示

### 9. 清理
- [ ] 删除 api/example.ts
- [ ] 删除 components/HelloWorld.vue
- [ ] 删除各 View 中重复的 CSS

## 进度日志
| 日期 | 进度 | 备注 |
|------|------|------|
| 2026-04-22 | 创建 | 任务分解完成 |
