# Checklist: feat-e2e-supply-chain

## Completion Checklist

### Development
- [x] 2 个 spec 文件编写完成（customer/supplier）
- [x] 复用 auth fixture 和公共工具函数
- [x] 代码自测通过

### Code Quality
- [x] 测试命名规范统一
- [x] 测试数据隔离（E2E_SC_ 前缀）
- [x] 无硬编码等待

### Testing — 客户管理
- [x] 列表加载
- [x] 新建客户（编码/名称/类型选择）
- [x] 必填校验
- [x] 编辑（编码只读）
- [x] 删除

### Testing — 供应商管理
- [x] 列表加载
- [x] 新建供应商（编码/名称）
- [x] 必填校验
- [x] 编辑（编码只读）
- [x] 删除

### Cleanup
- [x] afterEach 清理测试数据
- [x] 测试可重复执行

## Verification Record

| Date | Status | Summary |
|------|--------|---------|
| 2026-04-22 | PASSED | 10/10 E2E tests passed (47.1s). All Gherkin scenarios verified. Evidence: evidence/verification-report.md |
