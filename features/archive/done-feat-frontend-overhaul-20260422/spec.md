# feat-frontend-overhaul — 前端全面升级

## 概述
对 LiteMes 前端进行全面升级，引入 Element Plus + Tailwind CSS，重构导航布局，完善各模块前端页面与组件。

## 拆分策略
本 Feature 为父级索引，拆分为 5 个子 Feature 并行开发：

| 子 Feature | 说明 | 状态 |
|------------|------|------|
| feat-frontend-ui-lib | UI 组件库引入与样式重构（Element Plus + Tailwind CSS） | completed |
| feat-frontend-layout | 导航布局（侧边栏 + 面包屑 + Tab 页签） | completed |
| feat-process-frontend | 工序管理前端（API + View + Route） | completed |
| feat-cascading-selector | 级联选择器（公司→工厂→部门） | completed |
| feat-auth-frontend | 登录认证前端（登录页 + JWT + 路由守卫） | completed |

## 技术要点
- Element Plus 组件库引入与按需加载（unplugin-vue-components / unplugin-auto-import）
- Tailwind CSS v4 集成
- 侧边栏导航 + 面包屑 + Tab 页签的多层导航体系
- 深色主题支持
- 级联选择器组件封装（公司→工厂→部门联动）
- JWT 认证 + 路由守卫 + 登录页

## 归档信息
- 完成时间: 2026-04-22
- 所有子 Feature 均已合并至 main 分支
