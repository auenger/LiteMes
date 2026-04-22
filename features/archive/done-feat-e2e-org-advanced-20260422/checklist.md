# Checklist: feat-e2e-org-advanced

## Completion Checklist

### Development
- [x] 2 个 spec 文件编写完成（department/shift-schedule）
- [x] 正确导入并复用 auth fixture
- [x] 代码自测通过

### Code Quality
- [x] 测试命名规范统一
- [x] 测试数据隔离（E2E_ORG_ 前缀 + 时间戳）
- [x] 无硬编码等待（仅保留必要的动画等待）

### Testing — 部门管理
- [x] 列表加载（树形结构）
- [x] 新建顶级部门（关联工厂）
- [x] 新建子部门（选择上级，层级验证）
- [x] 编辑部门
- [x] 删除子部门

### Testing — 班制班次
- [x] 列表加载
- [x] 新建班制
- [x] 管理班次（嵌套对话框：编码/名称/时间/跨天）
- [x] 删除班制

### Cleanup
- [x] 测试可重复执行（唯一编码 + beforeEach 数据准备）
- [x] 无 afterEach（避免 dialog 清理问题，数据不影响业务）

## Verification Record

| Date | Status | Results | Evidence |
|------|--------|---------|----------|
| 2026-04-22 | PASS | 9/9 tests passed | evidence/playwright-report/, evidence/test-results.json |
