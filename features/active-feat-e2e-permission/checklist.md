# Checklist: feat-e2e-permission

## Completion Checklist

### Development
- [ ] 2 个 spec 文件编写完成（permission-group/user-permission）
- [ ] 复用 auth fixture 和公共工具函数
- [ ] 代码自测通过

### Code Quality
- [ ] 测试命名规范统一
- [ ] 测试数据隔离（E2E_PERM_ 前缀）
- [ ] 无硬编码等待

### Testing — 权限组管理
- [ ] 列表加载（含工厂数/工作中心数/工序数列）
- [ ] 新建权限组（名称 + 备注）
- [ ] 名称必填校验
- [ ] 关联工厂（Tab 切换 + 多选 checkbox + 全选 toggle）
- [ ] 关联工作中心（Tab 切换 + checkbox）
- [ ] 关联工序（Tab 切换 + checkbox）
- [ ] 计数列更新验证
- [ ] 编辑权限组
- [ ] 删除权限组

### Testing — 用户权限
- [ ] 列表加载
- [ ] 新建用户权限（权限组选择器 + 用户选择器）
- [ ] 删除用户权限

### Cleanup
- [ ] afterEach 清理测试数据（反向依赖：用户权限 → 权限组）
- [ ] 测试可重复执行
