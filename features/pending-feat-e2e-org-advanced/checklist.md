# Checklist: feat-e2e-org-advanced

## Completion Checklist

### Development
- [ ] 2 个 spec 文件编写完成（department/shift-schedule）
- [ ] 正确导入并复用 auth fixture
- [ ] 代码自测通过

### Code Quality
- [ ] 测试命名规范统一
- [ ] 测试数据隔离（E2E_ORG_ 前缀）
- [ ] 无硬编码等待

### Testing — 部门管理
- [ ] 列表加载（树形结构）
- [ ] 新建顶级部门（关联工厂）
- [ ] 新建子部门（选择上级，层级验证）
- [ ] 编辑部门
- [ ] 删除子部门

### Testing — 班制班次
- [ ] 列表加载
- [ ] 新建班制
- [ ] 管理班次（嵌套对话框：编码/名称/时间/跨天）
- [ ] 删除班制

### Cleanup
- [ ] afterEach 清理测试数据
- [ ] 测试可重复执行
