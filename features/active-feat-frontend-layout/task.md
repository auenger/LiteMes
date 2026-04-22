# Tasks: feat-frontend-layout

## 任务分解

### 1. 状态管理
- [x] 创建 stores/tabs.ts（Tab 页签管理，参考 demo）
- [x] 创建 stores/theme.ts（暗色主题管理，参考 demo）

### 2. 布局组件
- [x] 创建 layouts/MainLayout.vue（主布局）
  - Header 区域（Logo + 面包屑 + 主题切换 + 用户信息）
  - Sidebar 区域（可折叠菜单，el-menu，按模块分组）
  - Tab Bar 区域（Tab 页签，支持关闭）
  - Content 区域（router-view + transition）
- [x] 更新 App.vue 使用 MainLayout

### 3. 路由重构
- [x] 重构 router/index.ts 为嵌套路由结构
- [x] 所有业务路由嵌套在 MainLayout 下
- [x] 配置路由 meta 信息（标题、图标、所属模块）

### 4. 样式与主题
- [x] 配置暗色主题 CSS 变量
- [x] Element Plus 暗色主题集成
- [x] 布局响应式适配

## 进度日志
| 日期 | 进度 | 备注 |
|------|------|------|
| 2026-04-22 | 创建 | 任务分解完成 |
| 2026-04-22 | 完成 | 全部任务实现，构建通过 |
