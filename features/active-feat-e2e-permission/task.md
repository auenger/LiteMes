# Tasks: feat-e2e-permission

## Task Breakdown

### 1. 权限组管理测试 — `e2e/permission/permission-group.spec.ts`
- [ ] 权限组列表加载测试 — 导航到 /data-permission-groups，断言表格可见 + 列包含名称/备注/工厂数/工作中心数/工序数 — Scenario: 权限组列表加载
- [ ] 新建权限组测试 — 填写名称/备注，断言成功 + 计数均为 0 — Scenario: 新建权限组
- [ ] 权限组名称必填校验 — 空表单提交，断言校验提示 — Scenario: 权限组名称必填校验
- [ ] 关联工厂测试（Tab 操作）— 点击关联 → 切换工厂 Tab → 勾选 checkbox → 确定，断言工厂数更新 — Scenario: 关联工厂
- [ ] 关联工作中心测试（Tab 切换）— 打开关联 → 切换工作中心 Tab → 勾选 → 确定，断言计数更新 — Scenario: 关联工作中心
- [ ] 编辑权限组测试 — 修改备注，断言更新 — Scenario: 编辑权限组基本信息
- [ ] 删除权限组测试 — 删除 + 确认，断言消失 — Scenario: 删除权限组

### 2. 用户权限测试 — `e2e/permission/user-permission.spec.ts`
- [ ] 用户权限列表加载测试 — 导航到 /user-data-permissions，断言表格可见 — Scenario: 用户权限列表加载
- [ ] 新建用户权限测试 — 选择权限组和用户，断言成功 — Scenario: 新建用户权限
- [ ] 删除用户权限测试 — 删除 + 确认，断言消失 — Scenario: 删除用户权限

### 3. 数据清理
- [ ] 每个 spec 实现 afterEach 清理逻辑（反向依赖：用户权限 → 权限组）

## Progress Log
| Date | Progress | Notes |
|------|----------|-------|
| 2026-04-22 | Feature 创建 | 等待开发 |
