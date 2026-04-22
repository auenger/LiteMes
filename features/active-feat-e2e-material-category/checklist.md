# Checklist: feat-e2e-material-category

## Completion Checklist

### Development
- [x] 1 个 spec 文件编写完成（material-category）
- [x] 正确导入并复用 auth fixture
- [x] 代码自测通过

### Code Quality
- [x] 测试命名规范统一
- [x] 测试数据隔离（E2E_MC_ 前缀 + 唯一时间戳）
- [x] 无硬编码等待

### Testing — 物料分类
- [x] 列表加载（树形结构）
- [x] 新建顶级分类
- [x] 新建子分类（层级验证）
- [x] 编辑分类名称
- [x] 删除子分类

### Cleanup
- [x] 每个测试使用独立时间戳数据
- [x] 测试可重复执行

## Verification Record

| Date | Status | Results | Evidence |
|------|--------|---------|----------|
| 2026-04-22T23:42 | PASSED | 5/5 tests pass | evidence/verification-report.md |
