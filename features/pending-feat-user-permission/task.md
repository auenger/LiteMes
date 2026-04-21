# Tasks: feat-user-permission 用户数据权限

## 任务清单

### 后端
- [ ] UserDataPermission 实体与数据库表
- [ ] UserDataPermissionFactory 关联实体与表
- [ ] UserDataPermissionWorkCenter 关联实体与表
- [ ] UserDataPermissionProcess 关联实体与表
- [ ] 用户权限列表查询 API（支持用户名/姓名模糊查询）
- [ ] 批量添加权限 API（多用户 + 权限组 → 批量赋值）
- [ ] 用户级工厂/工作中心/工序 CRUD API
- [ ] Excel 导入/导出 API
- [ ] SqlSugar 全局查询过滤器实现
- [ ] 当前用户权限上下文注入（登录时加载权限缓存）

### 前端
- [ ] 用户数据权限列表页面（查询/重置）
- [ ] 批量添加权限弹窗（勾选用户 → 选择权限组 → 确认）
- [ ] 工厂 Tab 页（选择工厂弹窗，多选分页勾选）
- [ ] 工作中心 Tab 页（选择工作中心弹窗，多选分页勾选）
- [ ] 工序 Tab 页（选择工序弹窗，多选分页勾选）
- [ ] Excel 导入/导出功能

### 通用
- [ ] 审计字段自动填充
- [ ] 权限上下文中间件/拦截器

---

## 进度记录
| 日期 | 进度 | 备注 |
|------|------|------|
| 2026-04-21 | 已拆分 | 从 feat-data-permission 拆分 |
