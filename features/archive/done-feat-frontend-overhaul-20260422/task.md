# feat-frontend-overhaul — 任务清单

## 子 Feature 执行顺序

### 1. feat-frontend-ui-lib (优先级最高)
- [x] 安装 Element Plus + Tailwind CSS v4
- [x] 配置 unplugin-vue-components / unplugin-auto-import
- [x] 全局样式重构
- [x] 已有页面迁移至 Element Plus 组件

### 2. feat-frontend-layout
- [x] 侧边栏导航组件（支持多级菜单折叠）
- [x] 面包屑导航
- [x] Tab 页签管理
- [x] 深色主题切换
- [x] MainLayout 整合

### 3. feat-process-frontend
- [x] 工序管理 API 对接
- [x] 工序列表页（搜索、分页、状态切换）
- [x] 工序新增/编辑对话框
- [x] 路由注册

### 4. feat-cascading-selector
- [x] 级联选择器组件（公司→工厂→部门）
- [x] v-model 双向绑定
- [x] 替换已有页面中的独立下拉选择

### 5. feat-auth-frontend
- [x] 登录页面
- [x] JWT Token 管理（localStorage + Pinia Store）
- [x] 路由守卫（beforeEach）
- [x] 用户信息 Store
