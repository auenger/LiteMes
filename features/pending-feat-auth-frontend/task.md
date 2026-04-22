# Tasks: feat-auth-frontend

## 任务分解

### 1. 登录页面
- [ ] 创建 views/auth/Login.vue
  - 居中卡片布局
  - 用户名/密码输入框（el-form + el-input）
  - 表单校验（必填）
  - 登录按钮 + loading 状态
  - 登录失败提示

### 2. 状态管理
- [ ] 完善 stores/user.ts
  - login(username, password) — 登录
  - logout() — 退出
  - getUserInfo() — 获取用户信息
  - isAuthenticated 计算属性
  - Token 管理（localStorage）

### 3. 路由守卫
- [ ] 创建 router/guards.ts
  - beforeEach 守卫：未登录跳转 /login
  - 已登录访问 /login 跳转首页
  - 记录原路径，登录后跳回
- [ ] 更新 router/index.ts 集成守卫

### 4. API
- [ ] 创建或更新 api/auth.ts（登录接口）
- [ ] 确认后端登录 API 是否已实现

## 进度日志
| 日期 | 进度 | 备注 |
|------|------|------|
| 2026-04-22 | 创建 | 任务分解完成 |
