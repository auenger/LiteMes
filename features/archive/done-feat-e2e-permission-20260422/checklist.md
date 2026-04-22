# Checklist: feat-e2e-permission

## Completion Checklist

### Development
- [x] 2 个 spec 文件编写完成（permission-group/user-permission）
- [x] 复用 auth fixture 和公共工具函数
- [x] 代码自测通过

### Code Quality
- [x] 测试命名规范统一
- [x] 测试数据隔离（E2E_PERM_ 前缀）
- [x] 无硬编码等待

### Testing — 权限组管理
- [x] 列表加载（含工厂数/工作中心数/工序数列）
- [x] 新建权限组（名称 + 备注）
- [x] 名称必填校验
- [x] 关联工厂（Tab 切换 + 多选 checkbox + 全选 toggle）
- [x] 关联工作中心（Tab 切换 + checkbox）
- [x] 关联工序（Tab 切换 + checkbox）
- [x] 计数列更新验证
- [x] 编辑权限组
- [x] 删除权限组

### Testing — 用户权限
- [x] 列表加载
- [x] 新建用户权限（权限组选择器 + 用户选择器）
- [x] 删除用户权限

### Cleanup
- [x] afterEach 清理测试数据（反向依赖：用户权限 → 权限组）
- [x] 测试可重复执行

## Verification Record
- **Date**: 2026-04-22
- **Status**: PASSED
- **Tests**: 10/10 passed
- **Evidence**: `evidence/playwright-report/`, `evidence/test-results.json`
- **Bug Fixes**: Fixed `group.groupName` -> `group.name` and `user.id` -> `user.userId` in UserDataPermissionList.vue
