# Feature: feat-auth-frontend 登录认证前端

## 基本信息
- **ID**: feat-auth-frontend
- **名称**: 登录认证前端
- **优先级**: 60
- **规模**: S
- **依赖**: feat-frontend-layout
- **父模块**: feat-frontend-overhaul
- **创建时间**: 2026-04-22

## 需求来源
- http.ts 已有 JWT 拦截器（401 → 跳转 /login）
- 但无登录页面，无 /login 路由
- stores/user.ts 仅有框架代码

## 需求描述
实现登录页面和 JWT 认证流程。包括登录表单（用户名+密码）、JWT Token 存储、路由守卫（未登录跳转登录页）、登录状态管理。配合后端 SmallRye JWT 认证。

## 用户价值点

### VP1: 安全认证闭环
完成从登录到鉴权的完整闭环，未认证用户无法访问业务页面。

### VP2: 登录状态持久化
Token 存储在 localStorage，刷新页面后无需重新登录。

## 技术方案

### 后端 API（需确认或创建）
- POST /api/auth/login — 登录（username + password → JWT token）
- GET /api/auth/userinfo — 获取当前用户信息

### 需要创建的文件
- `views/auth/Login.vue` — 登录页面（el-form + 居中布局）
- `router/guards.ts` — 路由守卫逻辑

### 需要更新的文件
- `stores/user.ts` — 完善用户状态管理（login/logout/userinfo）
- `router/index.ts` — 添加 /login 路由 + 路由守卫
- `http.ts` — 调整 401 处理逻辑

### 登录页设计
```
┌──────────────────────────────────┐
│                                  │
│         ┌──────────────┐         │
│         │  LiteMes Logo │        │
│         │              │         │
│         │  ┌──────────┐│         │
│         │  │ 用户名    ││         │
│         │  └──────────┘│         │
│         │  ┌──────────┐│         │
│         │  │ 密码      ││         │
│         │  └──────────┘│         │
│         │  [ 记住我 ]  │         │
│         │  ┌──────────┐│         │
│         │  │  登 录    ││         │
│         │  └──────────┘│         │
│         └──────────────┘         │
│                                  │
└──────────────────────────────────┘
```

## 验收标准 (Gherkin)

```gherkin
Feature: 登录认证前端

  Scenario: 正常登录
    Given 用户在登录页
    When 输入正确的用户名和密码并点击登录
    Then 跳转到首页
    And 顶部显示用户信息

  Scenario: 登录失败
    When 输入错误的用户名或密码并点击登录
    Then 显示登录失败提示
    And 页面停留在登录页

  Scenario: 未认证访问
    Given 用户未登录
    When 直接访问任意业务页面 URL
    Then 自动跳转到登录页
    And 登录成功后跳回原页面

  Scenario: 退出登录
    Given 用户已登录
    When 点击退出登录
    Then 清除 Token
    And 跳转到登录页
```
