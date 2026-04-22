# Checklist: feat-e2e-equipment

## Completion Checklist

### Development
- [x] 3 个 spec 文件编写完成（equipment-type/equipment-model/equipment-ledger）
- [x] 复用 auth fixture 和公共工具函数
- [x] 代码自测通过

### Code Quality
- [x] 测试命名规范统一
- [x] 测试数据隔离（E2E_EQ_ 前缀）
- [x] 无硬编码等待

### Testing — 设备类型
- [x] 列表加载
- [x] 新建设备类型
- [x] 必填校验
- [x] 编辑（编码只读）
- [x] 删除

### Testing — 设备型号
- [x] 列表加载
- [x] 新建型号（关联设备类型选择器）
- [x] 编辑（编码只读）
- [x] 删除

### Testing — 设备台账
- [x] 列表加载
- [x] 新建设备（级联选择器：类型→型号）
- [x] 运行状态/管理状态枚举值选择
- [x] 工厂选择 + 制造商输入 + 日期选择器
- [x] 级联选择器联动验证（选类型后型号过滤）
- [x] 编辑（编码只读）
- [x] 删除

### Cleanup
- [x] afterEach 清理测试数据（反向依赖：台账 → 型号 → 类型）
- [x] 测试可重复执行

## Verification Record

| Date | Status | Tests | Evidence |
|------|--------|-------|----------|
| 2026-04-22 | PASSED | 14/14 passed (1.6m) | evidence/playwright-report/ |
